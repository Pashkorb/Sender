package org.example;

import org.example.Service.EmailSender;

import javax.swing.*;
import java.awt.*;
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
        setFontForAllComponents(mainPanel, new Font("SansSerif", Font.PLAIN, 20));

//        setSize(800, 600);

//        setVisible(true);

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

    private void setFontForAllComponents(Container container, Font font) {
        for (Component component : container.getComponents()) {
            // Обрабатываем кнопки отдельно с принудительным обновлением
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                Font currentFont = button.getFont();
                button.setFont(new Font(
                        currentFont.getName(),
                        currentFont.getStyle(),
                        font.getSize()
                ));
                button.revalidate();
                button.repaint();
                continue;
            }

            // Остальная логика обработки
            if (component instanceof JLabel
                    || component instanceof JTextField
                    || component instanceof JPasswordField) {

                component.setFont(font);
            }

            // Рекурсивный обход контейнеров
            if (component instanceof Container) {
                Container childContainer = (Container) component;

                // Особые случаи контейнеров
                if (childContainer instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) childContainer;
                    setFontForAllComponents(scrollPane.getViewport(), font);
                }
                else if (childContainer instanceof JViewport) {
                    setFontForAllComponents((Container) ((JViewport) childContainer).getView(), font);
                }
                else {
                    setFontForAllComponents(childContainer, font);
                }
            }
        }
    }
}