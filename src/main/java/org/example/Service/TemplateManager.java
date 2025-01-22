package org.example.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TemplateManager {
    private static final String TEMPLATE_TABLE = "Шаблоны";
    private static final DatabaseManager dbManager = DatabaseManager.getInstance();


    // Сохранение шаблона в базу данных
    public static void saveTemplateWithNames(String templateName, Map<String, Map<String, String>> fieldsData) throws Exception {
        try (Connection connection = dbManager.getConnection()) {
            // Сохраняем шаблон
            String insertTemplate = "INSERT INTO Шаблоны (Наименование, Поля) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertTemplate, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, templateName);
                stmt.setString(2, ""); // Пустая строка для столбца Поля
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int templateId = rs.getInt(1);

                        // Сохраняем поля
                        String insertField = "INSERT INTO Поля (Шаблон_id, Номер, Наименование_поля, Текст) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement fieldStmt = connection.prepareStatement(insertField)) {
                            for (Map.Entry<String, Map<String, String>> entry : fieldsData.entrySet()) {
                                String number = entry.getKey();
                                Map<String, String> data = entry.getValue();

                                fieldStmt.setInt(1, templateId);
                                fieldStmt.setInt(2, Integer.parseInt(number));
                                fieldStmt.setString(3, data.get("name"));
                                fieldStmt.setString(4, data.get("text"));
                                fieldStmt.addBatch();
                            }
                            fieldStmt.executeBatch();
                        }
                    }
                }
            }
        }
    }
    private static void saveFields(Connection connection, int templateId, Map<String, String> fields) throws SQLException {
        String query = "INSERT INTO Поля (Шаблон_id, Номер, Наименование_поля, Текст) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                String fieldNumber = entry.getKey(); // Например, "0"
                String fieldText = entry.getValue();

                // Здесь можно добавить логику для извлечения имени поля, если оно есть
                String fieldName = "Поле " + fieldNumber; // Пример имени поля

                statement.setInt(1, templateId);
                statement.setInt(2, Integer.parseInt(fieldNumber));
                statement.setString(3, fieldName);
                statement.setString(4, fieldText);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    // Загрузка шаблона из базы данных
    public static Map<String, String> loadTemplate(String templateName) throws Exception {
        try (Connection connection = dbManager.getConnection()) {
            String query = "SELECT Поля FROM " + TEMPLATE_TABLE + " WHERE Наименование = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, templateName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String fieldsJson = resultSet.getString("Поля");
                        return deserializeFields(fieldsJson); // Десериализуем поля из JSON
                    } else {
                        System.out.println("[WARN] Шаблон '" + templateName + "' не найден.");
                        return null;
                    }
                }
            }
        }
    }

    // Сериализация полей в JSON
    private static String serializeFields(Map<String, String> fields) {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }
        if (json.length() > 1) {
            json.deleteCharAt(json.length() - 1); // Удаляем последнюю запятую
        }
        json.append("}");
        return json.toString();
    }

    // Десериализация полей из JSON
    private static Map<String, String> deserializeFields(String json) {
        Map<String, String> fields = new HashMap<>();
        json = json.replace("{", "").replace("}", "");
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].replace("\"", "").trim();
                String value = keyValue[1].replace("\"", "").trim();
                fields.put(key, value);
            }
        }
        return fields;
    }
    public static Map<String, Map<String, String>> loadAllTemplates() throws Exception {
        Map<String, Map<String, String>> templates = new HashMap<>();

        try (Connection connection = dbManager.getConnection()) {
            String query = "SELECT Наименование, Поля FROM Шаблоны";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String templateName = resultSet.getString("Наименование");
                        String fieldsJson = resultSet.getString("Поля");

                        // Десериализуем поля из JSON
                        Map<String, String> fields = deserializeFields(fieldsJson);

                        // Добавляем шаблон в результат
                        templates.put(templateName, fields);
                    }
                }
            }
        }

        return templates;
    }
    // Загрузка шаблона с именами полей
    public static Map<String, Map<String, String>> loadTemplateWithFieldNames(String templateName) throws SQLException {
        Map<String, Map<String, String>> fields = new HashMap<>();

        try (Connection connection = dbManager.getConnection()) {
            String query = "SELECT Номер, Наименование_поля, Текст FROM Поля " +
                    "WHERE Шаблон_id = (SELECT id FROM Шаблоны WHERE Наименование = ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, templateName);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String number = String.valueOf(rs.getInt("Номер"));
                    String name = rs.getString("Наименование_поля");
                    String text = rs.getString("Текст");

                    fields.put(number, Map.of(
                            "name", name,
                            "text", text
                    ));
                }
            }
        }
        return fields;
    }
}