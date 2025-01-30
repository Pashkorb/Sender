package org.example.Model;

import org.example.Service.DatabaseManager;

import javax.swing.table.AbstractTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ErrorTableModel extends AbstractTableModel {
    private final List<ErrorEntry> errors = new ArrayList<>();
    private final String[] columnNames = {"Текст ошибки", "Дата и время"};
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public int getRowCount() {
        return errors.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ErrorEntry error = errors.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> error.getErrorText();
            case 1 -> error.getTimestamp().format(formatter);
            default -> null;
        };
    }

    public void addError(ErrorEntry error) {
        errors.add(error);
        fireTableRowsInserted(errors.size() - 1, errors.size() - 1);
    }

    public void loadErrors() {
        errors.clear();
        String sql = "SELECT Текст, ДатаВремя FROM Ошибки ORDER BY id DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ErrorEntry error = new ErrorEntry(
                        rs.getString("Текст"),
                        LocalDateTime.parse(rs.getString("ДатаВремя"),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                );
                errors.add(error);
            }
            fireTableDataChanged(); // Обновляем таблицу после загрузки данных
        } catch (Exception e) {
            e.printStackTrace(); // Логируем ошибку
        }
    }
}