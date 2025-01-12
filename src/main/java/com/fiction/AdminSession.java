package com.fiction;

public class AdminSession {
    private static AdminSession instance;
    private boolean isLoggedIn = false;
    private String username;

    private AdminSession() {}

    public static AdminSession getInstance() {
        if (instance == null) {
            instance = new AdminSession();
        }
        return instance;
    }

    public void login(String username) {
        this.username = username;
        this.isLoggedIn = true;
    }

    public void logout() {
        this.username = null;
        this.isLoggedIn = false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getUsername() {
        return username;
    }
}