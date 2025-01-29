package org.example;

import org.example.Service.EmailSender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;

public class Support extends JPanel{
    private JLabel LableName;
    private JButton button1;
    private JPanel mainPanel;

    private Hello parent;

    public Support(Hello parent){
        this.parent = parent;
        add(mainPanel); // Добавляем панель из дизайнера


    }
}
