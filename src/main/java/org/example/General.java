package org.example;

import org.example.Service.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class General extends JPanel{
    private JComboBox comboBox_Printers;
    private JTextField textFieldX1;
    private JTextField textFieldX2;
    private JTextField textFieldX0;
    private JCheckBox CheckBox_CountPrint;
    private JTextField textFieldCountPrint;
    private JButton ButtonAddField;
    private JButton ButtonRemoveField;
    private JButton ButtonUppdateDateSample;
    private JButton ButtonStopPrinter;
    private JButton ButtonSendDataForPrinter;
    private JButton ButtonSelectSample;
    private JTextField textFieldNameFieldX0;
    private JButton ButtonRemoveFieldInSample;
    private JButton ButtonAddFieldInSample;
    private JTextField textFieldTextX0;
    private JTextField textFieldTextX2;
    private JTextField textFieldTextX3;
    private JTextField textFieldTextX1;
    private JButton ButtonSaveSample;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JPasswordField textFieldPrintedCount;
    private JTextField textFieldRemaindedPrinting;
    private JLabel LabelNameFieldX0;
    private JLabel LabelNameFieldX1;
    private JLabel LabelNameFieldX2;
    private JLabel LabelNameFieldX3;
    private JTextField textFieldNameFieldX1;
    private JTextField textFieldNameFieldX2;
    private JTextField textFieldNameFieldX3;

    private JPanel Panel1;
    private JPanel Panel2;
    private JPanel Panel3;
    private JPanel Panel4;
    private JPanel Panel5;
    private JPanel Panel6;
    private JPanel Panel7;
    private JPanel Panel8;
    private JPanel Panel9;
    private JPanel Panel10;
    private JPanel Panel11;
    private JPanel Panel12;
    private JPanel Panel13;
    private JPanel Panel14;
    private JPanel Panel15;
    private JPanel Panel16;
    private JPanel Panel17;
    private JPanel Panel18;
    private JPanel Panel19;
    private JPanel Panel20;
    private JPanel Panel21;
    private JPanel Panel22;
    private JPanel Panel23;
    private JTextField textFieldX3;
    private JLabel LabelX3;
    private JLabel LabelX2;
    private JLabel LabelX1;
    private JLabel LabelX0;

    private JPanel mainPanel; // Главная панель из дизайнера
    private MainFrame parent;

    public General(MainFrame parent) {
        this.parent = parent;
        add(mainPanel); // Добавляем панель из дизайнера
//
//        setContentPane(panel1);
//        setTitle("General");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        pack();
        setVisible(true);

            // Присваиваем имена текстовым полям
            textFieldTextX0.setName("textFieldTextX0");
            textFieldTextX1.setName("textFieldTextX1");
            textFieldTextX2.setName("textFieldTextX2");
            textFieldTextX3.setName("textFieldTextX3");


            if (mainPanel == null) {
                System.out.println("Основная панель (panel1) не инициализирована!");
            } else {
                System.out.println("Основная панель (panel1) инициализирована.");
            }

            if (mainPanel == null) {
                System.out.println("Panel1 не инициализирована!");
            } else {
                System.out.println("Panel1 инициализирована.");
            }

            // Аналогично для остальных панелей...

        // Обработчик для кнопки "Сохранить шаблон"
        ButtonSaveSample.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTemplate();
            }
        });

        // Обработчик для кнопки "Выбрать шаблон"
        ButtonSelectSample.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectTemplate();
            }
        });

        ButtonSendDataForPrinter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Проверяем, открыто ли соединение
                if (!PrinterManager.isConnectionOpen()) {
                    JOptionPane.showMessageDialog(null, "Соединение с принтером не установлено!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Собираем данные из текстовых полей

                List<String> printTasks = new ArrayList<>();
                if (!textFieldX0.getText().isEmpty()) {
                    printTasks.add(textFieldX0.getText());
                }
                if (!textFieldX1.getText().isEmpty()) {
                    printTasks.add(textFieldX1.getText());
                }
                if (!textFieldX2.getText().isEmpty()) {
                    printTasks.add(textFieldX2.getText());
                }
                if (!textFieldX3.getText().isEmpty()) {
                    printTasks.add(textFieldX3.getText());
                }

                // Проверяем, есть ли данные для отправки
                if (printTasks.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Нет данных для отправки!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // Подготавливаем команду для печати
                    Coder coder = new Coder();
                    PrinterCommand command = coder.preparePrintCommand(printTasks, "02"); // Используем код функции "02"

                    // Подготавливаем команду для отправки
                    String preparedCommand = coder.prepareCommandForSending(command);
                    System.out.println("[INFO] Подготовленная команда для отправки: " + preparedCommand);

                    // Отправляем данные через PrinterManager
                    byte[] data = preparedCommand.getBytes(StandardCharsets.US_ASCII);
                    PrinterManager.sendData(data);

                    JOptionPane.showMessageDialog(null, "Данные успешно отправлены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка при подготовке или отправке данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showSettings(); // Переключаемся на панель настроек

            }
        });


        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showReport(); // Переключаемся на панель настроек

            }
        });
    }


    // Метод для сохранения шаблона
    private void saveTemplate() {
        String templateName = JOptionPane.showInputDialog("Введите название шаблона:");
        if (templateName == null || templateName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Название шаблона не может быть пустым.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Собираем данные полей с именами и номерами
        Map<String, Map<String, String>> fieldsData = new HashMap<>();
        fieldsData.put("0", Map.of(
                "text", textFieldTextX0.getText(),
                "name", textFieldNameFieldX0.getText()
        ));
        fieldsData.put("1", Map.of(
                "text", textFieldTextX1.getText(),
                "name", textFieldNameFieldX1.getText()
        ));
        fieldsData.put("2", Map.of(
                "text", textFieldTextX2.getText(),
                "name", textFieldNameFieldX2.getText()
        ));
        fieldsData.put("3", Map.of(
                "text", textFieldTextX3.getText(),
                "name", textFieldNameFieldX3.getText()
        ));

        try {
            TemplateManager.saveTemplateWithNames(templateName, fieldsData);
            JOptionPane.showMessageDialog(null, "Шаблон успешно сохранён!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка при сохранении шаблона: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }



    public void loadTemplateData(String templateName) {
        try {
            Map<String, Map<String, String>> fields = TemplateManager.loadTemplateWithFieldNames(templateName);

            // Сбрасываем поля
            resetFields();

            for (Map.Entry<String, Map<String, String>> entry : fields.entrySet()) {
                String number = entry.getKey(); // "0", "1" и т.д.
                Map<String, String> data = entry.getValue();

                switch (number) {
                    case "0":
                        LabelX0.setText("X0: " + data.get("name"));
                        textFieldX0.setText(data.get("text"));
                        textFieldNameFieldX0.setText(data.get("name"));
                        break;
                    case "1":
                        LabelX1.setText("X1: " + data.get("name"));
                        textFieldX1.setText(data.get("text"));
                        textFieldNameFieldX1.setText(data.get("name"));
                        break;
                    case "2":
                        LabelX2.setText("X2: " + data.get("name"));
                        textFieldX2.setText(data.get("text"));
                        textFieldNameFieldX2.setText(data.get("name"));
                        break;
                    case "3":
                        LabelX3.setText("X3: " + data.get("name"));
                        textFieldX3.setText(data.get("text"));
                        textFieldNameFieldX3.setText(data.get("name"));
                        break;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки: " + e.getMessage());
        }
    }

    private void resetFields() {
        // Сброс всех полей и меток
        LabelX0.setText("X0:");
        LabelX1.setText("X1:");
        LabelX2.setText("X2:");
        LabelX3.setText("X3:");

        textFieldX0.setText("");
        textFieldX1.setText("");
        textFieldX2.setText("");
        textFieldX3.setText("");

        textFieldNameFieldX0.setText("");
        textFieldNameFieldX1.setText("");
        textFieldNameFieldX2.setText("");
        textFieldNameFieldX3.setText("");
    }

    private void selectTemplate() {
        System.out.println("[DEBUG] Открытие окна выбора шаблона...");

        // Открываем окно выбора шаблона
        TemplateSelectionDialog dialog = new TemplateSelectionDialog(parent);
        dialog.setVisible(true);

        System.out.println("[DEBUG] Окно выбора шаблона закрыто.");
    }






    public static void main(String[] args) {
        // Создаем экземпляр окна
      //  General frame = new General();

        // Делаем окно видимым
        //frame.setVisible(true);
    }


}
