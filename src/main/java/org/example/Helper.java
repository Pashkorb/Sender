package org.example;

import org.example.Service.EmailSender;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Helper extends JPanel{
    private JPanel mainPanel;
    private JTextField textField1; // Поле для темы
    private JComboBox<String> comboBox1; // Выбор типа поддержки
    private JButton отправитьButton; // Кнопка "Отправить"
    private JTextPane textPane1; // Поле для текста обращения

    private MainFrame parent;

    public Helper(MainFrame parent) {
        this.parent = parent;
        add(mainPanel); // Добавляем панель из дизайнера
        // Инициализация формы

        setSize(800, 600);

        setVisible(true);

        // Обработчик для кнопки "Отправить"
        отправитьButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        // Получаем данные из формы
        String subject = textField1.getText(); // Тема
        String supportType = (String) comboBox1.getSelectedItem(); // Тип поддержки
        String body = textPane1.getText(); // Текст обращения

        // Формируем полное тело письма
        String fullBody = "Тип поддержки: " + supportType + "\n\n" + body;
        String to=null;
        // Адрес получателя (техподдержка)
        if (supportType.equals("Айти поддержка")){
             to = "Etppe32@gmail.com";
        }
        else{ to = "Etppe32@gmail.com"; }



        try {
            // Отправляем письмо
            EmailSender.sendEmail(to, subject, fullBody);
            JOptionPane.showMessageDialog(this, "Письмо успешно отправлено!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при отправке письма: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


}