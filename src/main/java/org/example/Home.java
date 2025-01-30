package org.example;

import org.example.Service.CurrentUser;

import javax.swing.*;

public class Home extends JPanel{
    private JButton buttonHome;
    private JButton buttonSetting;
    private JButton buttonAdmin;
    private JButton buttonPrinter;
    private JButton buttonSupport;
    private JButton buttonReport;
    private JLabel LableName;
    private JButton buttonLogOut;
    private JPanel mainframe;
    private final MainFrame parent;

    public Home(MainFrame parent) {
        this.parent=parent;
        add(mainframe);
        LableName.setText(CurrentUser.getName());
        setSize(1920, 1080);

        buttonHome.addActionListener(e -> parent.showHome());
        buttonSetting.addActionListener(e -> parent.showSettings());
        buttonAdmin.addActionListener(e->parent.showAdmin());
        buttonPrinter.addActionListener(e->parent.showGeneral());
        buttonSupport.addActionListener(e -> parent.showSupport());
        buttonReport.addActionListener(e -> parent.showReport());
        buttonLogOut.addActionListener(e -> parent.logLogout());


    }


}
