package org.example;

import com.fazecast.jSerialComm.SerialPort;
import org.example.Model.PrinterTableModel;
import org.example.Model.TemplateField;
import org.example.Model.TemplateTableModel;
import org.example.Model.UserTableModel;
import org.example.Service.*;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Setting extends JPanel{
    private final TemplateTableModel templateModel;
    private UserTableModel userModel;
    private JTextField textFielLicense;
    private JComboBox comboBoxSerialPort;
    private JCheckBox COMPortCheckBox;
    private final Logger logger = Logger.getInstance(); // Добавляем логгер

    private JTextField textFieldIPAdress;
    private JCheckBox ethernetCheckBox;
    private JTextField textFieldPrinterPort;
    private JButton ButtonOpenPort;
    private JButton ButtonClosePort;
    private JTable tablePrinter;
    private JTable tableUsers;

    private JPanel mainPanel; // Главная панель из дизайнера
    private JButton buttonReport;
    private JButton buttonHome;
    private JButton buttonSetting;
    private JButton buttonAdmin;
    private JButton buttonPrinter;
    private JButton buttonSupport;
    private JLabel LableName;
    private JButton buttonLogOut;
    private JButton buttonRemovePrinter;
    private JButton buttonGeneral;
    private JButton buttonHelper;

    private final PrinterTableModel printerModel;

    private JButton buttonSave;

    private JButton buttonSaveSample;
    private JButton buttonAddUser;
    private JButton ButtonAddPrinter;
    private JButton ButtonAddField;
    private JButton ButtonRemoveField;
    private JTable table2;

    private JButton buttonsavePrinters;
    private final MainFrame parent;
    public Setting(MainFrame parent,LocalDate date) {
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


        ButtonAddPrinter.addActionListener(e -> addPrinter());
        buttonRemovePrinter.addActionListener(e -> removePrinter());
        ButtonOpenPort.addActionListener(this::handleOpenPort);
        ButtonClosePort.addActionListener(this::handleClosePort);

        buttonSupport.setBorderPainted(false);
        buttonSupport.setContentAreaFilled(false);
        buttonSupport.setFocusPainted(false);
        buttonSupport.setText(""); // Убираем текст, если он есть

        buttonAdmin.setBorderPainted(false);
        buttonAdmin.setContentAreaFilled(false);
        buttonAdmin.setFocusPainted(false);
        buttonAdmin.setText(""); // Убираем текст, если он есть

        buttonHome.setBorderPainted(false);
        buttonHome.setContentAreaFilled(false);
        buttonHome.setFocusPainted(false);
        buttonHome.setText(""); // Убираем текст, если он есть

        buttonReport.setBorderPainted(false);
        buttonReport.setContentAreaFilled(false);
        buttonReport.setFocusPainted(false);
        buttonReport.setText(""); // Убираем текст, если он есть

        buttonPrinter.setBorderPainted(false);
        buttonPrinter.setContentAreaFilled(false);
        buttonPrinter.setFocusPainted(false);
        buttonPrinter.setText(""); // Убираем текст, если он есть

        buttonLogOut.setBorderPainted(false);
        buttonLogOut.setContentAreaFilled(false);
        buttonLogOut.setFocusPainted(false);
        buttonLogOut.setText(""); // Убираем текст, если он есть

        buttonSetting.setBorderPainted(false);
        buttonSetting.setContentAreaFilled(false);
        buttonSetting.setFocusPainted(false);
        buttonSetting.setText(""); // Убираем текст, если он есть

        // Настройка COM-портов
        updateComPorts();



        mainPanel.setPreferredSize(new Dimension(1920, 1080));


        // Инициализация таблицы принтеров
        printerModel = new PrinterTableModel();
        tablePrinter.setModel(printerModel);
        configurePrintersTable();
        loadPrinters();



        // Инициализация таблицы шаблонов
        templateModel = new TemplateTableModel();
        table2.setModel(templateModel);
        configureTemplateTable();

        // Добавляем обработчики
        ButtonAddField.addActionListener(e -> addTemplateField());
        ButtonRemoveField.addActionListener(e -> removeTemplateField());
        buttonSaveSample.addActionListener(e -> saveTemplate());

//        // Убираем границы и фон кнопок
//        buttonHelper.setBorderPainted(false);
//        buttonHelper.setContentAreaFilled(false);
//        buttonHelper.setFocusPainted(false);
//        buttonHelper.setText(""); // Убираем текст, если он есть
//
//        buttonGeneral.setBorderPainted(false);
//        buttonGeneral.setContentAreaFilled(false);
//        buttonGeneral.setFocusPainted(false);
//        buttonGeneral.setText("");
//
//        buttonReport.setBorderPainted(false);
//        buttonReport.setContentAreaFilled(false);
//        buttonReport.setFocusPainted(false);
//        buttonReport.setText("");
//
//
//
//        // Настройка COM-портов
//        SerialPort[] ports = SerialPort.getCommPorts();
//        for (SerialPort port : ports) {
//            comboBoxSerialPort.addItem(port.getSystemPortName());
//        }
//
//        // Обработчики событий
//        ButtonOpenPort.addActionListener(this::handleOpenPort);
//        ButtonClosePort.addActionListener(this::handleClosePort);
//        buttonReport.addActionListener(e -> parent.showReport());
//        buttonGeneral.addActionListener(e -> parent.showGeneral());
//        buttonHelper.addActionListener(e->parent.showSupport());
//

//
//        setSize(600, 400);
//        ButtonAddPrinter.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                printerModel.addRow(new Object[]{null, "", "", 0, 0});
//
//            }
//        });
//        buttonsavePrinters.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                savePrinters();
//            }
//        });

        buttonsavePrinters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePrinters();
            }
        });
        ethernetCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelected(false);
            }
        });
        COMPortCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelected(true);

            }
        });
    }

    private void setSelected(boolean isSerial) {
        COMPortCheckBox.setSelected(isSerial);
        ethernetCheckBox.setSelected(!isSerial);
    }

    private void saveTemplate() {
        String templateName = JOptionPane.showInputDialog("Введите название шаблона:");
        if (templateName == null || templateName.trim().isEmpty()) {
            showError("Название шаблона не может быть пустым");
            return;
        }

        if (templateModel.getRowCount() == 0) {
            showError("Добавьте хотя бы одно поле в шаблон");
            return;
        }

        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            // Сохраняем основной шаблон
            int templateId = saveMainTemplate(conn, templateName);

            // Сохраняем поля шаблона
            saveTemplateFields(conn, templateId);

            conn.commit();
            showInfo("Шаблон успешно сохранен!");
        } catch (SQLException ex) {
            showError("Ошибка сохранения: " + ex.getMessage());
        }
    }

    private int saveMainTemplate(Connection conn, String name) throws SQLException {
        String sql = "INSERT INTO Шаблоны (Наименование, Поля) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, ""); // Для обратной совместимости
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Не удалось получить ID шаблона");
        }
    }

    private void saveTemplateFields(Connection conn, int templateId) throws SQLException {
        String sql = "INSERT INTO Поля (Шаблон_id, Номер, Наименование_поля, Текст) VALUES (?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < templateModel.getRowCount(); i++) {
                TemplateField field = templateModel.getFieldAt(i);
                pstmt.setInt(1, templateId);
                pstmt.setInt(2, field.getNumber());
                pstmt.setString(3, field.getFieldName());
                pstmt.setString(4, field.getText());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    private void configureTemplateTable() {
        // Установите редакторы для колонок
        table2.setDefaultEditor(String.class, new DefaultCellEditor(new JTextField()));
        table2.setDefaultEditor(Integer.class, new DefaultCellEditor(new JTextField()));

        table2.getColumnModel().getColumn(0).setPreferredWidth(100);
        table2.getColumnModel().getColumn(1).setPreferredWidth(200);
        table2.getColumnModel().getColumn(2).setPreferredWidth(400);
    }

    private void addTemplateField() {
        templateModel.addField(new TemplateField(
                templateModel.getRowCount() + 1,
                "Новое поле",
                ""
        ));
    }

    private void removeTemplateField() {
        int selectedRow = table2.getSelectedRow();
        if (selectedRow >= 0) {
            templateModel.removeField(selectedRow);
        }
    }
    private void updateComPorts() {
        comboBoxSerialPort.removeAllItems();
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            comboBoxSerialPort.addItem(port.getSystemPortName());
        }
    }

    private void configureTable() {
        // Настройка выпадающего списка для ролей
        JComboBox<UserRole> roleComboBox = new JComboBox<>(UserRole.values());
        tableUsers.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(roleComboBox));

        // Рендерер для паролей
        tableUsers.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 3) { // Колонка с паролем
                    setText("********");
                }
                return c;
            }
        });
    }

    private void addPrinter() {
        printerModel.addRow(new Object[]{
                null,  // id
                "",    // Наименование
                "",    // Серия
                0,     // Количество символов
                0      // Часы работы
        });
    }

    private void removePrinter() {
        int selectedRow = tablePrinter.getSelectedRow();
        if (selectedRow >= 0) {
            printerModel.removeRow(selectedRow);
        }
    }

    private void savePrinters() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            for (int i = 0; i < printerModel.getRowCount(); i++) {
                Object[] row = printerModel.getRow(i);

                if (row[1] == null || ((String) row[1]).trim().isEmpty()) {
                    showError("Введите наименование принтера в строке " + (i+1));
                    return;
                }

                String sql = row[0] == null ?
                        "INSERT INTO Принтеры (Наименование, Серия, КоличествоСимволов, ЧасыРаботы) VALUES (?,?,?,?)" :
                        "UPDATE Принтеры SET Наименование=?, Серия=?, КоличествоСимволов=?, ЧасыРаботы=? WHERE id=?";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, (String) row[1]);
                    pstmt.setString(2, (String) row[2]);
                    pstmt.setInt(3, (Integer) row[3]);
                    pstmt.setInt(4, (Integer) row[4]);

                    if (row[0] != null) {
                        pstmt.setInt(5, (Integer) row[0]);
                    }

                    pstmt.executeUpdate();
                }
            }
            conn.commit();
            showInfo("Данные успешно сохранены!");
        } catch (SQLException ex) {
            showError("Ошибка сохранения: " + ex.getMessage());
        }
    }
    private void loadPrinters() {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Принтеры")) {

            printerModel.setRowCount(0);
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
        } catch (SQLException ex) {
            showError("Ошибка загрузки принтеров: " + ex.getMessage());
        }
    }

    private void configurePrintersTable() {
        // Настройка редакторов для числовых колонок
        tablePrinter.setDefaultEditor(Integer.class, new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                try {
                    Integer.parseInt(getCellEditorValue().toString());
                    return super.stopCellEditing();
                } catch (NumberFormatException e) {
                    showError("Введите целое число");
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

    // В класс Setting добавить:
    private void removeUser() {
        int selectedRow = tableUsers.getSelectedRow();
        if (selectedRow >= 0) {
            userModel.removeRow(selectedRow);
        }
    }

    // В XML добавить кнопку удаления
//    private void saveUsers() {
//        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
//            conn.setAutoCommit(false);
//            boolean hasErrors = false;
//
//            for (int i = 0; i < userModel.getRowCount(); i++) {
//                Object[] row = userModel.getRow(i);
//                UserData data = new UserData(row);
//
//                if (!validateUserData(data, i)) {
//                    hasErrors = true;
//                    continue;
//                }
//
//                try {
//                    if (data.isNewUser()) {
//                        createUser(conn, data);
//                    } else {
//                        updateUser(conn, data);
//                    }
//                } catch (SQLException e) {
//                    hasErrors = true;
//                    showError("Ошибка сохранения строки " + (i+1) + ": " + e.getMessage());
//                }
//            }
//
//            if (hasErrors) {
//                conn.rollback();
//                showError("Сохранение отменено из-за ошибок");
//            } else {
//                conn.commit();
//                loadUsers(); // Перезагружаем данные после сохранения
//                showInfo("Данные успешно сохранены!");
//            }
//        } catch (SQLException ex) {
//            showError("Ошибка подключения к базе: " + ex.getMessage());
//        }
//    }
//
//    private boolean validateUserData(UserData data, int row) {
//        if (data.fio.isEmpty() || data.role.isEmpty() || data.login.isEmpty()) {
//            showError("Заполните все обязательные поля в строке " + (row+1));
//            return false;
//        }
//        // Проверка пароля для нового пользователя
//        if (data.isNewUser() && (data.password.isEmpty() || data.password.equals("********"))) {
//            showError("Введите пароль для нового пользователя в строке " + (row+1));
//            return false;
//        }
//        try {
//            UserRole.valueOf(data.role);
//        } catch (IllegalArgumentException e) {
//            showError("Некорректная роль пользователя в строке " + (row+1));
//            return false;
//        }
//
//        return true;
//    }
//
//    private void createUser(Connection conn, UserData data) throws SQLException {
//        System.out.println("[LOG] Проверка существования логина: " + data.login);
//        if (isLoginExists(conn, data.login)) {
//            System.out.println("[ERROR] Логин уже существует: " + data.login);
//            showError("Логин уже существует: " + data.login);
//            return;
//        }
//
//        String hashedPass = data.password.isEmpty() ? "" : BCrypt.hashpw(data.password, BCrypt.gensalt());
//        try (PreparedStatement pstmt = conn.prepareStatement(
//                "INSERT INTO Пользователи (Фио, Роль, Логин, Пароль, Доступ) VALUES (?,?,?,?,?)")) {
//            pstmt.setString(1, data.fio);
//            pstmt.setString(2, data.role);
//            pstmt.setString(3, data.login);
//            pstmt.setString(4, hashedPass);
//            pstmt.setBoolean(5, data.access);
//            pstmt.executeUpdate();
//        }
//        logger.log("Создан пользователь: " + data.login);
//        System.out.println("[LOG] Пользователь создан: " + data.login);
//    }
//
//    private void updateUser(Connection conn, UserData data) throws SQLException {
//        String updateQuery = "UPDATE Пользователи SET Фио=?, Роль=?, Логин=?, Доступ=?";
//        boolean passwordChanged = !data.password.equals("********");
//        System.out.println("[LOG] Обновление пользователя. Смена пароля: " + passwordChanged);
//        // Проверяем, изменился ли логин
//        if (isLoginChanged(conn, data)) {
//            if (isLoginExists(conn, data.login)) {
//                showError("Логин уже существует: " + data.login);
//                throw new SQLException("Duplicate login");
//            }
//        }
//        if (passwordChanged) {
//            updateQuery += ", Пароль=?";
//            String hashedPass = BCrypt.hashpw(data.password, BCrypt.gensalt());
//
//            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery + " WHERE id=?")) {
//                pstmt.setString(1, data.fio);
//                pstmt.setString(2, data.role);
//                pstmt.setString(3, data.login);
//                pstmt.setBoolean(4, data.access);
//                pstmt.setString(5, hashedPass);
//                pstmt.setInt(6, data.id);
//                pstmt.executeUpdate();
//            }
//            logger.log("Смена пароля для: " + data.login);
//            System.out.println("[LOG] Пароль изменен для: " + data.login);
//        } else {
//            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery + " WHERE id=?")) {
//                pstmt.setString(1, data.fio);
//                pstmt.setString(2, data.role);
//                pstmt.setString(3, data.login);
//                pstmt.setBoolean(4, data.access);
//                pstmt.setInt(5, data.id);
//                pstmt.executeUpdate();
//            }
//        }
//        logger.log("Обновлен пользователь ID: " + data.id);
//        System.out.println("[LOG] Пользователь ID " + data.id + " обновлен");
//    }
//
//    private boolean isLoginChanged(Connection conn, UserData data) throws SQLException {
//        try (PreparedStatement pstmt = conn.prepareStatement(
//                "SELECT Логин FROM Пользователи WHERE id=?")) {
//            pstmt.setInt(1, data.id);
//            ResultSet rs = pstmt.executeQuery();
//            return rs.next() && !rs.getString("Логин").equals(data.login);
//        }
//    }


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


    private void updatePortStatus(boolean isOpen) {
        ButtonOpenPort.setEnabled(!isOpen);
        ButtonClosePort.setEnabled(isOpen);
        comboBoxSerialPort.setEnabled(!isOpen);
        textFieldIPAdress.setEnabled(!isOpen);
        textFieldPrinterPort.setEnabled(!isOpen);
    }
    private void handleOpenPort(ActionEvent e) {
        try {
            if (COMPortCheckBox.isSelected()) {
                String portName = (String) comboBoxSerialPort.getSelectedItem();
                PrinterManager.openCOMPort(portName);
            } else if (ethernetCheckBox.isSelected()) {
                String ip = textFieldIPAdress.getText().trim();
                int port = Integer.parseInt(textFieldPrinterPort.getText().trim());
                PrinterManager.openEthernetPort(ip, port);
            }
            updatePortStatus(true);
            JOptionPane.showMessageDialog(this, "Порт успешно открыт!");
        } catch (Exception ex) {
            updatePortStatus(false); // Добавьте эту строку
            showError("Ошибка открытия порта: " + ex.getMessage());
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
