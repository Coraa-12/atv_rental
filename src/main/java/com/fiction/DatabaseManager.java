package com.fiction;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/atv_rental";
    private static final String USER = "Fiction";
    private static final String PASSWORD = "n7@-NtX2zJ4N@vZVkMPDAFZp@";

    public static List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT customer_id, name, email, phone_number FROM customers";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("customer_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone_number");
                customers.add(new Customer(id, name, email, phone));
            }
        }
        return customers;
    }

    public static void deleteCustomer(int customerId) throws SQLException {
        String query = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();
        }
    }

    public static void saveCustomer(Customer customer) throws SQLException {
        String query = "INSERT INTO customers (name, email, phone_number) VALUES (?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, customer.getCustomerName());
            pstmt.setString(2, customer.getCustomerEmail());
            pstmt.setString(3, customer.getCustomerPhone());
            pstmt.executeUpdate();
        }
    }

    public static List<RentalRecord> getAllRentals() throws SQLException {
        List<RentalRecord> rentalRecords = new ArrayList<>();
        String query = "SELECT r.rental_id, r.customer_id, c.name AS customer_name, " +
                "r.atv_id, a.model_name, r.start_time, r.end_time, " +
                "r.status, r.total_cost, r.rental_duration " +
                "FROM rentals r " +
                "JOIN customers c ON r.customer_id = c.customer_id " +
                "JOIN ATVs a ON r.atv_id = a.atv_id";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String rentalId = rs.getString("rental_id");
                int customerId = rs.getInt("customer_id");
                String customerName = rs.getString("customer_name");
                String atvId = rs.getString("atv_id");
                String modelName = rs.getString("model_name");
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");
                String status = rs.getString("status");
                double totalCost = rs.getDouble("total_cost");
                int rentalDuration = rs.getInt("rental_duration");

                // Combine atvId and modelName
                String fullAtvInfo = atvId + " - " + modelName;

                RentalRecord record = new RentalRecord(rentalId, customerId, customerName,
                        fullAtvInfo, startTime, endTime,
                        status, totalCost, rentalDuration);
                rentalRecords.add(record);
            }
        }
        return rentalRecords;
    }

    public static void addRental(RentalRecord rental) throws SQLException {
        String query = "INSERT INTO rentals (customer_id, atv_id, start_time, end_time, status, total_cost, rental_duration) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, rental.getCustomerId());
            stmt.setString(2, rental.getAtvId());
            stmt.setString(3, rental.getStartTime());
            stmt.setString(4, rental.getEndTime());
            stmt.setString(5, rental.getStatus());

            // Add null check for totalCost
            Double totalCost = rental.getTotalCost();
            if (totalCost != null) {
                stmt.setBigDecimal(6, new BigDecimal(totalCost));
            } else {
                stmt.setNull(6, Types.DECIMAL);
            }

            stmt.setInt(7, rental.getRentalDuration());
            stmt.executeUpdate();
        }
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
                atvModels.add(atvId + " - " + modelName);
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

    public static boolean verifyAdminCredentials(String username, String password) throws SQLException {
        String query = "SELECT password FROM admin_users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return storedPassword.equals(password);
                }
                return false;
            }
        }
    }

    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateLastLogin(String username) throws SQLException {
        String query = "UPDATE admin_users SET last_login = NOW() WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }
}