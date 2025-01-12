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
        // CHECK IF LOGIN
        if (!AdminSession.getInstance().isLoggedIn()) {
            // SHOW LOGIN DIALOG
            AdminLoginDialog loginDialog = new AdminLoginDialog();
            loginDialog.showAndWait().ifPresent(credentials -> {
                try {
                    if (DatabaseManager.verifyAdminCredentials(credentials.getKey(), credentials.getValue())) {
                        // LOGIN SUCCESFULL
                        AdminSession.getInstance().login(credentials.getKey());
                        DatabaseManager.updateLastLogin(credentials.getKey());
                        processRentalInsertion();
                    } else {
                        showError("Invalid credentials!");
                    }
                } catch (SQLException e) {
                    showError("Login error: " + e.getMessage());
                }
            });
        } else {
            // IF ALREADY LOGGED IN
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

        // VALIDASI INPUT
        if (customerId == null || atvModel == null || startDate == null || endDate == null || rentalDurationInput.isEmpty()) {
            showError("Please fill all fields.");
            return;
        }

        int rentalDuration;
        try {
            rentalDuration = Integer.parseInt(rentalDurationInput);
        } catch (NumberFormatException e) {
            showError("Invalid rental duration value.");
            return;
        }

        // FETCH RENTAL RATE FROM ATVs TABLE
        double rentalRate;
        try {
            rentalRate = DatabaseManager.getRentalRate(atvModel.split(" - ")[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error fetching rental rate.");
            return;
        }

        // CALCULATE TOTAL COST BASED ON RENTAL DURATION AND RENTAL RATE
        double totalCost = rentalDuration * rentalRate;

        // BASED ON LOCAL MACHINE TIME
        LocalDateTime startDateTime = LocalDateTime.now();
        String startTimeFormatted = startDateTime.format(DATE_TIME_FORMATTER);
        String endTime = startDateTime.plusHours(rentalDuration).format(DATE_TIME_FORMATTER);

        // CREATE NEW RENTAL RECORD
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

        // FINALLY SAVE TO DATABASE
        try {
            DatabaseManager.addRental(newRecord);
            // CHANGE THE ATV STATUS TO UNAVAILABLE
            String atvId = atvModel.split(" - ")[0];
            DatabaseManager.updateATVAvailability(atvId, false);
            // REFRESH COMBOBOX
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