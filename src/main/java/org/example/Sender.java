package org.example;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender extends JFrame implements SerialPortDataListener{
    private static final Logger logger = Logger.getLogger(Sender.class.getName());


    private JPanel contentPane;
    private JButton кнопкаButton;
    private JButton OpenPort;
    private JButton closePortButton;
    private JComboBox portComboBox;
    private JList fileList;
    private JButton selectFolderButton;
    private JLabel LabelCount;
    private JTextPane CountTextPanel;
    private JCheckBox CountPrint;
    private JButton StopPrint;
    private JLabel statusLabel;
    private JButton StatusButton;
    private JRadioButton radioButtonSerial;
    private JRadioButton radioButtonEthernet;
    private JTextField TextField_IP; //Апи принтера
    private JTextField textField_PrinterPort; // Порт принтера
    private JTextField textField_PCPort;//Порт компьютера
    private SerialPort selectedPort; // Выбранный порт
    private DefaultListModel<String> fileListModel; // Модель для списка
    private String selectedFolderPath = null; // Путь к выбранной папке
    private int remainingCopies = 0; // Количество оставшихся копий
    private EthernetSender ethernetSender; // Instance to manage Ethernet connection

    public Sender() {
        setContentPane(contentPane);
        fileListModel = new DefaultListModel<>();
        fileList.setModel(fileListModel);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при установке LookAndFeel", e);
        }

        setTitle("Sender - Индустрия маркировки");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            portComboBox.addItem(port.getSystemPortName());
        }

        кнопкаButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendData();
            }
        });

        OpenPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPort();
            }
        });

        closePortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closePort();
            }
        });

        selectFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFolder();
            }
        });

        StopPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendStopCommand();
            }
        });

        radioButtonSerial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(radioButtonSerial.isSelected()){
                    radioButtonEthernet.setSelected(false);
                }
            }
        });
        radioButtonEthernet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(radioButtonEthernet.isSelected()){
                    radioButtonSerial.setSelected(false);
                }
            }
        });
    }

    private void sendData() {
        try {
            if (radioButtonSerial.isSelected()) {

                // Логирование: Начало выполнения метода
                System.out.println("[INFO] Начало отправки данных.");

                // Проверка, открыт ли порт
                if (selectedPort == null || !selectedPort.isOpen()) {
                    JOptionPane.showMessageDialog(null, "Порт не выбран или не открыт!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    System.out.println("[ERROR] Порт не выбран или не открыт.");
                    return;
                }
            }else if (radioButtonEthernet.isSelected()) {
                String ipAddress = TextField_IP.getText();
                int port = Integer.parseInt(textField_PrinterPort.getText());

                if (!ethernetSender.isConnected()) {
                    JOptionPane.showMessageDialog(null, "Принтер не подключен!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            System.out.println("[INFO] Порт открыт и готов к использованию.");
            remainingCopies=Integer.parseInt(CountTextPanel.getText());
            // Выбор файла для печати
            String selectedFilePath = (String) fileList.getSelectedValue();
            if (selectedFilePath == null) {
                JOptionPane.showMessageDialog(null, "Выберите файл из списка!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                System.out.println("[ERROR] Файл для печати не выбран.");
                return;
            }
            System.out.println("[INFO] Выбран файл для печати: " + selectedFilePath);

            // Чтение файла построчно
            List<String> fileLines = Files.readAllLines(Paths.get(selectedFilePath));
            System.out.println("[INFO] Файл успешно прочитан. Количество строк: " + fileLines.size());

            // Подготовка команды для печати
            Coder coder = new Coder();
            PrinterCommand command = coder.preparePrintCommand(fileLines, "02"); // Используем код функции "02"
            System.out.println("[INFO] Команда для печати успешно подготовлена.");

            // Подготовка команды для отправки
            String preparedCommand = coder.prepareCommandForSending(command);

            // Отправка команды
            if (radioButtonSerial.isSelected()){
                PrintSender.setSerialPort(selectedPort);
                PrintSender.send(preparedCommand.getBytes(StandardCharsets.US_ASCII)); // Передаем массив байтов
                JOptionPane.showMessageDialog(null, "Данные успешно отправлены!");
            }
            else if (radioButtonEthernet.isSelected()){
                ethernetSender.send(preparedCommand.getBytes(StandardCharsets.US_ASCII));
                JOptionPane.showMessageDialog(null, "Данные успешно отправлены!");
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Ошибка при отправке данных", ex);
            JOptionPane.showMessageDialog(null, "Ошибка при отправке данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPort() {
        try {
            if (radioButtonSerial.isSelected()) {
                String selectedPortName = (String) portComboBox.getSelectedItem();
                if (selectedPortName == null) {
                    JOptionPane.showMessageDialog(null, "Выберите порт из списка!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                for (SerialPort port : SerialPort.getCommPorts()) {
                    if (port.getSystemPortName().equals(selectedPortName)) {
                        selectedPort = port;
                        break;
                    }
                }

                if (selectedPort != null) {
                    if (selectedPort.openPort()) {
                        JOptionPane.showMessageDialog(null, "Порт " + selectedPortName + " успешно открыт!");
                        selectedPort.addDataListener(this); // Добавляем слушатель
                    } else {
                        JOptionPane.showMessageDialog(null, "Не удалось открыть порт " + selectedPortName, "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (radioButtonEthernet.isSelected()) {
                String ipAddress = TextField_IP.getText();
                int port = Integer.parseInt(textField_PrinterPort.getText());

                ethernetSender = new EthernetSender();
                ethernetSender.connect(ipAddress, port);
                JOptionPane.showMessageDialog(null, "Соединение с принтером по IP: " + ipAddress + ":" + port + " установлено.");
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Ошибка при открытии порта", ex);
            JOptionPane.showMessageDialog(null, "Ошибка при открытии порта: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closePort() {
        try {
            if (radioButtonSerial.isSelected()) {
                if (selectedPort == null || !selectedPort.isOpen()) {
                    JOptionPane.showMessageDialog(null, "Порт не открыт!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                selectedPort.closePort();
                JOptionPane.showMessageDialog(null, "Порт успешно закрыт!");
            } else if (radioButtonEthernet.isSelected()) {
                ethernetSender.disconnect();
                JOptionPane.showMessageDialog(null, "Соединение с принтером по Ethernet закрыто.");
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Ошибка при закрытии порта", ex);
            JOptionPane.showMessageDialog(null, "Ошибка при закрытии порта: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(Sender.this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            loadFilesFromFolder(selectedFolder.getAbsolutePath());
        }
    }

    private void loadFilesFromFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Папка " + folderPath + " не существует!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        fileListModel.clear();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(null, "Папка " + folderPath + " пуста!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (File file : files) {
            fileListModel.addElement(file.getAbsolutePath());
        }
        selectedFolderPath = folderPath;
    }


    public static void main(String[] args) {
        Sender frame = new Sender();
        frame.setVisible(true);
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE| SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
    }


    @Override
    public void serialEvent(SerialPortEvent event) {
        try {
            if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                try {
                    // Выжидаем несколько миллисекунд перед чтением данных
                    Thread.sleep(50); // Задержка в 50 миллисекунд
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.log(Level.SEVERE, "Ошибка при задержке", e);
                }

                InputStream inputStream = selectedPort.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);

                // Преобразуем полученные данные в строку
                String receivedData = new String(buffer, 0, bytesRead, StandardCharsets.US_ASCII);
                System.out.println("[INFO] Пришли данные на порт: " + receivedData);



                // Обрабатываем буфер
                handleMessage(receivedData);
            } else if (event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
                System.out.println("[INFO] Порт отключён.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при обработке данных", e);
            JOptionPane.showMessageDialog(null, "Ошибка при обработке данных: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleMessage(String message) {
        // Ищем индексы символов SOI и EOI
        char SOI = (char) 0x7E;
        char EOI = (char) 0x0D;
        int soiIndex = message.indexOf(SOI);
        int eoiIndex = message.indexOf(EOI);

        // Проверяем, найдены ли оба символа и находятся ли они в правильном порядке
        if (soiIndex != -1 && eoiIndex != -1 && eoiIndex > soiIndex) {
            // Извлекаем сообщение между SOI и EOI
            String validMessage = message.substring(soiIndex + 1, eoiIndex);
            System.out.println("Сообщение- "+validMessage);
            // Обрабатываем сообщение
            if (validMessage.contains("08000")) {
                remainingCopies--;
                SwingUtilities.invokeLater(() -> LabelCount.setText("Осталось копий: " + remainingCopies));

                if (remainingCopies <= 0) {
                    SwingUtilities.invokeLater(() -> CountPrint.setSelected(false));
                    sendStopCommand();
                    JOptionPane.showMessageDialog(null, "Печать завершена!");
                }
            } else if (validMessage.length() > 5) {
                SwingUtilities.invokeLater(() -> statusLabel.setText(PrinterStatus.getStatus(validMessage)));
            }
        }
    }
    public void sendStopCommand() {
        try{
            if (selectedPort == null || !selectedPort.isOpen()) {
                JOptionPane.showMessageDialog(null, "Порт не выбран или не открыт!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String functionCode = "03";
            String commandData = "";
            Coder coder=new Coder();
            PrinterCommand command = new PrinterCommand(functionCode, commandData);
            String preparedCommand = coder.prepareCommandForSending(command);
            PrintSender.setSerialPort(selectedPort);
            PrintSender.send(preparedCommand.getBytes(StandardCharsets.US_ASCII)); // Передаем массив байтов
            JOptionPane.showMessageDialog(null, "Данные успешно очищены!");


        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Ошибка при отправке данных", ex);
            JOptionPane.showMessageDialog(null, "Ошибка при отправке данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}



