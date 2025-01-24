package org.example.Service;


import com.fazecast.jSerialComm.SerialPort;
import java.util.HashMap;
import java.util.Map;

// PrinterManager.java
import com.fazecast.jSerialComm.*;
import org.example.Coder;
import org.example.Model.PrinterDataListener;
import org.example.PrinterCommand;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class PrinterManager {
    private static SerialPort serialPort;
    private static Socket ethernetSocket;
    private static BufferedReader ethernetReader;
    private static List<PrinterDataListener> listeners = new ArrayList<>();
    private static Thread ethernetReadThread;
    private static boolean running = true;

    public static void addDataListener(PrinterDataListener listener) {
        listeners.add(listener);
    }

    public static void removeDataListener(PrinterDataListener listener) {
        listeners.remove(listener);
    }

    private static void notifyDataReceived(String data) {
        System.out.println(data);
        Logger.getInstance().log("[Get] "+data);
        for (PrinterDataListener listener : listeners) {
            listener.onDataReceived(data);
        }
    }

    private static void notifyStatus(String status) {
        for (PrinterDataListener listener : listeners) {
            listener.onStatusUpdate(status);
        }
    }

    public static void openPort(String portName, String ip, int port) throws Exception {
        if (portName != null) {
            openSerialPort(portName);
        } else {
            openEthernetPort(ip, port);
        }
    }

    private static void openSerialPort(String portName) throws Exception {
        serialPort = SerialPort.getCommPort(portName);
        if (!serialPort.openPort()) {
            throw new Exception("Failed to open serial port");
        }

        serialPort.setBaudRate(9600);
        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                try {
                    byte[] buffer = new byte[serialPort.bytesAvailable()];
                    int numRead = serialPort.readBytes(buffer, buffer.length);
                    String data = new String(buffer, 0, numRead, StandardCharsets.US_ASCII);
                    notifyDataReceived(data);
                } catch (Exception e) {
                    notifyStatus("Serial read error: " + e.getMessage());
                }
            }
        });
    }

    private static void openEthernetPort(String ip, int port) throws Exception {
        ethernetSocket = new Socket(ip, port);
        ethernetReader = new BufferedReader(
                new InputStreamReader(ethernetSocket.getInputStream(), StandardCharsets.US_ASCII));

        ethernetReadThread = new Thread(() -> {
            try {
                while (running) {
                    if (ethernetReader.ready()) {


                        String data = ethernetReader.readLine();
                        if (data != null) notifyDataReceived(data);
                    }
                    Thread.sleep(150);
                }
            } catch (Exception e) {
                notifyStatus("Ethernet read error: " + e.getMessage());
            }
        });
        ethernetReadThread.start();
    }

    public static void closePort() {
        running = false;
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
        if (ethernetSocket != null && !ethernetSocket.isClosed()) {
            try {
                ethernetSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendData(byte[] data) throws IOException {
        if (serialPort != null && serialPort.isOpen()) {

            serialPort.writeBytes(data, data.length);
        } else if (ethernetSocket != null && ethernetSocket.isConnected()) {
            OutputStream out = ethernetSocket.getOutputStream();
            out.write(data);
            out.flush();
        }
    }

    public static void sendStopCommand() {
        try {
            Coder coder = new Coder();
            PrinterCommand command = new PrinterCommand("03", ""); // Код функции 03
            String formattedCommand = coder.prepareCommandForSending(command);
            sendData(formattedCommand.getBytes(StandardCharsets.US_ASCII));
        } catch (IOException e) {
            notifyStatus("Ошибка отправки STOP: " + e.getMessage());
        }
    }

    public static boolean isConnectionOpen() {
        if (serialPort != null && serialPort.isOpen()) {
            return true;
        } else if (ethernetSocket != null && ethernetSocket.isConnected()) {
            return true;
        }
        return false;
    }
}