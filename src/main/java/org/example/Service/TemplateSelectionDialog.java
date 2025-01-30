package org.example.Service;

import org.example.General;
import org.example.MainFrame;
import org.example.Service.TemplateManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TemplateSelectionDialog extends JDialog {
    private final JComboBox<String> comboBoxTemplates;
    private final JButton buttonSelect;
    private final General parentFrame;

    public TemplateSelectionDialog(MainFrame parent) {
        super(parent, "Выбор шаблона", true);
        this.parentFrame = parent.getGeneralPanel();

        // Создаем панель для компонентов
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Комбо-бокс для выбора шаблона
        comboBoxTemplates = new JComboBox<>();
        panel.add(new JLabel("Выберите шаблон:"));
        panel.add(comboBoxTemplates);

        // Кнопка "Выбрать"
        buttonSelect = new JButton("Выбрать");
        buttonSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSelectTemplate();
            }
        });
        panel.add(buttonSelect);

        // Загружаем шаблоны в комбо-бокс
        loadTemplates();

        // Добавляем панель в окно
        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    // Загрузка шаблонов в комбо-бокс
    private void loadTemplates() {
        try {
            Map<String, Map<String, String>> templates = TemplateManager.loadAllTemplates();
            comboBoxTemplates.removeAllItems(); // Очищаем комбо-бокс

            // Добавляем каждый шаблон в комбо-бокс
            for (String templateName : templates.keySet()) {
                comboBoxTemplates.addItem(templateName);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке шаблонов: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Обработка выбора шаблона
    private void onSelectTemplate() {
        System.out.println("[DEBUG] Начало обработки выбора шаблона...");

        // Получаем выбранный шаблон из комбо-бокса
        String selectedTemplate = (String) comboBoxTemplates.getSelectedItem();
        System.out.println("[DEBUG] Выбранный шаблон: " + selectedTemplate);

        if (selectedTemplate != null) {
            try {
                // Загружаем данные шаблона из базы данных
                System.out.println("[DEBUG] Загрузка данных шаблона...");
                Map<String, Map<String, String>> templateData = TemplateManager.loadTemplateWithFieldNames(selectedTemplate);

                // Логируем данные шаблона
                if (templateData != null) {
                    System.out.println("[DEBUG] Данные из шаблона:");
                    for (Map.Entry<String, Map<String, String>> entry : templateData.entrySet()) {
                        System.out.println("[DEBUG] " + entry.getKey() + ": " + entry.getValue());
                    }
                } else {
                    System.out.println("[DEBUG] Данные шаблона не найдены.");
                }

                // Передаем выбранный шаблон в главное окно
                System.out.println("[DEBUG] Передача выбранного шаблона в главное окно...");
                parentFrame.loadTemplateData(selectedTemplate);

                System.out.println("[DEBUG] Закрытие окна выбора шаблона...");
                dispose(); // Закрываем окно выбора шаблона
            } catch (Exception e) {
                System.out.println("[DEBUG] Ошибка при загрузке данных шаблона: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Ошибка при загрузке шаблона: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("[DEBUG] Шаблон не выбран.");
            JOptionPane.showMessageDialog(this, "Шаблон не выбран.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        System.out.println("[DEBUG] Обработка выбора шаблона завершена.");
    }

}