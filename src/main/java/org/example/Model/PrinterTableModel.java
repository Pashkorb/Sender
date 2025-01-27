package org.example.Model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PrinterTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Наименование", "Серия", "Количество символов", "Часов работы до ТО"};
    private final List<Object[]> data = new ArrayList<>(); // [id, name, series, symbols, hours]
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
        switch (columnIndex) {
            case 0:
            case 1: return String.class;
            case 2:
            case 3: return Integer.class;
            default: return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data.get(row)[col + 1]; // Пропускаем id в первом элементе массива
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        try {
            // Преобразуем ввод к нужным типам
            switch (col) {
                case 2:
                case 3:
                    data.get(row)[col + 1] = Integer.parseInt(value.toString());
                    break;
                default:
                    data.get(row)[col + 1] = value;
            }
        } catch (NumberFormatException e) {
            data.get(row)[col + 1] = 0; // Значение по умолчанию при ошибке
        }
        modified.set(row, true);
        fireTableCellUpdated(row, col);
    }

    public void addRow(Object[] row) {
        data.add(row);
        modified.add(false);
        fireTableRowsInserted(data.size()-1, data.size()-1);
    }

    public Object[] getRow(int row) {
        return data.get(row);
    }
}