package org.example;

import org.example.Service.EmailSender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Support extends JPanel{
    private JLabel LableName;
    private JButton buttonLogOut;
    private JPanel mainPanel;
    private JButton buttonHome;
    private JButton buttonSetting;
    private JButton buttonAdmin;
    private JButton buttonPrinter;
    private JButton buttonSupport;
    private JButton buttonReport;
    private JTextField textField1;
    private JComboBox comboBox1;
    private JTextArea textArea1;
    private JButton отправитьButton;

    private Hello parentH;

    private MainFrame parent;

    public Support(Hello parent){
        this.parentH = parent;
        add(mainPanel); // Добавляем панель из дизайнера
        initComponents();

    }

    public Support(MainFrame parent){

        this.parent = parent;
        add(mainPanel); // Добавляем панель из дизайнера
        mainPanel.setPreferredSize(new Dimension(1920, 1080));

        buttonHome.addActionListener(e -> parent.showHome());
        buttonSetting.addActionListener(e -> parent.showSettings());
        buttonAdmin.addActionListener(e->parent.showAdmin());
        buttonPrinter.addActionListener(e->parent.showGeneral());
        buttonSupport.addActionListener(e -> parent.showSupport());
        buttonReport.addActionListener(e -> parent.showReport());
        buttonLogOut.addActionListener(e -> parent.logLogout());
        initComponents();
    }

    private void initComponents() {
        // Настройка шрифтов

        // Заполнение комбобокса
        comboBox1.setModel(new DefaultComboBoxModel<>(new String[]{
                "Айти поддержка",
                "Техническая поддержка"
        }));

        // Обработчик отправки
        отправитьButton.addActionListener(this::sendEmail);
    }

    private void sendEmail(ActionEvent e) {
        String subject = textField1.getText();
        String supportType = (String) comboBox1.getSelectedItem();
        String body = textArea1.getText();

        String fullBody = "Тип поддержки: " + supportType + "\n\n" + body;
        String to = supportType.equals("Айти поддержка")
                ? "support@mirmarking.ru"
                : "service@mirmarking.ru";

        try {
            EmailSender.sendEmail(to, subject, fullBody);
            JOptionPane.showMessageDialog(this, "Письмо отправлено!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
