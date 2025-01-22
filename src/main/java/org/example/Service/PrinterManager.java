package org.example.Service;


import com.fazecast.jSerialComm.SerialPort;
import java.util.HashMap;
import java.util.Map;

public class PrinterManager {
    // Статические переменные для хранения настроек принтера
    private static String printerName;
    private static String printerSeries;
    private static int characterCount;
    private static int workingHours;

    private static SerialPort serialPort; // Для работы с COM-портом
    private static String ipAddress; // Для работы с Ethernet
    private static int printerPort; // Порт принтера (для Ethernet)
    private static boolean isSerialConnection = true; // По умолчанию используется Serial

    // Логирование
    private static void log(String message) {
        System.out.println("[PrinterManager] " + message);
    }

    // Установка настроек принтера
    public static void setPrinterSettings(String name, String series, int chars, int hours) {
        printerName = name;
        printerSeries = series;
        characterCount = chars;
        workingHours = hours;
        log("Настройки принтера обновлены: " + name + ", " + series + ", " + chars + " символов, " + hours + " часов");
    }

    // Получение текущих настроек принтера
    public static Map<String, String> getPrinterSettings() {
        Map<String, String> settings = new HashMap<>();
        settings.put("name", printerName);
        settings.put("series", printerSeries);
        settings.put("chars", String.valueOf(characterCount));
        settings.put("hours", String.valueOf(workingHours));
        log("Получены настройки принтера: " + settings);
        return settings;
    }

    // Установка типа соединения (Serial или Ethernet)
    public static void setConnectionType(boolean isSerial) {
        isSerialConnection = isSerial;
        log("Тип соединения установлен: " + (isSerial ? "Serial" : "Ethernet"));
    }

    // Открытие порта (Serial или Ethernet)
    public static void openPort(String portName, String ip, int port) {
        if (isSerialConnection) {
            if (serialPort != null && serialPort.isOpen()) {
                log("Порт уже открыт: " + portName);
                return;
            }
            serialPort = SerialPort.getCommPort(portName);
            if (serialPort.openPort()) {
                serialPort.setBaudRate(9600);
                serialPort.setNumDataBits(8);
                serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
                serialPort.setParity(SerialPort.NO_PARITY);
                log("Serial порт открыт: " + portName);
            } else {
                log("Ошибка открытия Serial порта: " + portName);
            }
        } else {
            ipAddress = ip;
            printerPort = port;
            log("Ethernet соединение установлено: " + ip + ":" + port);
        }
    }

    // Закрытие порта
    public static void closePort() {
        if (isSerialConnection) {
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
                log("Serial порт закрыт.");
            } else {
                log("Serial порт уже закрыт.");
            }
        } else {
            log("Ethernet соединение закрыто.");
        }
    }

    // Отправка данных на принтер
    public static void sendData(byte[] data) {
        if (isSerialConnection) {
            if (serialPort == null || !serialPort.isOpen()) {
                log("Ошибка: Serial порт не открыт.");
                return;
            }
            serialPort.writeBytes(data, data.length);
            log("Данные отправлены через Serial порт.");
        } else {
            // Логика для Ethernet
            log("Данные отправлены через Ethernet: " + new String(data));
        }
    }

    // Получение статуса принтера
    public static String getPrinterStatus() {
        if (isSerialConnection) {
            return "Serial порт " + (serialPort != null && serialPort.isOpen() ? "открыт" : "закрыт");
        } else {
            return "Ethernet соединение: " + ipAddress + ":" + printerPort;
        }
    }

    public static boolean isConnectionOpen() {
        if (isSerialConnection) {
            return serialPort != null && serialPort.isOpen();
        } else {
            return ipAddress != null && !ipAddress.isEmpty();
        }
    }
}