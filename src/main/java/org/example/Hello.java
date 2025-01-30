package org.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.Service.CurrentUser;
import org.example.Service.LicenseInputDialog;
import org.example.Service.LicenseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

public class Hello extends JFrame{
    private JLabel FastMarking;
    private JLabel F1;
    private JLabel F2;
    private final Support supportPanel;
    private JButton buttonEnter;
    private JButton buttonHelper;
    private JPanel mainPanel;
    private final HelloPanel helloPanel;

    public Hello (){
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

        mainPanel.setPreferredSize(new Dimension(1920, 1080));


        supportPanel=new Support(this);
        helloPanel=new HelloPanel(this);



        // Настройка CardLayout
        mainPanel = new JPanel(new CardLayout());

        mainPanel.add(supportPanel,"Support");
        mainPanel.add(helloPanel,"Hello");

        add(mainPanel);
        showHelloPanel();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
//                logLogout();
//                onExit();
                CurrentUser.clear();
                System.exit(0);
            }
        });

        buttonEnter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enter();
            }
        });
    }

    public void enter() {
        dispose();
        LocalDate expirationDate = LocalDate.of(2026, 12, 12);

        LicenseManager.generateLicenseKey(expirationDate);
        try {
            System.out.println("[MAIN] Проверка лицензии...");
            String licenseKey = LicenseManager.loadLicense();

            if (licenseKey == null || !LicenseManager.validateLicenseKey(licenseKey)) {
                System.out.println("[MAIN] Лицензия недействительна");
                LicenseInputDialog dialog = new LicenseInputDialog(null);
                dialog.setVisible(true); // Модальный диалог

                if (dialog.isLicenseValid()) {
                    System.out.println("[MAIN] Лицензия активирована");
                    LicenseManager.saveLicense(dialog.getLicenseKey());
                    licenseKey = dialog.getLicenseKey(); // Обновляем licenseKey

                } else {
                    System.out.println("[MAIN] Лицензия не активирована. Выход");
                    JOptionPane.showMessageDialog(null, "Лицензия не активирована. Программа завершена.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }

            // Извлекаем дату окончания лицензии
            com.auth0.jwt.interfaces.DecodedJWT jwt = com.auth0.jwt.JWT.decode(licenseKey);
            LocalDate Date = LocalDate.parse(jwt.getClaim("expiration").asString());
            System.out.println("[MAIN] Дата окончания лицензии: " + expirationDate);

            System.out.println("[MAIN] Создание формы входа...");
            SwingUtilities.invokeLater(() -> {
                Enter enterForm = new Enter(Date);
                enterForm.setVisible(true);
                System.out.println("[MAIN] Форма входа закрыта");
            });

        } catch (Exception e) {
            System.out.println("[MAIN] КРИТИЧЕСКАЯ ОШИБКА: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка при запуске: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

    }

    public void showSupport() {
        ((CardLayout)mainPanel.getLayout()).show(mainPanel,"Support");
    }
    public void showHelloPanel() {
        ((CardLayout)mainPanel.getLayout()).show(mainPanel,"Hello");
    }



}
