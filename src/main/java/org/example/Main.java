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
        Hello helloForm = new Hello();
        helloForm.setVisible(true);
    }


}

/*
public static void main(String[] args) {
            System.out.println("[MAIN] Запуск приложения");
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



*/
