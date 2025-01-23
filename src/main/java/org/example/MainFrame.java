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

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private General generalPanel;
    private Setting settingPanel;
    private  Report reportPanel;

    public MainFrame() {
        System.out.println("[MAIN FRAME] Создание главного окна");

        setTitle("Главное окно");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);

        // Инициализация панелей
        generalPanel = new General(this);
        settingPanel = new Setting(this);
        reportPanel=new Report(this);

        // Настройка CardLayout
        mainPanel = new JPanel(new CardLayout());
        mainPanel.add(generalPanel, "General");
        mainPanel.add(settingPanel, "Settings");
        mainPanel.add(reportPanel, "Report");

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
    }

    public void showSettings() {
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "Settings");
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    public void showReport() {
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "Report");

    }
}