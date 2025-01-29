package org.example;

import javax.swing.*;

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


    }

    public Support(MainFrame parent){

        this.parent = parent;
        add(mainPanel); // Добавляем панель из дизайнера

        buttonHome.addActionListener(e -> parent.showHome());
        buttonSetting.addActionListener(e -> parent.showSettings());
        buttonAdmin.addActionListener(e->parent.showAdmin());
        buttonPrinter.addActionListener(e->parent.showGeneral());
        buttonSupport.addActionListener(e -> parent.showSupport());
        buttonReport.addActionListener(e -> parent.showReport());
        buttonLogOut.addActionListener(e -> parent.logLogout());

    }
}
