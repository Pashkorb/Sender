package org.example;

import javax.swing.*;
import java.time.LocalDate;

public class Admin extends JPanel{
    private JPanel mainPanel;
    private JButton buttonHome;
    private JButton buttonSetting;
    private JButton buttonAdmin;
    private JButton buttonPrinter;
    private JButton buttonSupport;
    private JButton buttonReport;
    private JLabel LableName;
    private JButton buttonLogOut;
    private JTextField textField1;
    private JTextField textField2;
    private JButton активироватьButton;
    private JButton новыйПользовательButton;
    private JTable table1;
    private JTable table2;
    private MainFrame parent;
    private LocalDate date;

    public Admin(MainFrame parent, LocalDate date) {
        this.parent = parent;
        this.date = date;
        add(mainPanel); // Добавляем панель из дизайнера
    }
}
