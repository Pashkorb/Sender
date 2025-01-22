package org.example.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static Logger instance;
    private Path logFilePath;

    private Logger() {
        try {
            // Получаем путь к папке APPDATA
            String userHome = System.getProperty("user.home");
            Path appDataPath = Paths.get(userHome, "AppData", "Roaming", "FastMarking");

            // Создаём папку, если она не существует
            if (!Files.exists(appDataPath)) {
                Files.createDirectories(appDataPath);
            }

            // Создаём файл логов
            logFilePath = appDataPath.resolve("ЛОГИ.txt");
            if (!Files.exists(logFilePath)) {
                Files.createFile(logFilePath);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при инициализации логгера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        if (logFilePath == null) {
            System.err.println("Ошибка: путь к файлу логов не инициализирован.");
            return;
        }
        try {
            // Добавляем временную метку к сообщению
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String logMessage = "[" + timestamp + "] " + message + "\n";

            // Записываем сообщение в файл
            Files.write(logFilePath, logMessage.getBytes(), StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Ошибка при записи в файл логов: " + e.getMessage());
            e.printStackTrace();
        }
    }
}