package org.example.Model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PrinterReportTableModel extends AbstractTableModel { // Добавляем наследование
    private final String[] columnNames = {"Принтер", "Отработано", "Осталось до ТО"};
    private final List<Object[]> data = new ArrayList<>();

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
        Object[] printer = data.get(row);
        return switch (col) {
            case 0 -> printer[0] + " (" + printer[1] + ")";
            case 1 -> printer[2] + " ч.";
            case 2 -> "0 ч.";
            default -> null;
        };
    }

    public void addRow(String name, String serial, int hours) {
        data.add(new Object[]{name, serial, hours});
        fireTableRowsInserted(data.size()-1, data.size()-1); // Теперь метод доступен
    }
}