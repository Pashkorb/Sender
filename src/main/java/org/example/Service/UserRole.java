package org.example.Service;

import java.util.Arrays;

public enum UserRole {
    ADMIN("Admin"),
    WORKER("Worker");

    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    // Метод для получения роли по строке (например, из базы данных)
    public static UserRole fromString(String roleName) {
        for (UserRole role : values()) {
            if (role.roleName.equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + roleName);
    }
    public static String[] getAllRoles() {
        return Arrays.stream(values())
                .map(UserRole::getRoleName)
                .toArray(String[]::new);
    }
}