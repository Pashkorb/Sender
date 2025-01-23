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
            String userHome = System.getProperty("user.home");
            Path appDataPath = Paths.get(userHome, "AppData", "Roaming", "FastMarking");

            if (!Files.exists(appDataPath)) {
                Files.createDirectories(appDataPath);
            }

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

    // Общий метод для логирования
    public  void log(String message) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String logMessage = "[" + timestamp + "] " + message + "\n";
            Files.write(logFilePath, logMessage.getBytes(), StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("Ошибка записи в лог: " + e.getMessage());
        }
    }

    // Специализированные методы
    public void logLogin(String username) {
        log("Вход в систему: Пользователь '" + username + "'");
    }

    public void logLogout(String username) {
        log("Выход из системы: Пользователь '" + username + "'");
    }

    public void logError(String error) {
        log("ОШИБКА: " + error);
    }
}