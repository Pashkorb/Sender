package org.example;

import org.example.Model.LoginReportTableModel;
import org.example.Model.PrintReportMessagTableModel;
import org.example.Model.PrinterReportTableModel;
import org.example.Service.CurrentUser;
import org.example.Service.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Report extends JPanel {
    private JTable tablePrintersReport;
    private JTable PrintReport;
    private JTable tableAuentificationReport;
    private final MainFrame parent;
    private JPanel mainPanel;
    private JButton buttonHome;
    private JButton buttonSetting;
    private JButton buttonAdmin;
    private JButton buttonPrinter;
    private JButton buttonSupport;
    private JButton buttonReport;
    private JLabel LableName;
    private JButton buttonLogOut;


    public Report(MainFrame parent) {
        this.parent = parent;
        add(mainPanel);

        LableName.setText(CurrentUser.getName());

        buttonHome.addActionListener(e -> parent.showHome());
        buttonSetting.addActionListener(e -> parent.showSettings());
        buttonAdmin.addActionListener(e->parent.showAdmin());
        buttonPrinter.addActionListener(e->parent.showGeneral());
        buttonSupport.addActionListener(e -> parent.showSupport());
        buttonReport.addActionListener(e -> parent.showReport());
        buttonLogOut.addActionListener(e -> parent.logLogout());
        // Убираем границы и фон кнопок

        tablePrintersReport.setModel(new PrinterReportTableModel());
        PrintReport.setModel(new PrintReportMessagTableModel());
        tableAuentificationReport.setModel(new LoginReportTableModel());


        mainPanel.setPreferredSize(new Dimension(1920, 1080));


        // Инициализация моделей
        tablePrintersReport.setModel(new PrinterReportTableModel());
        PrintReport.setModel(new PrintReportMessagTableModel());
        tableAuentificationReport.setModel(new LoginReportTableModel());

        // Загрузка данных
        loadPrintersReport();
        loadPrintReport();
        loadLoginReport();
    }
    private void loadPrintersReport() {
        PrinterReportTableModel model = (PrinterReportTableModel) tablePrintersReport.getModel();
        model.setRowCount(0); // Очистка предыдущих данных

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
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Ошибка загрузки принтеров: " + ex.getMessage());
        }
    }

    private void loadPrintReport() {
        PrintReportMessagTableModel model = (PrintReportMessagTableModel) PrintReport.getModel();
        model.setRowCount(0);

        String sql = "SELECT u.Фио, p.Наименование, p.Серия, z.ДатаВремяПечати, z.Сообщение " +
                "FROM ЗаданияПечати z " +
                "JOIN Пользователи u ON z.Пользователь_id = u.id " +
                "JOIN Принтеры p ON z.Принтер_id = p.id";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                model.addRow(
                        rs.getString("Фио"),
                        rs.getString("Наименование"),
                        rs.getString("Серия"),
                        formatDateTime(rs.getString("ДатаВремяПечати")),
                        rs.getString("Сообщение")
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Ошибка загрузки отчетов печати: " + ex.getMessage());
        }
    }

    private void loadLoginReport() {
        LoginReportTableModel model = (LoginReportTableModel) tableAuentificationReport.getModel();
        model.setRowCount(0);

        String sql = "SELECT u.Фио, j.ТипСобытия, j.ДатаВремя "
                + "FROM ЖурналАвторизаций j "
                + "JOIN Пользователи u ON j.Пользователь_id = u.id "
                + "ORDER BY u.Фио, j.ДатаВремя";

        String currentUser = null;
        String lastLogin = null;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String user = rs.getString("Фио");
                String eventType = rs.getString("ТипСобытия");
                String dateTime = rs.getString("ДатаВремя"); // Убрали formatDateTime()

                if (eventType.equals("Вход")) {
                    lastLogin = dateTime;
                    currentUser = user;
                } else if (eventType.equals("Выход") && currentUser != null && currentUser.equals(user)) {
                    model.addRow(user, lastLogin, dateTime);
                    lastLogin = null;
                    currentUser = null;
                }
            }

            if (lastLogin != null) {
                model.addRow(currentUser, lastLogin, "Не завершено");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Ошибка загрузки журнала: " + ex.getMessage());
        }
    }
    private String formatDateTime(String dateTime) {
        if (dateTime == null || dateTime.equalsIgnoreCase("N/A")) {
            return "N/A";
        }

        DateTimeFormatter[] inputFormatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        };

        for (DateTimeFormatter formatter : inputFormatters) {
            try {
                LocalDateTime dt = LocalDateTime.parse(dateTime, formatter);
                return dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            } catch (Exception e) {
                // Продолжаем попытки
            }
        }

        System.err.println("Ошибка парсинга даты: " + dateTime);
        return "N/A";
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}




//
//        button1.setBorderPainted(false);
//                button1.setContentAreaFilled(false);
//                button1.setFocusPainted(false);
//                button1.setText(""); // Убираем текст, если он есть
//
//                button2.setBorderPainted(false);
//                button2.setContentAreaFilled(false);
//                button2.setFocusPainted(false);
//                button2.setText("");
//
//                button3.setBorderPainted(false);
//                button3.setContentAreaFilled(false);
//                button3.setFocusPainted(false);
//                button3.setText("");