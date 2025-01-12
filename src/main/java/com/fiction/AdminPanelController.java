package com.fiction;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;
import java.util.List;
import javafx.util.Pair;

public class AdminPanelController {

    @FXML
    private ComboBox<String> customerIdComboBox;
    @FXML
    private ComboBox<String> atvModelComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField rentalDurationField;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static AdminPanelController instance;

    public AdminPanelController() {
        instance = this;
    }

    public static AdminPanelController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        loadCustomerIds();
        loadATVModels();
    }

    @FXML
    private void handleSubmit() {
        // Check if admin is logged in
        if (!AdminSession.getInstance().isLoggedIn()) {
            // Show login dialog
            AdminLoginDialog loginDialog = new AdminLoginDialog();
            loginDialog.showAndWait().ifPresent(credentials -> {
                try {
                    if (DatabaseManager.verifyAdminCredentials(credentials.getKey(), credentials.getValue())) {
                        // Login successful
                        AdminSession.getInstance().login(credentials.getKey());
                        DatabaseManager.updateLastLogin(credentials.getKey());
                        // Proceed with insertion
                        processRentalInsertion();
                    } else {
                        showError("Invalid credentials!");
                    }
                } catch (SQLException e) {
                    showError("Login error: " + e.getMessage());
                }
            });
        } else {
            // Already logged in, proceed with insertion
            processRentalInsertion();
        }
    }

    private void processRentalInsertion() {
        String rentalId = "R" + System.currentTimeMillis();
        String customerId = customerIdComboBox.getValue();
        String atvModel = atvModelComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String rentalDurationInput = rentalDurationField.getText();
        String status = "Ongoing";

        // Validate input
        if (customerId == null || atvModel == null || startDate == null || endDate == null || rentalDurationInput.isEmpty()) {
            showError("Please fill all fields.");
            return;
        }

        // Parse rental duration
        int rentalDuration;
        try {
            rentalDuration = Integer.parseInt(rentalDurationInput);
        } catch (NumberFormatException e) {
            showError("Invalid rental duration value.");
            return;
        }

        // Fetch rental rate
        double rentalRate;
        try {
            rentalRate = DatabaseManager.getRentalRate(atvModel.split(" - ")[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error fetching rental rate.");
            return;
        }

        // Calculate total cost
        double totalCost = rentalDuration * rentalRate;

        // Use current local machine time for start time
        LocalDateTime startDateTime = LocalDateTime.now();
        String startTimeFormatted = startDateTime.format(DATE_TIME_FORMATTER);
        String endTime = startDateTime.plusHours(rentalDuration).format(DATE_TIME_FORMATTER);

        // Create a new RentalRecord
        RentalRecord newRecord = new RentalRecord(
                rentalId,
                Integer.parseInt(customerId),
                null,
                atvModel.split(" - ")[0],
                startTimeFormatted,
                endTime,
                status,
                totalCost,
                rentalDuration
        );

        // Save to database
        try {
            DatabaseManager.addRental(newRecord);
            // Mark the ATV as unavailable
            String atvId = atvModel.split(" - ")[0];
            DatabaseManager.updateATVAvailability(atvId, false);
            // Refresh the ComboBox
            loadATVModels();
            showInfo("Rental successfully added!");
            clearForm();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error saving to database.");
        }
    }

    private void loadCustomerIds() {
        try {
            List<Customer> customers = DatabaseManager.getAllCustomers();
            customerIdComboBox.getItems().clear();
            for (Customer customer : customers) {
                customerIdComboBox.getItems().add(String.valueOf(customer.getCustomerId()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading customer IDs.");
        }
    }

    private void loadATVModels() {
        try {
            List<ATV> atvModels = DatabaseManager.getAllATVModels();
            atvModelComboBox.getItems().clear();
            for (ATV atv : atvModels) {
                if (atv.isAvailable()) {
                    atvModelComboBox.getItems().add(atv.getAtvId() + " - " + atv.getModelName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading ATV models.");
        }
    }

    private void clearForm() {
        customerIdComboBox.setValue(null);
        atvModelComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        rentalDurationField.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showLoginDialog() {
        AdminLoginDialog loginDialog = new AdminLoginDialog();
        loginDialog.showAndWait().ifPresent(credentials -> {
            String username = credentials.getKey();
            String password = credentials.getValue();
            try {
                if (DatabaseManager.verifyAdminCredentials(username, password)) {
                    AdminSession.getInstance().login(username);
                    showAdminPanel();
                } else {
                    showError("Invalid credentials. Please try again.");
                    showLoginDialog();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error verifying credentials.");
            }
        });
    }

    private void showAdminPanel() {
    }

    @FXML
    private void handleLogout() {
        AdminSession.getInstance().logout();
        showInfo("Successfully logged out.");
    }

    public void refreshATVModels() {
        loadATVModels();
    }

    @FXML
    private void switchToRentalRecords() {
        try {
            Parent rentalRecordsRoot = FXMLLoader.load(getClass().getResource("RentalRecords.fxml"));
            Stage stage = (Stage) customerIdComboBox.getScene().getWindow();
            stage.setScene(new Scene(rentalRecordsRoot));
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Rental Records view.");
        }
    }

    @FXML
    private void switchToCustomerData() {
        try {
            Parent customerDataRoot = FXMLLoader.load(getClass().getResource("CustomerData.fxml"));
            Stage stage = (Stage) AdminPanelApp.getPrimaryStage().getScene().getWindow();
            stage.setScene(new Scene(customerDataRoot));
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Customer Data view.");
        }
    }
}