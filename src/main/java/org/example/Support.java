package org.example;

import javax.swing.*;

public class Support extends JPanel{
    private JLabel LableName;
    private JButton button1;
    private JPanel mainPanel;
    private JButton buttonHome;
    private JButton buttonSetting;
    private JButton buttonAdmin;
    private JButton buttonPrinter;
    private JButton buttonSupport;
    private JButton buttonReport;

    private Hello parent;

    public Support(Hello parent){
        this.parent = parent;
        add(mainPanel); // Добавляем панель из дизайнера


    }
}
