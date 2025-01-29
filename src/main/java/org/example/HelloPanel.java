package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelloPanel extends JPanel{
    private JPanel mainPanel;
    private JLabel F1;
    private JLabel F2;
    private JButton buttonEnter;
    private JButton buttonHelper;
    private JLabel FastMarking;
    private Hello parent;

    public HelloPanel(Hello parent) {
        this.parent = parent;
        add(mainPanel); // Добавляем панель из дизайнера
        buttonHelper.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showSupport();

            }
        });
        buttonEnter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                parent.enter();

            }
        });
    }
}
