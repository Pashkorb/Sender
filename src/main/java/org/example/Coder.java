package org.example;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class Coder {

    // Подготовка команды для печати
    public PrinterCommand preparePrintCommand(List<String> printTasks, String commandCode) throws Exception {
        if (printTasks == null || printTasks.isEmpty()) {
            throw new Exception("No data to print");
        }

        List<String> dataList = new java.util.ArrayList<>();

        // Collect data in reverse order
        for (int index = printTasks.size() - 1; index >= 0; index--) {
            String text = printTasks.get(index);
            if (!text.isEmpty() || !dataList.isEmpty()) {
                dataList.add(0, text); // Insert at the beginning to reverse
            }
        }

        if (dataList.isEmpty()) {
            throw new Exception("No data to print");
        }

        // Prepare the command data
        StringBuilder commandData = new StringBuilder();
        for (int index = 0; index < dataList.size(); index++) {
            String line = dataList.get(index);
            String hexLine = "X" + index + line;
            String hexString = StrToHex(hexLine) + "001F";
            commandData.append(hexString);
        }

        return new PrinterCommand(commandCode, commandData.toString());
    }

    // Подготовка команды для отправки
    public String prepareCommandForSending(PrinterCommand command) {
        StringBuilder result = new StringBuilder();

        // Function Code
        String functionCode = command.getFunctionCode(); // Используем код функции из объекта
        result.append(functionCode);

        // Memo ID
        result.append("00"); // Согласно документации - "00" or "01", unnecessary

        // Length
        int dataLength = command.getData().length(); // Используем данные из объекта
        result.append(WordToHex(dataLength));

        // Data
        result.append(command.getData()); // Используем данные из объекта

        // Checksum
        int checksum = 0;
        for (char c : result.toString().toCharArray()) {
            checksum += c;
        }
        checksum = checksum - (checksum / 65536) * 65536;
        checksum = (~checksum) + 1; // Two's complement
        String checksumHex = WordToHex(checksum & 0xFFFF);

        result.append(checksumHex);

        // SOI and EOI
        char SOI = (char) 0x7E;
        char EOI = (char) 0x0D;
        result.insert(0, SOI);
        result.append(EOI);

        return result.toString();
    }

    // Hex conversion functions
    private static String StrToHex(String text) {
        System.out.println("[INFO] Начало преобразования строки в HEX: " + text);

        // Преобразуем строку в массив байтов с использованием кодировки UTF-16
        byte[] bytes = text.getBytes(StandardCharsets.UTF_16BE); // UTF-16BE - big-endian

        // Преобразуем каждый байт в HEX-формат
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(ByteToHex(b));
        }

        System.out.println("[INFO] Преобразование строки в HEX завершено: " + result);
        return result.toString();
    }

    private static String WordToHex(int value) {
        int highByte = (value >> 8) & 0xFF;
        int lowByte = value & 0xFF;
        return ByteToHex(highByte) + ByteToHex(lowByte);
    }

    private static String ByteToHex(int value) {
        String hexString = "0123456789ABCDEF";
        int high = (value >> 4) & 0xF;
        int low = value & 0xF;
        return String.valueOf(hexString.charAt(high)) + hexString.charAt(low);
    }
}