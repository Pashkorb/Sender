package org.example;

import javax.swing.*;
import java.awt.*;

public class Report extends JPanel{
    private JTable table1;
    private JTable table2;
    private JTable table3;
    private MainFrame parent;
    private JPanel mainPanel;

    public Report(MainFrame parent) {
        this.parent = parent;
        add(mainPanel);
    }
}
