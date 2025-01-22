package org.example;

import org.example.Service.DatabaseManager;
import org.example.Service.LicenseInputDialog;
import org.example.Service.LicenseManager;

import javax.swing.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {


        System.out.println(LicenseManager.generateLicenseKey(LocalDate.of(2025, 12, 31)));
        try {
            // Проверяем лицензию
            String licenseKey = LicenseManager.loadLicense();
            if (licenseKey == null || !LicenseManager.validateLicenseKey(licenseKey)) {
                // Лицензия недействительна, запрашиваем новый ключ
                LicenseInputDialog dialog = new LicenseInputDialog(null);
                dialog.setVisible(true);

                if (dialog.isLicenseValid()) {
                    // Сохраняем новый ключ
                    LicenseManager.saveLicense(dialog.getLicenseKey());
                } else {
                    JOptionPane.showMessageDialog(null, "Лицензия не активирована. Программа завершена.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }

            // Лицензия действительна, запускаем программу
            System.out.println("Лицензия действительна. Программа запущена.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ошибка при проверке лицензии: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Инициализация базы данных и логов
        DatabaseManager dbManager = DatabaseManager.getInstance();

        // Дополнительные действия при запуске
        System.out.println("Программа запущена. Проверьте папку APPDATA и файл логов.");



        // Создаем и отображаем форму
        General form = new General();
        form.setVisible(true);

    }
}