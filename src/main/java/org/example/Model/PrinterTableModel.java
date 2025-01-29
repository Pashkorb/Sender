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
            modified.set(row, true);
            fireTableCellUpdated(row, col);
        } catch (NumberFormatException e) {
            // Можно добавить обработку ошибки ввода
            data.get(row)[col + 1] = 0;
        }
    }

    public void addRow(Object[] row) {
        // Гарантируем правильный формат строки: [id, name, series, symbols, hours]
        Object[] formattedRow = new Object[5];
        System.arraycopy(row, 0, formattedRow, 0, Math.min(row.length, 5));
        data.add(formattedRow);
        modified.add(false);
        fireTableRowsInserted(data.size()-1, data.size()-1);
    }

    public Object[] getRow(int row) {
        return data.get(row);
    }

    public void removeRow(int row) {
        if (row >= 0 && row < data.size()) {
            data.remove(row);
            modified.remove(row);
            fireTableRowsDeleted(row, row);
        }
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

    // Дополнительные методы для работы с данными
    public void setModified(int row, boolean status) {
        if (row >= 0 && row < modified.size()) {
            modified.set(row, status);
        }
    }

    public boolean isModified(int row) {
        return row >= 0 && row < modified.size() && modified.get(row);
    }
}