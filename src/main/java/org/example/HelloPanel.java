package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelloPanel extends JPanel{
    private JPanel mainPanel;
    private JLabel F1;
    private JLabel F2;
    private JButton buttonEnter;
    private JButton buttonHelper;
    private JLabel FastMarking;
    private final Hello parent;

    public HelloPanel(Hello parent) {
        this.parent = parent;
        add(mainPanel); // Добавляем панель из дизайнера
        mainPanel.setPreferredSize(new Dimension(1920, 1080));

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
