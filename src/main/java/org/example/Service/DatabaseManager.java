package org.example.Service;

import org.example.Model.TemplateField;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String APP_DATA_FOLDER = "FastMarking";
    private static final String DB_FILE_NAME = "fastmarking.db";
    private static final String LOG_FILE_NAME = "ЛОГИ.txt";
    private static DatabaseManager instance;
    private static Path dbFilePath;
    private Path logFilePath;

    private DatabaseManager() {
        try {
            // Регистрация драйвера SQLite
            Class.forName("org.sqlite.JDBC");

            // Получаем путь к папке APPDATA
            String userHome = System.getProperty("user.home");
            Path appDataPath = Paths.get(userHome, "AppData", "Roaming", APP_DATA_FOLDER);

            // Создаём папку, если она не существует
            if (!Files.exists(appDataPath)) {
                Files.createDirectories(appDataPath);
                log("Папка " + APP_DATA_FOLDER + " создана в " + appDataPath);
            }

            // Создаём файл логов
            logFilePath = appDataPath.resolve(LOG_FILE_NAME);
            if (!Files.exists(logFilePath)) {
                Files.createFile(logFilePath);
                log("Файл логов " + LOG_FILE_NAME + " создан.");
            }

            // Получаем путь к файлу базы данных
            dbFilePath = appDataPath.resolve(DB_FILE_NAME);

            // Создаём файл базы данных, если он не существует
            if (!Files.exists(dbFilePath)) {
                Files.createFile(dbFilePath);
                log("Файл базы данных " + DB_FILE_NAME + " создан.");
                initializeDatabase(); // Инициализируем базу данных
            }
        } catch (Exception e) {
            // Логируем ошибку, если что-то пошло не так
            System.err.println("Ошибка при инициализации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Метод для получения соединения с базой данных
    public Connection getConnection() throws SQLException {
        if (dbFilePath == null) {
            throw new SQLException("Путь к файлу базы данных не инициализирован.");
        }
        // Добавляем параметр busy_timeout
        return DriverManager.getConnection(
                "jdbc:sqlite:file:" + dbFilePath + "?busy_timeout=1000"
        );
    }
    private void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath.toString())) {
            Statement statement = connection.createStatement();

            // Создаём таблицу Пользователи
            statement.execute("CREATE TABLE IF NOT EXISTS Пользователи (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Фио TEXT NOT NULL, " +
                    "Роль TEXT NOT NULL, " +
                    "Логин TEXT NOT NULL UNIQUE, " +
                    "Пароль TEXT NOT NULL, " +
                    "Доступ BOOLEAN NOT NULL DEFAULT TRUE)");
            log("Таблица 'Пользователи' создана или уже существует.");
// Проверяем и создаём администратора
            if (isFirstRun(connection)) {
                createAdminUser(connection);
            }
            // Создаём таблицу ЖурналАвторизаций
            statement.execute("CREATE TABLE IF NOT EXISTS ЖурналАвторизаций (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Пользователь_id INTEGER NOT NULL, " +
                    "ТипСобытия TEXT NOT NULL CHECK(ТипСобытия IN ('Вход', 'Выход')), " +
                    "ДатаВремя TEXT NOT NULL, " +
                    "FOREIGN KEY (Пользователь_id) REFERENCES Пользователи(id))");
            log("Таблица 'ЖурналАвторизаций' создана или уже существует.");

            statement.execute("CREATE TABLE IF NOT EXISTS ЗаданияПечати (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Пользователь_id INTEGER NOT NULL, " +
                    "Принтер_id INTEGER NOT NULL, " +
                    "ДатаВремяПечати TEXT NOT NULL, " +
                    "Сообщение TEXT, " +
                    "FOREIGN KEY (Пользователь_id) REFERENCES Пользователи(id)," +
                    "FOREIGN KEY (Принтер_id) REFERENCES Принтеры(id))");
            log("Таблица 'ЗаданияПечати' создана или уже существует.");

            // Создаём таблицу Принтеры
            statement.execute("CREATE TABLE IF NOT EXISTS Принтеры (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Наименование TEXT NOT NULL, " +
                    "Серия TEXT NOT NULL, " +
                    "КоличествоСимволов INTEGER NOT NULL, " +
                    "ЧасыРаботы INTEGER NOT NULL)");
            log("Таблица 'Принтеры' создана или уже существует.");

            // Создаём таблицу Ошибки
            statement.execute("CREATE TABLE IF NOT EXISTS Ошибки (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Текст TEXT NOT NULL, " +
                    "ДатаВремя TEXT NOT NULL)");
            log("Таблица 'Ошибки' создана или уже существует.");

            // Создаём таблицу Шаблоны
            statement.execute("CREATE TABLE IF NOT EXISTS Шаблоны (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Наименование TEXT NOT NULL, " +
                    "Поля TEXT NOT NULL)"); // Добавляем столбец Поля
            log("Таблица 'Шаблоны' создана или уже существует.");

            // Создаём таблицу Поля
            statement.execute("CREATE TABLE IF NOT EXISTS Поля (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Шаблон_id INTEGER NOT NULL, " +
                    "Номер INTEGER NOT NULL, " +
                    "Наименование_поля TEXT NOT NULL, " +
                    "Текст TEXT NOT NULL, " +
                    "FOREIGN KEY (Шаблон_id) REFERENCES Шаблоны(id))");
            log("Таблица 'Поля' создана или уже существует.");

            log("База данных инициализирована. Все таблицы созданы.");
        } catch (Exception e) {
            log("Ошибка при инициализации базы данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isFirstRun(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Пользователи")) {
            return rs.getInt(1) == 0;
        }
    }

    private void createAdminUser(Connection connection) {
        String sql = "INSERT INTO Пользователи (Фио, Роль, Логин, Пароль) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) { // Используем try-with-resources
//            String password = "@MirMarking_Prog";
            String password = "11";
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            pstmt.setString(1, "");
            pstmt.setString(2, "Admin");
//            pstmt.setString(3, "admin");
            pstmt.setString(3, "11");
            pstmt.setString(4, hashedPassword);
            pstmt.executeUpdate();
            log("Администратор создан");
        } catch (Exception e) {
            log("Ошибка создания администратора: " + e.getMessage());
        }
    }

    private void log(String message) {
        if (logFilePath == null) {
            System.err.println("Ошибка: путь к файлу логов не инициализирован.");
            return;
        }
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String logMessage = "[" + timestamp + "] " + message + "\n";
            Files.write(logFilePath, logMessage.getBytes(), java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<TemplateField> loadTemplateFields(String templateName) throws SQLException {
        List<TemplateField> fields = new ArrayList<>();
        String sql = "SELECT p.Номер, p.Наименование_поля, p.Текст " +
                "FROM Поля p " +
                "JOIN Шаблоны s ON p.Шаблон_id = s.id " +
                "WHERE s.Наименование = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, templateName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                fields.add(new TemplateField(
                        rs.getInt("Номер"),
                        rs.getString("Наименование_поля"),
                        rs.getString("Текст")
                ));
            }
        }
        return fields;
    }

    public void savePrintTask(int userId, int printerId, String message) {
        String sql = "INSERT INTO ЗаданияПечати (Пользователь_id, Принтер_id, ДатаВремяПечати, Сообщение) " +
                "VALUES (?, ?, datetime('now', 'localtime'), ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, printerId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getInstance().logError("Ошибка сохранения задания печати: " + ex.getMessage());
        }
    }


}