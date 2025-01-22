package org.example;

import org.example.Service.PrinterManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Setting extends JFrame{
    public JPanel panel1;
    private JTextField textFielLicense;
    private JComboBox comboBoxSerialPort;
    private JCheckBox COMPortCheckBox;
    private JTextField textFieldIPAdress;
    private JCheckBox ethernetCheckBox;
    private JTextField textFieldPrinterPort;
    private JTextField textFieldPCPort;
    private JButton ButtonOpenPort;
    private JButton ButtonClosePort;
    private JTable tablePrinter;
    private JTable tableUsers;
    private JTextPane textPaneErrors;
    private JPanel panel2;
    private JPanel panel3;
    private JPanel panel4;
    private JPanel panel5;
    private JPanel panel6;
    private JPanel panel7;
    private JPanel panel8;
    private JPanel panel9;

    public Setting() {
    setContentPane(panel1); // Устанавливаем panel1 как основную панель
    setTitle("Настройки");
    setSize(600, 400);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    ButtonOpenPort.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (COMPortCheckBox.isSelected()) {
                String portName = (String) comboBoxSerialPort.getSelectedItem();
                PrinterManager.setConnectionType(true);
                PrinterManager.openPort(portName, null, 0);
            } else if (ethernetCheckBox.isSelected()) {
                String ip = textFieldIPAdress.getText();
                int port = Integer.parseInt(textFieldPrinterPort.getText());
                PrinterManager.setConnectionType(false);
                PrinterManager.openPort(null, ip, port);
            }
        }
    });
    ButtonClosePort.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            PrinterManager.closePort();
        }
    });
}
}
