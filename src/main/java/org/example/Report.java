package org.example;

import org.example.Model.LoginReportTableModel;
import org.example.Model.PrintReportMessagTableModel;
import org.example.Model.PrinterReportTableModel;
import org.example.Service.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Report extends JPanel {
    private JTable table1;
    private JTable table2;
    private JTable table3;
    private MainFrame parent;
    private JPanel mainPanel;
    private JButton button1;
    private JButton button2;
    private JButton button3;

    public Report(MainFrame parent) {
        this.parent = parent;
        add(mainPanel);
        // Убираем границы и фон кнопок
        button1.setBorderPainted(false);
        button1.setContentAreaFilled(false);
        button1.setFocusPainted(false);
        button1.setText(""); // Убираем текст, если он есть

        button2.setBorderPainted(false);
        button2.setContentAreaFilled(false);
        button2.setFocusPainted(false);
        button2.setText("");

        button3.setBorderPainted(false);
        button3.setContentAreaFilled(false);
        button3.setFocusPainted(false);
        button3.setText("");

        mainPanel.setPreferredSize(new Dimension(1920, 1080));

        button1.addActionListener(e -> parent.showSettings());
        button2.addActionListener(e -> parent.showGeneral());
        button3.addActionListener(e -> parent.showSupport());


        loadPrinterData();
        loadLoginData();
        printerMessageData();

    }

    private void printerMessageData() {
        PrintReportMessagTableModel model = new PrintReportMessagTableModel();

        table3.setModel(model);
        adjustMessTableColumns(table3);
    }

    private void loadPrinterData() {
        PrinterReportTableModel model = new PrinterReportTableModel();

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Наименование, Серия, ЧасыРаботы FROM Принтеры")) {

            while (rs.next()) {
                model.addRow(
                        rs.getString("Наименование"),
                        rs.getString("Серия"),
                        rs.getInt("ЧасыРаботы")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка загрузки данных принтеров", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        table1.setModel(model);
        adjustTableColumns(table1);
    }

    private void adjustTableColumns(JTable table) {
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.setRowHeight(25);
    }


    private void loadLoginData() {
        LoginReportTableModel model = new LoginReportTableModel();

        String sql = "SELECT u.Фио, j.ТипСобытия, j.ДатаВремя " +
                "FROM ЖурналАвторизаций j " +
                "JOIN Пользователи u ON j.Пользователь_id = u.id " +
                "ORDER BY j.ДатаВремя DESC"; // Сортируем по дате

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            String lastLogin = null;
            String lastLogout = null;
            String currentUser = null;

            while (rs.next()) {
                String fio = rs.getString("Фио");
                String eventType = rs.getString("ТипСобытия");
                String eventTime = rs.getString("ДатаВремя");

                if (currentUser == null || !currentUser.equals(fio)) {
                    // Новый пользователь, сбрасываем данные
                    currentUser = fio;
                    lastLogin = null;
                    lastLogout = null;
                }

                if ("Вход".equals(eventType)) {
                    lastLogin = eventTime;
                    // Если есть выход, добавляем запись
                    if (lastLogout != null) {
                        model.addRow(fio, lastLogin, lastLogout);
                        lastLogout = null; // Сбрасываем выход после добавления записи
                    }
                } else if ("Выход".equals(eventType)) {
                    lastLogout = eventTime;
                    // Если есть вход, добавляем запись
                    if (lastLogin != null) {
                        model.addRow(fio, lastLogin, lastLogout);
                        lastLogin = null; // Сбрасываем вход после добавления записи
                    }
                }
            }

            // Добавляем последнюю запись, если остались незакрытые данные
            if (lastLogin != null) {
                model.addRow(currentUser, lastLogin, "Не зарегистрирован");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка загрузки журнала входов", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        table2.setModel(model);
        adjustLoginTableColumns(table2);
    }
    private void adjustLoginTableColumns(JTable table) {
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.setRowHeight(25);
    }
    private void adjustMessTableColumns(JTable table) {
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.setRowHeight(25);
    }

}