package org.example.Model;

import org.example.Service.UserRole;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class UserTableModel extends AbstractTableModel {
    private final String[] columnNames = {"ФИО", "Роль", "Логин", "Пароль", "Доступ"};
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
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data.get(row)[col]; // Возвращаем объект как есть
    }
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 4) return Boolean.class;
        return String.class; // Все колонки обрабатываем как String
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (column == 1 && value instanceof UserRole) {
            // Сохраняем название роли вместо объекта
            data.get(row)[column] = ((UserRole) value).getRoleName();
        } else {
            data.get(row)[column] = value;
        }
        fireTableCellUpdated(row, column);
    }

    public void addRow(Object[] row) {
        data.add(row);
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
}