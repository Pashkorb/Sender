package org.example.Model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PrintReportMessagTableModel extends AbstractTableModel { // Добавляем наследование
    private final String[] columnNames = {"Сотрудник", "Принтер", "Серийный номер", "Дата печати", "Сообщение"};
    private final List<Object[]> data = new ArrayList<>();
    private final List<Boolean> modified = new ArrayList<>();

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
            case 0 -> record[0]; // Сотрудник
            case 1 -> record[1]; // Принтер
            case 2 -> record[2]; // Серийный номер
            case 3 -> record[3]; // Дата печати
            case 4 -> record[4]; // Сообщение
            default -> null;
        };
    }

    public void addRow(String user, String printer, String serial, String date, String message) {
        data.add(new Object[]{user, printer, serial, date, message});
        fireTableRowsInserted(data.size()-1, data.size()-1);
        modified.add(false);
    }

    public void setRowCount(int rowCount) {
        if (rowCount == 0) {
            int oldSize = data.size();
            if (oldSize > 0) { // Добавляем проверку на положительный размер
                data.clear();
                modified.clear();
                fireTableRowsDeleted(0, oldSize - 1);
            }
        }
    }
}