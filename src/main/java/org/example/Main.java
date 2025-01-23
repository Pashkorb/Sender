package org.example;

import org.example.Service.DatabaseManager;
import org.example.Service.LicenseInputDialog;
import org.example.Service.LicenseManager;

import javax.swing.*;
import java.time.LocalDate;

import org.example.Service.*;
import javax.swing.*;
import java.time.LocalDate;

public class Main {


        public static void main(String[] args) {
            System.out.println("[MAIN] Запуск приложения");

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
                    } else {
                        System.out.println("[MAIN] Лицензия не активирована. Выход");
                        JOptionPane.showMessageDialog(null, "Лицензия не активирована. Программа завершена.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                }

                System.out.println("[MAIN] Создание формы входа...");
                SwingUtilities.invokeLater(() -> {
                    Enter enterForm = new Enter();
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
    }