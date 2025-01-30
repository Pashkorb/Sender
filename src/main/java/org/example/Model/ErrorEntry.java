package org.example.Model;

import java.time.LocalDateTime;

public class ErrorEntry {
    private final int id;
    private final String errorText;
    private final LocalDateTime timestamp;

    public ErrorEntry(String errorText, LocalDateTime timestamp) {
        this.id = -1; // Или можно генерировать уникальный ID, если нужно
        this.errorText = errorText;
        this.timestamp = timestamp;
    }

    // Геттеры
    public int getId() { return id; }
    public String getErrorText() { return errorText; }
    public LocalDateTime getTimestamp() { return timestamp; }
}