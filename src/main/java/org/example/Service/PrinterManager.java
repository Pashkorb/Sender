package org.example.Service;

import com.fazecast.jSerialComm.*;
import org.example.Coder;
import org.example.Model.PrinterDataListener;
import org.example.PrinterCommand;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrinterManager {
    private static SerialPort serialPort;
    private static Socket ethernetSocket;
    private static BufferedReader ethernetReader;
    private static final List<PrinterDataListener> listeners = new ArrayList<>();
    private static Thread ethernetReadThread;
    private static final AtomicBoolean running = new AtomicBoolean(false);

    // Регистрация слушателей
    public static synchronized void addDataListener(PrinterDataListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static synchronized void removeDataListener(PrinterDataListener listener) {
        listeners.remove(listener);
    }

    private static synchronized void notifyDataReceived(String data) {
        Logger.getInstance().log("[RX] " + data);
        listeners.forEach(listener -> listener.onDataReceived(data));
    }

    private static synchronized void notifyStatus(String status) {
        listeners.forEach(listener -> listener.onStatusUpdate(status));
    }

    // Управление портами
    public static void openCOMPort(String portName) throws Exception {
        closePort();
        serialPort = SerialPort.getCommPort(portName);

        if (!serialPort.openPort()) {
            throw new IOException("Не удалось открыть COM-порт: " + portName);
        }

        serialPort.setBaudRate(9600);
        serialPort.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                100,
                0
        );

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
                    if (numRead > 0) {
                        notifyDataReceived(new String(buffer, 0, numRead, StandardCharsets.US_ASCII));
                    }
                } catch (Exception e) {
                    notifyStatus("Ошибка чтения COM: " + e.getMessage());
                }
            }
        });
        running.set(true);
        notifyStatus("COM-порт открыт: " + portName);
    }

    public static void openEthernetPort(String ip, int port) throws Exception {
        closePort();
        ethernetSocket = new Socket();
        ethernetSocket.connect(new InetSocketAddress(ip, port), 3000);

        if (!ethernetSocket.isConnected()) {
            throw new IOException("Не удалось подключиться к " + ip + ":" + port);
        }

        ethernetReader = new BufferedReader(
                new InputStreamReader(ethernetSocket.getInputStream(), StandardCharsets.US_ASCII)
        );

        running.set(true);
        ethernetReadThread = new Thread(() -> {
            try {
                while (running.get()) {
                    String data = ethernetReader.readLine();
                    if (data != null) {
                        notifyDataReceived(data);
                    }
                }
            } catch (Exception e) {
                if (running.get()) {
                    notifyStatus("Ошибка чтения Ethernet: " + e.getMessage());
                }
            }
        });
        ethernetReadThread.start();
        notifyStatus("Ethernet подключение установлено: " + ip + ":" + port);
    }

    public static synchronized void closePort() {
        running.set(false);

        if (serialPort != null && serialPort.isOpen()) {
            serialPort.removeDataListener();
            serialPort.closePort();
            serialPort = null;
            notifyStatus("COM-порт закрыт");
        }

        if (ethernetSocket != null && !ethernetSocket.isClosed()) {
            try {
                ethernetSocket.close();
            } catch (IOException e) {
                Logger.getInstance().logError("Ошибка закрытия сокета: " + e.getMessage());
            }
            ethernetSocket = null;
            notifyStatus("Ethernet подключение закрыто");
        }

        if (ethernetReadThread != null) {
            try {
                ethernetReadThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            ethernetReadThread = null;
        }
    }

    // Отправка данных
    public static synchronized void sendData(byte[] data) throws IOException {
        if (serialPort != null && serialPort.isOpen()) {
            int result = serialPort.writeBytes(data, data.length);
            if (result == -1) {
                throw new IOException("Ошибка записи в COM-порт");
            }
        } else if (ethernetSocket != null && ethernetSocket.isConnected()) {
            OutputStream out = ethernetSocket.getOutputStream();
            out.write(data);
            out.flush();
        } else {
            throw new IOException("Нет активного подключения");
        }
    }

    public static void sendStopCommand() {
        try {
            Coder coder = new Coder();
            PrinterCommand command = new PrinterCommand("03", "");
            String formattedCommand = coder.prepareCommandForSending(command);
            sendData(formattedCommand.getBytes(StandardCharsets.US_ASCII));
        } catch (Exception e) {
            notifyStatus("Ошибка отправки STOP: " + e.getMessage());
        }
    }

    public static synchronized boolean isConnectionOpen() {
        return (serialPort != null && serialPort.isOpen()) ||
                (ethernetSocket != null && ethernetSocket.isConnected());
    }
}