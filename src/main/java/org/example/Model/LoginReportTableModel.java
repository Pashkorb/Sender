package org.example.Model;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LoginReportTableModel extends AbstractTableModel {
    private final String[] columnNames = {"ФИО сотрудника", "Дата входа", "Дата выхода"};
    private final List<Object[]> data = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object[] record = data.get(row);
        return switch (col) {
            case 0 -> record[0]; // ФИО
            case 1 -> formatDateTime((String) record[1]); // Дата входа
            case 2 -> formatDateTime((String) record[2]); // Дата выхода
            default -> null;
        };
    }

    private String formatDateTime(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.trim().isEmpty()) {
            return "Не зарегистрирован";
        }
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, inputFormatter);
            return dateTime.format(formatter);
        } catch (Exception e) {
            System.err.println("Ошибка парсинга даты: " + isoDateTime);
            return "Не зарегистрирован";
        }
    }

    public void addRow(String fio, String loginTime, String logoutTime) {
        data.add(new Object[]{fio, loginTime, logoutTime});
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }
}