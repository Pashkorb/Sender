package org.example.Service;

import javax.swing.*;
import java.awt.*;

public class LicenseInputDialog extends JDialog {
    private final JTextField licenseField;
    private boolean licenseValid = false;

    public LicenseInputDialog(Frame owner) {
        super(owner, "Введите лицензионный ключ", true);
        setSize(400, 150);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridLayout(3, 1));

        JLabel label = new JLabel("Введите лицензионный ключ:");
        panel.add(label);

        licenseField = new JTextField();
        panel.add(licenseField);

        JButton submitButton = new JButton("Активировать");
        submitButton.addActionListener(e -> {
            String licenseKey = licenseField.getText();
            System.out.println("[INFO] Пользователь ввёл лицензионный ключ: " + licenseKey);
            if (LicenseManager.validateLicenseKey(licenseKey)) {
                licenseValid = true;
                System.out.println("[INFO] Лицензионный ключ действителен.");
                dispose();
            } else {
                System.out.println("[ERROR] Лицензионный ключ недействителен.");
                JOptionPane.showMessageDialog(this, "Неверный лицензионный ключ!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(submitButton);

        add(panel);
    }

    public boolean isLicenseValid() {
        return licenseValid;
    }

    public String getLicenseKey() {
        return licenseField.getText();
    }
}