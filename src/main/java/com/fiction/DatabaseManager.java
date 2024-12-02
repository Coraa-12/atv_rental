package com.fiction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.math.BigDecimal;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/scooter_rental";
    private static final String USER = "root";
    private static final String PASSWORD = "ge8Emybj99euqKoGh!xQ";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void addRental(RentalRecord rental) throws SQLException {
        // Removed rental_id from the query since it's auto-incremented
        String query = "INSERT INTO rentals (customer_name, scooter_id, start_time, end_time, status, total_cost) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, rental.getCustomerName());
            stmt.setString(2, rental.getScooterId());
            stmt.setString(3, rental.getStartTime());
            stmt.setString(4, rental.getEndTime());
            stmt.setString(5, rental.getStatus());
            stmt.setBigDecimal(6, new BigDecimal(rental.getTotalCost()));
            stmt.executeUpdate();
        }
    }

    // Add other CRUD methods as needed (e.g., updateRental, deleteRental, getRentals)
}
