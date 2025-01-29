package org.example;


import org.example.Service.CurrentUser;
import org.example.Service.DatabaseManager;
import org.example.Service.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class MainFrame extends JFrame {

    private JPanel mainPanel;
    private General generalPanel;
    private Setting settingPanel;
    private  Report reportPanel;

    private Helper supportPanel;



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
        generalPanel = new General(this,date);
//        settingPanel = new Setting(this,date);
//        supportPanel=new Helper(this);
//        reportPanel=new Report(this);

        // Настройка CardLayout
        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(generalPanel, "General");
//        mainPanel.add(settingPanel, "Settings");
//        mainPanel.add(reportPanel, "Report");
//        mainPanel.add(supportPanel,"Support");



        add(mainPanel);
        showGeneral();
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
    private void logLogout() {
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
//        generalPanel.loadPrintersToComboBox(); // Обновляем список при открытии

    }

    public void showSettings() {
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "Settings");
    }





    public void showReport() {
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "Report");

    }

    public void showSupport() {
        ((CardLayout)mainPanel.getLayout()).show(mainPanel,"Support");
    }
}