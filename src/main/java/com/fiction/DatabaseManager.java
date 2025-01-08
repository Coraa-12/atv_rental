package com.fiction;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/atv_rental";
    private static final String USER = "Fiction";
    private static final String PASSWORD = "n7@-NtX2zJ4N@vZVkMPDAFZp@";

    public static List<RentalRecord> getAllRentals() throws SQLException {
        List<RentalRecord> rentalRecords = new ArrayList<>();
        String query = "SELECT * FROM rentals";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String rentalId = rs.getString("rental_id");
                String customerName = rs.getString("customer_name");
                String atvId = rs.getString("atv_id");
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");
                String status = rs.getString("status");
                double totalCost = rs.getDouble("total_cost");

                RentalRecord record = new RentalRecord(rentalId, customerName, atvId, startTime, endTime, status, totalCost);
                rentalRecords.add(record);
            }
        }

        return rentalRecords;
    }

    public static List<ATV> getAllATVModels() throws SQLException {
        List<ATV> atvModels = new ArrayList<>();
        String query = "SELECT atv_id, model_name, availability FROM ATVs";

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String atvId = resultSet.getString("atv_id");
                String modelName = resultSet.getString("model_name");
                boolean availability = resultSet.getBoolean("availability");
                atvModels.add(new ATV(atvId, modelName, availability));
            }
        }

        return atvModels;
    }

    public static double getRentalRate(String atvId) throws SQLException {
        String query = "SELECT rental_rate FROM ATVs WHERE atv_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, atvId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("rental_rate");
                } else {
                    throw new SQLException("ATV not found");
                }
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void addRental(RentalRecord rental) throws SQLException {
        String query = "INSERT INTO rentals (customer_name, atv_id, start_time, end_time, status, total_cost) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, rental.getCustomerName());
            stmt.setString(2, rental.getAtvId());
            stmt.setString(3, rental.getStartTime());
            stmt.setString(4, rental.getEndTime());
            stmt.setString(5, rental.getStatus());
            stmt.setBigDecimal(6, new BigDecimal(rental.getTotalCost()));
            stmt.executeUpdate();
        }
    }

    public static void updateATVAvailability(String atvId, boolean availability) throws SQLException {
        System.out.println("Executing query: UPDATE ATVs SET availability = " + availability + " WHERE atv_id = '" + atvId + "'");
        String query = "UPDATE ATVs SET availability = ? WHERE atv_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setBoolean(1, availability);
            pstmt.setString(2, atvId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            if (rowsAffected == 0) {
                throw new SQLException("No rows updated. ATV ID '" + atvId + "' might not exist.");
            }
        }
    }



    public static List<String> getAvailableATVModels() throws SQLException {
        String query = "SELECT atv_id, model_name FROM ATVs WHERE availability = 1";
        List<String> atvModels = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String atvId = rs.getString("atv_id");
                String modelName = rs.getString("model_name");
                atvModels.add(atvId + " - " + modelName); // Concatenation here
            }
        }
        return atvModels;
    }


    public static void updateRentalStatus(String rentalId, String status) throws SQLException {
        String query = "UPDATE rentals SET status = ? WHERE rental_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, rentalId);
            stmt.executeUpdate();
        }
    }
}