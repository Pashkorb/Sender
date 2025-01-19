package org.example;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.swing.*;
import java.awt.*;
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
    private JTable table1;
    private JRadioButton radioButton1;
    private JButton SendButton;//кнопка отправки
    private JButton OpenPort;//кнопка открытия порта
    private JButton closePortButton;//кнопка закрытия порта
    private JComboBox portComboBox;//выбор сериал порта
    private JList fileList;//список всех файлов
    private JButton selectFolderButton;//
    private JLabel LabelCount;//поле количества осталось на печать
    private JTextField CountTextPanel;//ввод количества на печать
    private JCheckBox CountPrint;//галочка нужна ли печать по количеству
    private JButton StopPrint;//кнопка остановить печать
    private JLabel statusLabel;//вывод статуса
    private JButton StatusButton;//кнопка получить статус
    private JRadioButton radioButtonSerial;//выбор сериал порта
    private JRadioButton radioButtonEthernet;//выбор езернета
    private JTextField TextField_IP; //Апи принтера(для езернет)
    private JTextField textField_PrinterPort; // Порт принтера(для езернет)
    private SerialPort selectedPort; // Выбранный порт сериал порта
    private DefaultListModel<String> fileListModel; // Модель для списка
    private String selectedFolderPath = null; // Путь к выбранной папке
    private int remainingCopies = 0; // Количество оставшихся копий
    private EthernetSender ethernetSender; // Instance to manage Ethernet connection
    private JButton AddString;//кнопка добавить строку из файла
    private JButton removeString;//кнопка далить одну строку
    private JButton SaveMess;//кнопка сохранить в текстовый файл сообщение
    private JTextArea TotalMessage;//итоговое сообщение
    private JButton AddAllStrings;//кнопка Добавить все строки из файла

    public Sender() {
        initializeComponents(); // Инициализация всех компонентов
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

        // Создание панелей
        JPanel radioPanel = createRadioPanel();
        JPanel connectionPanel = createConnectionPanel();
        JPanel filePanel = createFilePanel();
        JPanel messagePanel = createMessagePanel();
        JPanel buttonPanel = createButtonPanel();

        // Главная панель с GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Добавление панелей на главную панель
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(radioPanel, gbc);

        gbc.gridy = 1;
        mainPanel.add(connectionPanel, gbc);

        gbc.gridy = 2;
        mainPanel.add(filePanel, gbc);

        gbc.gridy = 3;
        mainPanel.add(messagePanel, gbc);

        gbc.gridy = 4;
        mainPanel.add(buttonPanel, gbc);

        setContentPane(mainPanel);

        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            portComboBox.addItem(port.getSystemPortName());
        }

        SendButton.addActionListener(new ActionListener() {
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
        removeString.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeString();
            }
        });
        SaveMess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMess();
            }
        });
        AddAllStrings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAllStrings();
            }
        });
        AddString.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addString();
            }
        });
        radioButtonSerial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editView(true);
            }
        });
        radioButtonEthernet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editView(false);
            }
        });
    }

    private void saveMess() {
        if (TotalMessage.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Нет данных для сохранения!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить сообщение");
        int userSelection = fileChooser.showSaveDialog(Sender.this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".txt")) {
                fileToSave = new File(filePath + ".txt"); // Добавляем расширение .txt, если его нет
            }
            try {
                Files.write(fileToSave.toPath(), TotalMessage.getText().getBytes(StandardCharsets.UTF_8));
                JOptionPane.showMessageDialog(null, "Сообщение успешно сохранено!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                if (selectedFolderPath != null) {
                    loadFilesFromFolder(selectedFolderPath); // Обновляем список файлов
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Ошибка при сохранении файла", ex);
                JOptionPane.showMessageDialog(null, "Ошибка при сохранении файла: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private void addAllStrings() {
        if (fileList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Выберите файл из списка!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedFilePath = fileListModel.getElementAt(fileList.getSelectedIndex());
        File file = new File(selectedFilePath);

        // Проверяем, существует ли файл
        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "Файл не найден: " + selectedFilePath, "Ошибка", JOptionPane.ERROR_MESSAGE);
            fileListModel.remove(fileList.getSelectedIndex()); // Удаляем файл из списка, так как он больше не существует
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(selectedFilePath), StandardCharsets.UTF_8);
            for (String line : lines) {
                if (!TotalMessage.getText().contains(line)) { // Проверяем, что строка еще не добавлена
                    TotalMessage.append(line + "\n"); // Добавляем строку в итоговое сообщение
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Ошибка при чтении файла", ex);
            JOptionPane.showMessageDialog(null, "Ошибка при чтении файла: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    
    }

    private void removeString() {
        String currentText = TotalMessage.getText();
        if (currentText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Нет строк для удаления!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] lines = currentText.split("\n");
        if (lines.length > 0) {
            StringBuilder newText = new StringBuilder();
            for (int i = 0; i < lines.length - 1; i++) { // Пропускаем последнюю строку
                newText.append(lines[i]).append("\n"); // Добавляем строку и символ новой строки
            }
            TotalMessage.setText(newText.toString()); // Обновляем текст
        }
    }



    private void addString() {
        if (fileList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Выберите файл из списка!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedFilePath = fileListModel.getElementAt(fileList.getSelectedIndex());
        try {
            List<String> lines = Files.readAllLines(Paths.get(selectedFilePath), StandardCharsets.UTF_8);
            for (String line : lines) {
                if (!TotalMessage.getText().contains(line)) { // Проверяем, что строка еще не добавлена
                    TotalMessage.append(line + "\n"); // Добавляем строку в итоговое сообщение
                    break; // Добавляем только первую неиспользованную строку
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Ошибка при чтении файла", ex);
            JOptionPane.showMessageDialog(null, "Ошибка при чтении файла: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeComponents() {
        // Инициализация всех компонентов
        SendButton = new JButton("Send");
        OpenPort = new JButton("Open Port");
        closePortButton = new JButton("Close Port");
        selectFolderButton = new JButton("Select Folder");
        StopPrint = new JButton("Stop Print");
        removeString = new JButton("Remove String");
        SaveMess = new JButton("Save Message");
        AddAllStrings = new JButton("Add All Strings");
        AddString = new JButton("Add String");
        StatusButton = new JButton("Status");

        TextField_IP = new JTextField(15);
        textField_PrinterPort = new JTextField(5);
        CountTextPanel = new JTextField(5);
        TotalMessage = new JTextArea(10, 30);

        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);

        portComboBox = new JComboBox<>();

        radioButtonSerial = new JRadioButton("Serial");
        radioButtonEthernet = new JRadioButton("Ethernet");
        ButtonGroup connectionGroup = new ButtonGroup();
        connectionGroup.add(radioButtonSerial);
        connectionGroup.add(radioButtonEthernet);

        CountPrint = new JCheckBox("Print by Count");

        LabelCount = new JLabel("Осталось копий: 0");
        statusLabel = new JLabel("Status: Not Connected");

        // Главная панель с GridBagLayout
        contentPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Настройка отступов и выравнивания
        gbc.insets = new Insets(2, 2, 2, 2); // Минимальные отступы
        gbc.anchor = GridBagConstraints.NORTHWEST; // Выравнивание в левом верхнем углу
        gbc.fill = GridBagConstraints.BOTH; // Растягиваем компоненты по горизонтали и вертикали

        // Добавление панелей на главную панель
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Занимает две колонки
        contentPane.add(createRadioPanel(), gbc);

        gbc.gridy = 1;
        contentPane.add(createConnectionPanel(), gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1; // Сбрасываем ширину
        contentPane.add(createFilePanel(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0; // Растягиваем по горизонтали
        gbc.weighty = 1.0; // Растягиваем по вертикали
        contentPane.add(createMessagePanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0; // Сбрасываем растягивание
        gbc.weighty = 0; // Сбрасываем растягивание
        contentPane.add(createButtonPanel(), gbc);

        // Добавляем галочку и поле с количеством на панель
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        countPanel.add(CountPrint);
        countPanel.add(new JLabel("Количество:"));
        countPanel.add(CountTextPanel);
        countPanel.add(LabelCount);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(countPanel, gbc);
    }

    private JPanel createRadioPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2)); // Минимальные отступы
        panel.add(radioButtonSerial);
        panel.add(radioButtonEthernet);
        return panel;
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2); // Минимальные отступы
        gbc.anchor = GridBagConstraints.WEST;

        // Увеличение ширины выпадающего списка портов
        portComboBox.setPreferredSize(new Dimension(200, portComboBox.getPreferredSize().height));

        // Добавление метки и выпадающего списка портов
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Port:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(portComboBox, gbc);

        // Добавление метки и поля для IP-адреса
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("IP Address:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(TextField_IP, gbc);

        // Добавление метки и поля для порта принтера
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Printer Port:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(textField_PrinterPort, gbc);

        // Добавление кнопок справа
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(OpenPort, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(closePortButton, gbc);

        // Добавление галочки и поля с количеством
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Занимает две колонки
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        countPanel.add(CountPrint);
        countPanel.add(new JLabel("Количество:"));
        countPanel.add(CountTextPanel);
        countPanel.add(LabelCount);
        panel.add(countPanel, gbc);

        return panel;
    }
    private JPanel createFilePanel() {
        JPanel panel = new JPanel(new BorderLayout(2, 2)); // Минимальные отступы
        panel.add(new JScrollPane(fileList), BorderLayout.CENTER);
        panel.add(selectFolderButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout(2, 2)); // Минимальные отступы
        panel.add(new JScrollPane(TotalMessage), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(AddAllStrings);
        buttonPanel.add(AddString);
        buttonPanel.add(removeString);
        buttonPanel.add(SaveMess);

        panel.add(buttonPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2)); // Минимальные отступы
        panel.add(SendButton);
        panel.add(StatusButton);
        panel.add(StopPrint);
        return panel;
    }
    public void editView(boolean isSerial) {
        // Если выбран Serial, скрываем поля для Ethernet
        TextField_IP.setVisible(!isSerial);
        textField_PrinterPort.setVisible(!isSerial);

        // Если выбран Ethernet, скрываем поля для Serial
        portComboBox.setVisible(isSerial);

        // Перерисовываем панель, чтобы применить изменения
        contentPane.revalidate();
        contentPane.repaint();
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
            } else if (radioButtonEthernet.isSelected()) {
                String ipAddress = TextField_IP.getText();
                int port = Integer.parseInt(textField_PrinterPort.getText());

                if (!ethernetSender.isConnected()) {
                    JOptionPane.showMessageDialog(null, "Принтер не подключен!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            System.out.println("[INFO] Порт открыт и готов к использованию.");
            remainingCopies = Integer.parseInt(CountTextPanel.getText());

            // Получаем текст из итогового поля
            String message = TotalMessage.getText();
            if (message == null || message.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Итоговое сообщение пусто!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                System.out.println("[ERROR] Итоговое сообщение пусто.");
                return;
            }
            System.out.println("[INFO] Итоговое сообщение получено.");

            // Разделяем текст на строки
            List<String> messageLines = List.of(message.split("\n"));
            System.out.println("[INFO] Сообщение успешно разделено на строки. Количество строк: " + messageLines.size());

            // Подготовка команды для печати
            Coder coder = new Coder();
            PrinterCommand command = coder.preparePrintCommand(messageLines, "02"); // Используем код функции "02"
            System.out.println("[INFO] Команда для печати успешно подготовлена.");

            // Подготовка команды для отправки
            String preparedCommand = coder.prepareCommandForSending(command);

            // Отправка команды
            if (radioButtonSerial.isSelected()) {
                PrintSender.setSerialPort(selectedPort);
                PrintSender.send(preparedCommand.getBytes(StandardCharsets.US_ASCII)); // Передаем массив байтов
                JOptionPane.showMessageDialog(null, "Данные успешно отправлены!");
            } else if (radioButtonEthernet.isSelected()) {
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



