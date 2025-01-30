package org.example;

import org.example.Service.CurrentUser;
import org.example.Service.DatabaseManager;
import org.example.Service.Logger;
import org.example.Service.UserRole;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Enter extends JDialog { // Используем JDialog вместо JFrame
    private JPanel panel1;
    private final Logger logger = Logger.getInstance(); // Добавляем логгер

    private JButton buttonSupport;
    private JButton ButtonEnter;
    private JTextField textFieldLogin;//логин
    private JTextField textFieldPassword;//пароль

    private final LocalDate date;
    public Enter(LocalDate expirationDate) {
        super((JFrame) null, "Вход в систему", true); // Модальное окно
        System.out.println("[ENTER] Инициализация формы входа");
        date=expirationDate;

        setContentPane(panel1);

        panel1.getRootPane().setDefaultButton(ButtonEnter);
        setSize(1244, 588);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("Button.background", Color.WHITE);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextArea.background", Color.WHITE);
        UIManager.put("Label.background", Color.WHITE);

        ButtonEnter.addActionListener(e -> {
            System.out.println("[ENTER] Нажата кнопка входа");
            authenticate();
        });
    }
    private void authenticate() {

//        //TODO:Удалить заглушку
//        dispose(); // Закрываем диалог входа
//        openMainFrame(); // Открываем главное окн
//        return
//        //////////////////////////////////////////



        String login = textFieldLogin.getText();
        String password = textFieldPassword.getText();
        System.out.println("[ENTER] Попытка входа для: " + login);

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT id, Пароль, Фио,Роль,Доступ FROM Пользователи WHERE Логин = ?")) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("Пароль");
                if (BCrypt.checkpw(password, storedHash)) {
                    boolean isUserActive = rs.getBoolean("Доступ");

                    if (!isUserActive) {
                        String logMessage = String.format(
                                "Доступ запрещен. Пользователь '%s' деактивирован",
                                CurrentUser.getLogin()
                        );

                        // Логирование события
                        Logger.getInstance().log(logMessage);

                        // Опционально: показать сообщение пользователю
                        JOptionPane.showMessageDialog(
                                null,
                                "Ваш аккаунт деактивирован",
                                "Доступ запрещен",
                                JOptionPane.WARNING_MESSAGE
                        );

                        // Выход из метода/закрытие ресурсов
                            return;
                    }
                    int userId = rs.getInt("id");
                    String name = rs.getString("Фио");
                    String srole =rs.getString("Роль");
                    UserRole role=UserRole.fromString(srole);
                    CurrentUser.setId(userId);
                    CurrentUser.setLogin(login);
                    CurrentUser.setName(name);
                    CurrentUser.setRole(role);

                    // Логируем вход
                    Logger.getInstance().logLogin(login);
                    logLogin(userId, conn); // Запись в БД

                    dispose(); // Закрываем диалог входа
                    openMainFrame(); // Открываем главное окно

                    return;

                }
            }
            Logger.getInstance().logError("Неудачная попытка входа для логина: " + login);
            JOptionPane.showMessageDialog(this, "Неверный логин или пароль");
        } catch (Exception ex) {
            Logger.getInstance().logError("Ошибка аутентификации: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void openMainFrame() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("[ENTER] Создание MainFrame");
            MainFrame mainFrame = new MainFrame(date);
            mainFrame.setVisible(true);
        });
    }
    private void logLogin(int userId, Connection conn) throws SQLException {
        String sql = "INSERT INTO ЖурналАвторизаций (Пользователь_id, ТипСобытия, ДатаВремя) " +
                "VALUES (?, 'Вход', datetime('now', 'localtime'))";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }


}
