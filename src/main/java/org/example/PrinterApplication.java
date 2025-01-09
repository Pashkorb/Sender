//package org.example;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//
//public class PrinterApplication {
//
//    public static void main(String[] args) {
//        // Example usage
//        try {
//            // Set the serial port name
//            PrintSender.setPortName("COM3");
//
//            // Open the serial port
//            PrintSender.openPort();
//
//            // Read print tasks from a file or any source
//            List<String> printTasks = Files.readAllLines(Paths.get("printTasks.txt"));
//
//            // Set command code
//            String commandCode = "02";
//
//            // Send data to printer
//            Coder.setDataForPrinting(printTasks, commandCode);
//
//            // Close the serial port
//            PrintSender.closePort();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}