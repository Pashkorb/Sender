package org.example.Model;

public class UserData {
    String fio;
    String role;
    String login;
    String password;
    boolean access;
    Integer id;

    UserData(Object[] row) {
        this.fio = (String) row[0];
        this.role = (String) row[1];
        this.login = (String) row[2];
        this.password = (String) row[3];
        this.access = (Boolean) row[4];
        this.id = (Integer) row[5];
    }

    boolean isNewUser() {
        return id == null;
    }
}