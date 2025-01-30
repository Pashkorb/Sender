package org.example.Service;

import org.example.Service.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrintTaskManager {

    // Метод для записи задания печати в базу данных
    public static void logPrintTask(int userId, int printerId, String message) {
        String sql = "INSERT INTO ЗаданияПечати (Пользователь_id, Принтер_id, ДатаВремяПечати, Сообщение) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Устанавливаем параметры
            pstmt.setInt(1, userId);
            pstmt.setInt(2, printerId);
            pstmt.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstmt.setString(4, message);

            // Выполняем запрос
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Ошибка записи задания печати: " + ex.getMessage());
        }
    }
}