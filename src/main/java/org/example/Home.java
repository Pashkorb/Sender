package org.example;

import org.example.Service.CurrentUser;

import javax.swing.*;
import java.awt.*;

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
        mainframe.setPreferredSize(new Dimension(1920, 1080));

        buttonHome.addActionListener(e -> parent.showHome());
        buttonSetting.addActionListener(e -> parent.showSettings());
        buttonAdmin.addActionListener(e->parent.showAdmin());
        buttonPrinter.addActionListener(e->parent.showGeneral());
        buttonSupport.addActionListener(e -> parent.showSupport());
        buttonReport.addActionListener(e -> parent.showReport());
        buttonLogOut.addActionListener(e -> parent.logLogout());

        buttonSupport.setBorderPainted(false);
        buttonSupport.setContentAreaFilled(false);
        buttonSupport.setFocusPainted(false);
        buttonSupport.setText(""); // Убираем текст, если он есть

        buttonAdmin.setBorderPainted(false);
        buttonAdmin.setContentAreaFilled(false);
        buttonAdmin.setFocusPainted(false);
        buttonAdmin.setText(""); // Убираем текст, если он есть

        buttonHome.setBorderPainted(false);
        buttonHome.setContentAreaFilled(false);
        buttonHome.setFocusPainted(false);
        buttonHome.setText(""); // Убираем текст, если он есть

        buttonReport.setBorderPainted(false);
        buttonReport.setContentAreaFilled(false);
        buttonReport.setFocusPainted(false);
        buttonReport.setText(""); // Убираем текст, если он есть

        buttonPrinter.setBorderPainted(false);
        buttonPrinter.setContentAreaFilled(false);
        buttonPrinter.setFocusPainted(false);
        buttonPrinter.setText(""); // Убираем текст, если он есть

        buttonLogOut.setBorderPainted(false);
        buttonLogOut.setContentAreaFilled(false);
        buttonLogOut.setFocusPainted(false);
        buttonLogOut.setText(""); // Убираем текст, если он есть

        buttonSetting.setBorderPainted(false);
        buttonSetting.setContentAreaFilled(false);
        buttonSetting.setFocusPainted(false);
        buttonSetting.setText(""); // Убираем текст, если он есть
    }


}
