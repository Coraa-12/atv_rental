package com.fiction;

import java.sql.*;

public class MySQLConnectionTest {
    public static void main(String[] args) {
        // MySQL connection URL, user, and password
        String url = "jdbc:mysql://localhost:3306/scooter_rental";
        String user = "root";
        String password = "ge8Emybj99euqKoGh!xQ";

        // Establish the connection
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Successfully connected to MySQL!");

            // SQL query to insert data
            String sql = "INSERT INTO rentals (customer_name, scooter_id, status, total_cost) VALUES (?, ?, ?, ?)";
            
            // Prepare the statement
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                // Set the values for the parameters
                preparedStatement.setString(1, "John Doe");  // customer_name
                preparedStatement.setInt(2, 101);            // scooter_id
                preparedStatement.setString(3, "active");    // status
                preparedStatement.setBigDecimal(4, new java.math.BigDecimal(20.50));  // total_cost

                // Execute the update (insert)
                int rowsAffected = preparedStatement.executeUpdate();
                System.out.println(rowsAffected + " row(s) inserted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
