package org.example;


import org.example.Service.CurrentUser;
import org.example.Service.DatabaseManager;
import org.example.Service.Logger;
import org.example.Service.UserRole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class MainFrame extends JFrame {
    private final Logger logger = Logger.getInstance(); // Добавляем логгер

    private JPanel mainPanel;
    private General generalPanel;
    private Setting settingPanel;
    private  Report reportPanel;

    private Support supportPanel;
    private Admin adminPanel;

    private Home homePanel;

    private LocalDate date;

    public MainFrame(LocalDate dated) {

        date=dated;
        System.out.println("[MAIN FRAME] Создание главного окна");
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("Button.background", Color.WHITE);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextArea.background", Color.WHITE);
        UIManager.put("Label.background", Color.WHITE);
        setTitle("Главное окно");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Развернуть окно на весь экран
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Инициализация панелей
        homePanel=new Home(this);
        supportPanel=new Support(this);
        generalPanel = new General(this,date);
        settingPanel = new Setting(this,date);
        adminPanel=new Admin(this,date);
//        reportPanel=new Report(this);

        // Настройка CardLayout
        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(homePanel,"Home");
        mainPanel.add(generalPanel, "Support");
        mainPanel.add(generalPanel, "General");
        mainPanel.add(settingPanel, "Settings");
//        mainPanel.add(reportPanel, "Report");
        mainPanel.add(adminPanel,"Admin");



        add(mainPanel);
        showHome();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logLogout();
                onExit();
                CurrentUser.clear();
                System.exit(0);
            }
        });
    }
    public General getGeneralPanel() {
        return generalPanel;
    }
    private void onExit() {
        String username = CurrentUser.getLogin();
        Logger.getInstance().logLogout(username);
        CurrentUser.clear();
        System.exit(0);
    }
    public void logLogout() {
        System.out.println("[MAIN FRAME] Логирование выхода пользователя: " + CurrentUser.getLogin());

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO ЖурналАвторизаций (Пользователь_id, ТипСобытия, ДатаВремя) " +
                             "VALUES (?, 'Выход', datetime('now'))")) {
            pstmt.setInt(1, CurrentUser.getId());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGeneral() {
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "General");
        generalPanel.loadPrintersToComboBox(); // Обновляем список при открытии

    }

    public void showSettings() {

        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "Settings");
    }

    public void showAdmin() {
        if (CurrentUser.getRole().equals(UserRole.ADMIN)) {
            ((CardLayout) mainPanel.getLayout()).show(mainPanel, "Admin");
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "У вас недостаточно прав для доступа к административному разделу",
                    "Ошибка доступа",
                    JOptionPane.ERROR_MESSAGE
            );
            logger.log("Попытка несанкционированного доступа к админ-панели пользователем: "
                    + CurrentUser.getName());
        }
    }

    public void showHome(){
        ((CardLayout) mainPanel.getLayout()).show(mainPanel,"Home");
    }



    public void showReport() {
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "Report");

    }

    public void showSupport() {
        ((CardLayout)mainPanel.getLayout()).show(mainPanel,"Support");
    }

}