package org.example.Model;



import java.time.LocalDateTime;

public class ErrorEntry {
    private int id;
    private final String errorText;
    private final LocalDateTime timestamp;

    public ErrorEntry(String errorText, LocalDateTime timestamp) {
        this.id = id;
        this.errorText = errorText;
        this.timestamp = timestamp;
    }

    // Геттеры
    public int getId() { return id; }
    public String getErrorText() { return errorText; }
    public LocalDateTime getTimestamp() { return timestamp; }
}