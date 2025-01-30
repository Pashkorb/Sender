package org.example;

import org.example.Model.ErrorEntry;
import org.example.Model.ErrorTableModel;
import org.example.Model.UserTableModel;
import org.example.Service.*;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Admin extends JPanel {
    private JPanel mainPanel;
    private JButton buttonHome;
    private JButton buttonSetting;
    private JButton buttonAdmin;
    private JButton buttonPrinter;
    private JButton buttonSupport;
    private JButton buttonReport;
    private JLabel LableName;
    private JButton buttonLogOut;
    private JTextField textFieldLicence;    private Timer refreshTimer;

    private final Logger logger = Logger.getInstance(); // Добавляем логгер

    private JTextField textFieldNextLicence;
    private JButton ButtonActiveLicence;
    private JButton ButtonAddUser;    private ErrorTableModel errorModel;

    private JTable tableUser;
    private JTable tableErrors;
    private JButton buttonSaveUsers;
    private MainFrame parent;
    private LocalDate date;
    private UserTableModel userModel;

    public Admin(MainFrame parent, LocalDate date) {
        this.parent = parent;
        this.date = date;
        add(mainPanel); // Добавляем панель из дизайнера
        LableName.setText(CurrentUser.getName());

        buttonHome.addActionListener(e -> parent.showHome());
        buttonSetting.addActionListener(e -> parent.showSettings());
        buttonAdmin.addActionListener(e -> parent.showAdmin());
        buttonPrinter.addActionListener(e -> parent.showGeneral());
        buttonSupport.addActionListener(e -> parent.showSupport());
        buttonReport.addActionListener(e -> parent.showReport());
        buttonLogOut.addActionListener(e -> parent.logLogout());
        // Инициализация модели пользователей
        userModel = new UserTableModel();
        tableUser.setModel(userModel);
        ButtonActiveLicence.addActionListener(e -> activateLicense());

        // Настройка таблицы
        configureTable();

        // Загрузка данных
        loadUsers();

        // Обработчики кнопок
        ButtonAddUser.addActionListener(e -> addNewUser());
        buttonSaveUsers.addActionListener(e -> saveUsers());

        textFieldLicence.setText("Лицензия активирована, дата окончания - " + date);
        initErrorTable();
        loadErrors();
    }

    private void initErrorTable() {
        errorModel = new ErrorTableModel();
        tableErrors.setModel(errorModel);

        // Настройка колонок
        tableErrors.getColumnModel().getColumn(0).setPreferredWidth(150);
        tableErrors.getColumnModel().getColumn(1).setPreferredWidth(200);

    }
    private void initAutoRefresh() {
        refreshTimer = new Timer(50000, e -> refreshErrors());
        refreshTimer.start();
    }

    private void refreshErrors() {
        errorModel.loadErrors();
    }
    private void loadErrors() {
        String sql = "SELECT Текст, ДатаВремя FROM Ошибки ORDER BY id DESC";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ErrorEntry error = new ErrorEntry(
                        rs.getString("Текст"),
                        LocalDateTime.parse(rs.getString("ДатаВремя"),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                );
                errorModel.addError(error);
            }
        } catch (Exception e) {
            logger.logError("Ошибка загрузки ошибок: " + e.getMessage());
        }
    }
    private void activateLicense() {
        String newLicense = textFieldNextLicence.getText().trim();

        if (newLicense.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Введите лицензионный ключ",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!LicenseManager.validateLicenseKey(newLicense)) {
            JOptionPane.showMessageDialog(this,
                    "Недействительная лицензия",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Сохраняем новую лицензию
            LicenseManager.saveLicense(newLicense);

            // Обновляем отображение
            LocalDate expirationDate = LicenseManager.getExpirationDate(newLicense);
            textFieldLicence.setText("Лицензия активирована, дата окончания - " + expirationDate);
            textFieldNextLicence.setText("");

            JOptionPane.showMessageDialog(this,
                    "Лицензия успешно активирована",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка сохранения: " + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configureTable() {
        // Рендерер для отображения названий ролей
        tableUser.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText(value != null ? value.toString() : "");
            }
        });

        // Редактор с преобразованием значения
        JComboBox<UserRole> roleComboBox = new JComboBox<>(UserRole.values());
        tableUser.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(roleComboBox) {
            @Override
            public Object getCellEditorValue() {
                return ((UserRole) super.getCellEditorValue()).getRoleName();
            }
        });

        // Рендерер для паролей

    }

    private void addNewUser() {
        userModel.addRow(new Object[]{
                "",
                UserRole.WORKER.getRoleName(), // Сохраняем строковое значение
                "",
                "",
                true,
                null
        });
    }

    private void loadUsers() {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, Фио, Роль, Логин, Доступ FROM Пользователи")) {

            userModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getString("Фио"),
                        rs.getString("Роль"),
                        rs.getString("Логин"),
                        "********", // Маскировка пароля
                        rs.getBoolean("Доступ"),
                        rs.getInt("id")
                };
                userModel.addRow(row);
            }
        } catch (SQLException ex) {
            showError("Ошибка загрузки пользователей: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private void saveUsers() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            boolean hasErrors = false;

            for (int i = 0; i < userModel.getRowCount(); i++) {
                Object[] row = userModel.getRow(i);
                UserData data = new UserData(row);

                if (!validateUserData(data, i)) {
                    hasErrors = true;
                    continue;
                }

                try {
                    if (data.isNewUser()) {
                        createUser(conn, data);
                    } else {
                        updateUser(conn, data);
                    }
                } catch (SQLException e) {
                    hasErrors = true;
                    showError("Ошибка в строке " + (i + 1) + ": " + e.getMessage());
                }
            }

            if (hasErrors) {
                conn.rollback();
                showError("Сохранение отменено из-за ошибок");
            } else {
                conn.commit();
                loadUsers(); // Обновляем таблицу
                showInfo("Данные успешно сохранены!");
            }
        } catch (SQLException ex) {
            showError("Ошибка подключения: " + ex.getMessage());
        }



    }

    private void showInfo (String message){
        JOptionPane.showMessageDialog(this, message, "Информация", JOptionPane.INFORMATION_MESSAGE);
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
        try {
            // Проверяем преобразование строки в enum
            UserRole role = UserRole.fromString(data.role);
        } catch (IllegalArgumentException e) {
            showError("Некорректная роль пользователя в строке " + (row+1));
            return false;
        }

        return true;
    }
    private boolean isLoginExists(Connection conn, String login) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM Пользователи WHERE Логин=?")) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        }
    }
    private void createUser(Connection conn, UserData data) throws SQLException {
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

    private void updateUser(Connection conn, UserData data) throws SQLException {
        String updateQuery = "UPDATE Пользователи SET Фио=?, Роль=?, Логин=?, Доступ=?";
        boolean passwordChanged = !data.password.equals("********");
        System.out.println("[LOG] Обновление пользователя. Смена пароля: " + passwordChanged);
        // Проверяем, изменился ли логин
        if (isLoginChanged(conn, data)) {
            if (isLoginExists(conn, data.login)) {
                showError("Логин уже существует: " + data.login);
                throw new SQLException("Duplicate login");
            }
        }
        if (passwordChanged) {
            updateQuery += ", Пароль=?";
            String hashedPass = BCrypt.hashpw(data.password, BCrypt.gensalt());

            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery + " WHERE id=?")) {
                pstmt.setString(1, data.fio);
                pstmt.setString(2, data.role);
                pstmt.setString(3, data.login);
                pstmt.setBoolean(4, data.access);
                pstmt.setString(5, hashedPass);
                pstmt.setInt(6, data.id); // Всего 6 параметров
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

    private boolean isLoginChanged(Connection conn, UserData data) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT Логин FROM Пользователи WHERE id=?")) {
            pstmt.setInt(1, data.id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && !rs.getString("Логин").equals(data.login);
        }
    }


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
}
