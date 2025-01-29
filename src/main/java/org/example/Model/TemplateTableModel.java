package org.example.Model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TemplateTableModel extends AbstractTableModel {
    private List<TemplateField> fields = new ArrayList<>();
    private final String[] columns = {"Номер", "Название поля", "Текст"};
    @Override
    public int getRowCount() {
        return fields.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        TemplateField field = fields.get(row);
        switch (column) {
            case 0: field.setNumber((Integer) value); break;
            case 1: field.setFieldName((String) value); break;
            case 2: field.setText((String) value); break;
        }
        fireTableCellUpdated(row, column);
    }

    public void addField(TemplateField field) {
        fields.add(field);
        fireTableRowsInserted(fields.size()-1, fields.size()-1);
    }

    public void setFields(List<TemplateField> newFields) {
        this.fields = new ArrayList<>(newFields);
        fireTableDataChanged();
    }
    public TemplateField getFieldAt(int row) {
        return fields.get(row);
    }
    @Override
    public boolean isCellEditable(int row, int column) {
        // Разрешаем редактирование всех колонок
        return true;
    }
    @Override
    public Object getValueAt(int row, int column) {
        TemplateField field = fields.get(row);
        return switch (column) {
            case 0 -> field.getNumber();
            case 1 -> field.getFieldName();
            case 2 -> field.getText();
            default -> null;
        };
    }
    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    public void removeField(int row) {
        if (row >= 0 && row < fields.size()) {
            fields.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }
    @Override
    public Class<?> getColumnClass(int column) {
        return switch (column) {
            case 0 -> Integer.class;
            default -> String.class;
        };
    }

}