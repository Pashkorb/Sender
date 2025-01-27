package org.example;

import org.example.Model.PrinterDataListener;
import org.example.Service.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class General extends JPanel implements PrinterDataListener {
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
    private JTextField textFieldNameFieldX1;
    private JTextField textFieldNameFieldX2;
    private JTextField textFieldNameFieldX3;

    private int visibleFieldsCount = 3; // Начальное количество видимых полей


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
    private JPanel Panel15;
    private JPanel Panel21;
    private JPanel Panel22;
    private int remainingCopies;
    private JPanel Panel23;
    private JTextField textFieldX3;
    private JLabel LabelX3;
    private JLabel LabelX2;
    private JLabel LabelX1;
    private JLabel LabelX0;

    private JPanel mainPanel; // Главная панель из дизайнера
    private JTextField textFieldX5;
    private JLabel LabelX5;
    private JTextField textFieldTextX4;

    private JTextField textFieldX4;

    private JLabel LabelX4;
    private JTextField textFieldTextX5;
    private JTextField textFieldNameFieldX4;
    private JTextField textFieldNameFieldX5;
    private JPanel JPanelX3;
    private JPanel JPanelX4;
    private JPanel JPanelX5;
    private JPanel Panel13;
    private JLabel LabelNameFieldX0;
    private JPanel Panel14;
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
        setFontForAllComponents(mainPanel, new Font("SansSerif", Font.PLAIN, 16));

        // Убираем границы и фон кнопок
        button1.setBorderPainted(false);
        button1.setContentAreaFilled(false);
        button1.setFocusPainted(false);
        button1.setText(""); // Убираем текст, если он есть

        button2.setBorderPainted(false);
        button2.setContentAreaFilled(false);
        button2.setFocusPainted(false);
        button2.setText("");

        button3.setBorderPainted(false);
        button3.setContentAreaFilled(false);
        button3.setFocusPainted(false);
        button3.setText("");

        button4.setBorderPainted(false);
        button4.setContentAreaFilled(false);
        button4.setFocusPainted(false);
        button4.setText("");


        // Присваиваем имена текстовым полям
            textFieldTextX0.setName("textFieldTextX0");
            textFieldTextX1.setName("textFieldTextX1");
            textFieldTextX2.setName("textFieldTextX2");
            textFieldTextX3.setName("textFieldTextX3");

        setFieldsVisibility();
        
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
        PrinterManager.addDataListener(this);

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

                if (CheckBox_CountPrint.isSelected()){
                    remainingCopies = Integer.parseInt(textFieldCountPrint.getText());
                    textFieldRemaindedPrinting.setText(String.valueOf(remainingCopies));
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
                if (!textFieldX4.getText().isEmpty()) {
                    printTasks.add(textFieldX4.getText());
                }
                if (!textFieldX5.getText().isEmpty()) {
                    printTasks.add(textFieldX5.getText());
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
                    Logger.getInstance().log("[INFO] Подготовленная команда для отправки: " + preparedCommand);
                    System.out.println("[INFO] Подготовленная команда для отправки: " + preparedCommand);


                    // Отправляем данные через PrinterManager
                    byte[] data = preparedCommand.getBytes(StandardCharsets.US_ASCII);
                    PrinterManager.sendData(data);

                    Logger.getInstance().log("[Send] "+preparedCommand);

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
        ButtonStopPrinter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrinterManager.sendStopCommand();
            }
        });
        // Обработчики для кнопок добавления/удаления полей
        ButtonAddField.addActionListener(e -> {
            if (visibleFieldsCount < 6) { // Максимум 6 полей (X0-X5)
                visibleFieldsCount++;
                setFieldsVisibility();
            }
        });

        ButtonRemoveField.addActionListener(e -> {
            if (visibleFieldsCount > 3) { // Минимум 3 поля
                visibleFieldsCount--;
                setFieldsVisibility();
            }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showSupport();
            }
        });
        button4.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {
                String username = CurrentUser.getLogin();
                Logger.getInstance().logLogout(username);
                CurrentUser.clear();
            }
        });
    }
    void loadPrintersToComboBox() {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Наименование FROM Принтеры")) {

            comboBox_Printers.removeAllItems(); // Очищаем список
            while (rs.next()) {
                comboBox_Printers.addItem(rs.getString("Наименование"));
            }

            System.out.println("[DEBUG] Загружено принтеров: " + comboBox_Printers.getItemCount());
        } catch (SQLException ex) {
            System.err.println("Ошибка загрузки принтеров: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Ошибка загрузки списка принтеров",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
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

    private void setFieldsVisibility() {
        // X0, X1, X2 всегда видимы
        boolean x3Visible = visibleFieldsCount >= 4;
        boolean x4Visible = visibleFieldsCount >= 5;
        boolean x5Visible = visibleFieldsCount >= 6;

        JPanelX3.setVisible(x3Visible);
        textFieldX3.setVisible(x3Visible);
        LabelX3.setVisible(x3Visible);
        textFieldNameFieldX3.setVisible(x3Visible);
        textFieldTextX3.setVisible(x3Visible);

        JPanelX4.setVisible(x4Visible);
        textFieldX4.setVisible(x4Visible);
        LabelX4.setVisible(x4Visible);
        textFieldNameFieldX4.setVisible(x4Visible);
        textFieldTextX4.setVisible(x4Visible);

        JPanelX5.setVisible(x5Visible);
        textFieldX5.setVisible(x5Visible);
        LabelX5.setVisible(x5Visible);
        textFieldNameFieldX5.setVisible(x5Visible);
        textFieldTextX5.setVisible(x5Visible);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    @Override
    public void onDataReceived(String data) {
        SwingUtilities.invokeLater(() -> handlePrinterResponse(data));
    }

    @Override
    public void onStatusUpdate(String status) {
        SwingUtilities.invokeLater(() -> updateStatusLabel(status));
    }

    private void handlePrinterResponse(String data) {
        if (data.contains("08000")&&CheckBox_CountPrint.isSelected()) {
            remainingCopies--;
            textFieldRemaindedPrinting.setText(String.valueOf(remainingCopies));

            if (remainingCopies <= 0) {
                PrinterManager.sendStopCommand();
                JOptionPane.showMessageDialog(this, "Печать завершена!");
            }
        }
        // Добавьте другие обработчики статусов
    }

    private void updateStatusLabel(String status) {
        // Обновление статусной метки
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
        for (int i = 0; i < visibleFieldsCount; i++) {
            switch (i) {
                case 0:
                    fieldsData.put("0", Map.of(
                            "text", textFieldTextX0.getText(),
                            "name", textFieldNameFieldX0.getText()
                    ));
                    break;
                case 1:
                    fieldsData.put("1", Map.of(
                            "text", textFieldTextX1.getText(),
                            "name", textFieldNameFieldX1.getText()
                    ));
                    break;
                case 2:
                    fieldsData.put("2", Map.of(
                            "text", textFieldTextX2.getText(),
                            "name", textFieldNameFieldX2.getText()
                    ));
                    break;
                case 3:
                    fieldsData.put("3", Map.of(
                            "text", textFieldTextX3.getText(),
                            "name", textFieldNameFieldX3.getText()
                    ));
                    break;
                case 4:
                    fieldsData.put("4", Map.of(
                            "text", textFieldTextX4.getText(),
                            "name", textFieldNameFieldX4.getText()
                    ));
                    break;
                case 5:
                    fieldsData.put("5", Map.of(
                            "text", textFieldTextX5.getText(),
                            "name", textFieldNameFieldX5.getText()
                    ));
                    break;
            }
        }

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
            resetFields();

            int maxFieldNumber = -1;

            // Находим максимальный номер поля в шаблоне
            for (String key : fields.keySet()) {
                try {
                    int num = Integer.parseInt(key);
                    if(num > maxFieldNumber) maxFieldNumber = num;
                } catch (NumberFormatException ex) {
                    // Пропускаем некорректные ключи
                }
            }

            // Устанавливаем количество видимых полей
            visibleFieldsCount = (maxFieldNumber == -1) ? 3 : maxFieldNumber + 1;

            // Ограничиваем максимальное количество полей
            if(visibleFieldsCount > 6) visibleFieldsCount = 6;

            // Загружаем данные для каждого поля
            for (Map.Entry<String, Map<String, String>> entry : fields.entrySet()) {
                String number = entry.getKey();
                Map<String, String> data = entry.getValue();

                switch(number) {
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
                    case "4":
                        LabelX4.setText("X4: " + data.get("name"));
                        textFieldX4.setText(data.get("text"));
                        textFieldNameFieldX4.setText(data.get("name"));
                        break;
                    case "5":
                        LabelX5.setText("X5: " + data.get("name"));
                        textFieldX5.setText(data.get("text"));
                        textFieldNameFieldX5.setText(data.get("name"));
                        break;
                }
            }

            setFieldsVisibility(); // Обновляем видимость полей
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки: " + e.getMessage());
            e.printStackTrace();
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
