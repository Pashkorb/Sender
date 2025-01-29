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
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1) return UserRole.class; // Указываем тип UserRole
        if (columnIndex == 4) return Boolean.class;
        return String.class;
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
    public void setValueAt(Object value, int row, int col) {
        if (col == 1) {
            // Если значение пришло как строка, преобразуем его в UserRole
            if (value instanceof String) {
                value = UserRole.fromString((String) value);
            }
            data.get(row)[col] = value;
        } else {
            data.get(row)[col] = value;
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
            data.clear();
            modified.clear();
            fireTableRowsDeleted(0, oldSize-1);
        }
    }
}