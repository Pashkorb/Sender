package org.example;

import com.fazecast.jSerialComm.SerialPort;
import org.example.Model.PrinterTableModel;
import org.example.Model.UserTableModel;
import org.example.Service.DatabaseManager;
import org.example.Service.Logger;
import org.example.Service.PrinterManager;
import org.mindrot.jbcrypt.BCrypt;

import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter; // Добавьте этот импорт
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;


public class Setting extends JPanel{
    private UserTableModel userModel;
    private JTextField textFielLicense;
    private JComboBox comboBoxSerialPort;
    private JCheckBox COMPortCheckBox;
    private JTextField textFieldIPAdress;
    private JCheckBox ethernetCheckBox;
    private JTextField textFieldPrinterPort;
    private JTextField textFieldPCPort;
    private JButton ButtonOpenPort;
    private JButton ButtonClosePort;
    private JTable tablePrinter;
    private JTable tableUsers;
    private JTextPane textPaneErrors;
    private JPanel panel2;
    private JPanel panel3;
    private JPanel panel4;
    private JPanel panel5;
    private JPanel panel6;
    private JPanel panel7;
    private JPanel panel8;
    private JPanel panel9;

    private JPanel mainPanel; // Главная панель из дизайнера
    private JButton buttonReport;
    private JButton buttonGeneral;
    private JButton buttonHelper;

    private PrinterTableModel printerModel;

    private JButton buttonSave;

    private JButton buttonAddUser;
    private JButton ButtonAddPrinter;
    private JButton buttonsavePrinters;
    private MainFrame parent;
    public Setting(MainFrame parent,LocalDate date) {
        this.parent = parent;
        add(mainPanel);
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        textFielLicense.setText("Лицензия активирована, дата окончания - " + formattedDate);        // Инициализация таблицы пользователей
        userModel = new UserTableModel();
        tableUsers.setModel(userModel);
        configureTable();
        loadUsers();

        // Инициализация таблицы принтеров
        printerModel = new PrinterTableModel();
        tablePrinter.setModel(printerModel);
        configurePrintersTable();
        loadPrinters();

        // Настройка COM-портов
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            comboBoxSerialPort.addItem(port.getSystemPortName());
        }

        // Обработчики событий
        ButtonOpenPort.addActionListener(this::handleOpenPort);
        ButtonClosePort.addActionListener(this::handleClosePort);
        buttonReport.addActionListener(e -> parent.showReport());
        buttonGeneral.addActionListener(e -> parent.showGeneral());
        buttonAddUser.addActionListener(e -> addNewUser());
        buttonSave.addActionListener(e -> saveUsers());

        setSize(600, 400);
        ButtonAddPrinter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printerModel.addRow(new Object[]{null, "", "", 0, 0});

            }
        });
        buttonsavePrinters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePrinters();
            }
        });
    }

    private void savePrinters() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            Logger logger = Logger.getInstance();

            for (int i = 0; i < printerModel.getRowCount(); i++) {
                Object[] row = printerModel.getRow(i);
                Integer id = (Integer) row[0];

                // Используем безопасное преобразование типов
                String name = row[1] != null ? row[1].toString() : "";
                String series = row[2] != null ? row[2].toString() : "";

                // Преобразуем числовые значения с проверкой
                int symbols = 0;
                try {
                    symbols = Integer.parseInt(row[3].toString());
                } catch (NumberFormatException | NullPointerException e) {
                    showError("Некорректное количество символов в строке " + (i+1));
                    return;
                }

                int hours = 0;
                try {
                    hours = Integer.parseInt(row[4].toString());
                } catch (NumberFormatException | NullPointerException e) {
                    showError("Некорректное количество часов в строке " + (i+1));
                    return;
                }

                if (name.trim().isEmpty()) {
                    showError("Наименование не может быть пустым в строке " + (i+1));
                    return;
                }

                if (id == null) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO Принтеры (Наименование, Серия, КоличествоСимволов, ЧасыРаботы) VALUES (?, ?, ?, ?)")) {
                        pstmt.setString(1, name.trim());
                        pstmt.setString(2, series.trim());
                        pstmt.setInt(3, symbols);
                        pstmt.setInt(4, hours);
                        pstmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE Принтеры SET Наименование=?, Серия=?, КоличествоСимволов=?, ЧасыРаботы=? WHERE id=?")) {
                        pstmt.setString(1, name.trim());
                        pstmt.setString(2, series.trim());
                        pstmt.setInt(3, symbols);
                        pstmt.setInt(4, hours);
                        pstmt.setInt(5, id);
                        pstmt.executeUpdate();
                    }
                }
            }
            conn.commit();
            logger.log("Данные принтеров успешно сохранены");
        } catch (SQLException ex) {
            showError("Ошибка сохранения принтеров: " + ex.getMessage());
            Logger.getInstance().logError("Ошибка сохранения принтеров: " + ex.getMessage());
        }
    }

    private void loadPrinters() {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, Наименование, Серия, КоличествоСимволов, ЧасыРаботы FROM Принтеры")) {

            printerModel = new PrinterTableModel();
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("Наименование"),
                        rs.getString("Серия"),
                        rs.getInt("КоличествоСимволов"),
                        rs.getInt("ЧасыРаботы")
                };
                printerModel.addRow(row);
            }
            tablePrinter.setModel(printerModel);
        } catch (SQLException ex) {
            showError("Ошибка загрузки принтеров: " + ex.getMessage());
        }
    }


    private void configurePrintersTable() {
        // Редактор для числовых колонок
        tablePrinter.setDefaultEditor(Integer.class, new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                try {
                    String value = getCellEditorValue().toString();
                    if (!value.isEmpty()) Integer.parseInt(value);
                    return super.stopCellEditing();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(tablePrinter,
                            "Введите целое число", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        });

        // Выравнивание для числовых колонок
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablePrinter.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tablePrinter.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
    }

    private void configureTable() {
        tableUsers.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
                int modelColumn = table.convertColumnIndexToModel(column);
                if (modelColumn == 3) {
                    // Отображаем звездочки только для загруженных паролей
                    UserData data = new UserData(userModel.getRow(row));
                    if (data.password.equals("********")) {
                        setText("********");
                    } else {
                        setText(data.password); // Показываем реальный пароль для новых или измененных
                    }
                }
                return c;
            }
        });

        tableUsers.getTableHeader().setReorderingAllowed(false);
        tableUsers.setAutoCreateRowSorter(true);
        System.out.println("[LOG] Рендерер для таблицы настроен");
    }

    private void addNewUser() {
        System.out.println("[LOG] Добавление нового пользователя в таблицу");
        userModel.addRow(new Object[]{"", "", "", "", true, null});
    }

    private void loadUsers() {
        System.out.println("[LOG] Начало загрузки пользователей из БД");
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, Фио, Роль, Логин, Пароль, Доступ FROM Пользователи")) {

            userModel = new UserTableModel();
            int count = 0;
            while (rs.next()) {
                Object[] row = {
                        rs.getString("Фио"),
                        rs.getString("Роль"),
                        rs.getString("Логин"),
                        "********", // Пароль загружается как звездочки
                        rs.getBoolean("Доступ"),
                        rs.getInt("id")
                };
                userModel.addRow(row);
                count++;
            }
            tableUsers.setModel(userModel);
            System.out.println("[LOG] Загружено пользователей: " + count);
        } catch (SQLException ex) {
            System.out.println("[ERROR] Ошибка загрузки пользователей: " + ex.getMessage());
            showError("Ошибка загрузки пользователей: " + ex.getMessage());
        }
    }
    private void saveUsers() {
        System.out.println("[LOG] Начало сохранения пользователей");
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            Logger logger = Logger.getInstance();
            int totalRows = userModel.getRowCount();
            System.out.println("[LOG] Обработка " + totalRows + " записей");

            for (int i = 0; i < totalRows; i++) {
                Object[] row = userModel.getRow(i);
                UserData data = new UserData(row);
                System.out.println("[LOG] Обработка пользователя: " + data.login);

                if (!validateUserData(data, i)) {
                    System.out.println("[WARN] Валидация не пройдена для строки " + (i+1));
                    return;
                }

                if (data.isNewUser()) {
                    System.out.println("[LOG] Создание нового пользователя: " + data.login);
                    createUser(conn, data, logger);
                } else {
                    System.out.println("[LOG] Обновление пользователя ID: " + data.id);
                    updateUser(conn, data, logger);
                }
            }
            conn.commit();
            logger.log("Успешное сохранение данных пользователей");
            System.out.println("[LOG] Все изменения успешно сохранены");
            showInfo("Данные сохранены успешно!");
        } catch (SQLException ex) {
            System.out.println("[ERROR] Ошибка сохранения: " + ex.getMessage());
            Logger.getInstance().logError("Ошибка сохранения: " + ex.getMessage());
            showError("Ошибка сохранения: " + ex.getMessage());
        }
    }

    private boolean validateUserData(UserData data, int row) {
        if (data.fio.isEmpty() || data.role.isEmpty() || data.login.isEmpty()) {
            showError("Заполните все обязательные поля в строке " + (row+1));
            return false;
        }
        // Проверка пароля для нового пользователя
        if (data.isNewUser() && (data.password.isEmpty() || data.password.equals("********"))) {
            showError("Введите пароль для нового пользователя в строке " + (row+1));
            return false;
        }
        return true;
    }

    private void createUser(Connection conn, UserData data, Logger logger) throws SQLException {
        System.out.println("[LOG] Проверка существования логина: " + data.login);
        if (isLoginExists(conn, data.login)) {
            System.out.println("[ERROR] Логин уже существует: " + data.login);
            showError("Логин уже существует: " + data.login);
            return;
        }

        String hashedPass = data.password.isEmpty() ? "" : BCrypt.hashpw(data.password, BCrypt.gensalt());
        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO Пользователи (Фио, Роль, Логин, Пароль, Доступ) VALUES (?,?,?,?,?)")) {
            pstmt.setString(1, data.fio);
            pstmt.setString(2, data.role);
            pstmt.setString(3, data.login);
            pstmt.setString(4, hashedPass);
            pstmt.setBoolean(5, data.access);
            pstmt.executeUpdate();
        }
        logger.log("Создан пользователь: " + data.login);
        System.out.println("[LOG] Пользователь создан: " + data.login);
    }

    private void updateUser(Connection conn, UserData data, Logger logger) throws SQLException {
        String updateQuery = "UPDATE Пользователи SET Фио=?, Роль=?, Логин=?, Доступ=?";
        boolean passwordChanged = !data.password.equals("********");
        System.out.println("[LOG] Обновление пользователя. Смена пароля: " + passwordChanged);

        if (passwordChanged) {
            updateQuery += ", Пароль=?";
            String hashedPass = BCrypt.hashpw(data.password, BCrypt.gensalt());

            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery + " WHERE id=?")) {
                pstmt.setString(1, data.fio);
                pstmt.setString(2, data.role);
                pstmt.setString(3, data.login);
                pstmt.setBoolean(4, data.access);
                pstmt.setString(5, hashedPass);
                pstmt.setInt(6, data.id);
                pstmt.executeUpdate();
            }
            logger.log("Смена пароля для: " + data.login);
            System.out.println("[LOG] Пароль изменен для: " + data.login);
        } else {
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery + " WHERE id=?")) {
                pstmt.setString(1, data.fio);
                pstmt.setString(2, data.role);
                pstmt.setString(3, data.login);
                pstmt.setBoolean(4, data.access);
                pstmt.setInt(5, data.id);
                pstmt.executeUpdate();
            }
        }
        logger.log("Обновлен пользователь ID: " + data.id);
        System.out.println("[LOG] Пользователь ID " + data.id + " обновлен");
    }


    // Вспомогательные методы
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private boolean isLoginExists(Connection conn, String login) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM Пользователи WHERE Логин=?")) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        }
    }

    // Класс для хранения данных пользователя
    private static class UserData {
        String fio;
        String role;
        String login;
        String password;
        boolean access;
        Integer id;

        UserData(Object[] row) {
            this.fio = (String) row[0];
            this.role = (String) row[1];
            this.login = (String) row[2];
            this.password = (String) row[3];
            this.access = (Boolean) row[4];
            this.id = (Integer) row[5];
        }

        boolean isNewUser() {
            return id == null;
        }
    }

    private void handleOpenPort(ActionEvent e) {
        try {
            if (COMPortCheckBox.isSelected()) {
                String portName = (String) comboBoxSerialPort.getSelectedItem();
                if (portName == null) throw new Exception("Выберите COM-порт!");
                PrinterManager.openPort(portName, null, 0);
            } else if (ethernetCheckBox.isSelected()) {
                String ip = textFieldIPAdress.getText().trim();
                String portText = textFieldPrinterPort.getText().trim();
                if (ip.isEmpty() || portText.isEmpty()) throw new Exception("Заполните IP и порт!");
                int port = Integer.parseInt(portText);
                PrinterManager.openPort(null, ip, port);
            }
            JOptionPane.showMessageDialog(this, "Порт открыт!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void handleClosePort(ActionEvent e) {
        try {
            PrinterManager.closePort();
            JOptionPane.showMessageDialog(this, "Порт закрыт!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

}
