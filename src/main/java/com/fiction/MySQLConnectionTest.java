package com.fiction;

import java.sql.*;

public class MySQLConnectionTest {
    public static void main(String[] args) {
        // MySQL connection URL, user, and password
        String url = "jdbc:mysql://localhost:3306/atv_rental";
        String user = "root";
        String password = "4yvzeL_VFocYMtnZX!QqYipzu";

        // Establish the connection
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Successfully connected to MySQL!");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}
