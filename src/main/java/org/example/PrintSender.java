package org.example;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
public class PrintSender {

    private static SerialPort serialPort; // Статический объект порта
    private static String portName; // Имя порта
    // Установка имени порта
    public static void setPortName(String name) {
        portName = name;
        System.out.println("[INFO] Port name set to: " + portName);
    }
    public static void send(byte[] dataBytes) {
        if (serialPort == null || !serialPort.isOpen()) {
            System.out.println("[ERROR] Port is not open.");
            throw new IllegalStateException("Port is not open");
        }

        serialPort.writeBytes(dataBytes, dataBytes.length);
        System.out.println("[INFO] Data sent successfully.");
    }
    // Открытие порта
    public static void openPort() {
        if (serialPort != null && serialPort.isOpen()) {
            System.out.println("[WARN] Port is already open.");
            return;
        }
        serialPort = SerialPort.getCommPort(portName); // Получаем объект порта
        System.out.println("[INFO] Attempting to open port: " + portName);
        serialPort.openPort(); // Открываем порт
        serialPort.setBaudRate(9600); // Устанавливаем скорость передачи данных
        serialPort.setNumDataBits(8); // Устанавливаем количество бит данных
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT); // Устанавливаем количество стоп-битов
        serialPort.setParity(SerialPort.NO_PARITY); // Устанавливаем отсутствие паритета
        System.out.println("[INFO] Port " + portName + " opened successfully.");
    }

    // Отправка данных

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b)); // Преобразуем каждый байт в hex-формат
        }
        return hexString.toString();
    }
    // Метод для преобразования строки hex-символов в массив байтов
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    // Закрытие порта
    public static void closePort() {
        if (serialPort != null && serialPort.isOpen()) {
            System.out.println("[INFO] Closing port: " + portName);
            serialPort.closePort(); // Закрываем порт
            System.out.println("[INFO] Port " + portName + " closed successfully.");
        } else {
            System.out.println("[WARN] Port is already closed.");

        }
    }

    public static void setSerialPort(SerialPort selectedPort) {
        serialPort = selectedPort;

    }
}

