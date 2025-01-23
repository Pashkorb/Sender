package org.example.Service;

public class CurrentUser {
    private static int id;
    private static String login;
    private static UserRole role;

    // Геттеры и сеттеры
    public static int getId() { return id; }
    public static void setId(int id) { CurrentUser.id = id; }

    public static String getLogin() { return login; }
    public static void setLogin(String login) { CurrentUser.login = login; }

    public static UserRole getRole() { return role; }
    public static void setRole(UserRole role) { CurrentUser.role = role; }

    // Метод для очистки данных при выходе
    public static void clear() {
        id = -1;
        login = null;
        role = null;
    }
}