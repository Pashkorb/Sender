package org.example.Model;


import javax.swing.table.AbstractTableModel;
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
            case 0 -> error.getId();
            case 1 -> error.getErrorText();
            case 2 -> error.getTimestamp().format(formatter);
            default -> null;
        };
    }

    public void addError(ErrorEntry error) {
        errors.add(error);
        fireTableRowsInserted(errors.size()-1, errors.size()-1);
    }

    public void loadErrors() {
        // Реализация загрузки из БД
    }
}