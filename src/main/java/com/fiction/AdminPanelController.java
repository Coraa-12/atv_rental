package com.fiction;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;
import java.util.List;

public class AdminPanelController {

    @FXML
    private TextField customerNameField;
    @FXML
    private ComboBox<String> atvModelComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField rentalDurationField;
    @FXML
    private ComboBox<String> statusComboBox;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll("Complete", "Ongoing", "Cancelled");
        loadATVModels();
    }

    @FXML
    private void handleSubmit() {
        String rentalId = "R" + System.currentTimeMillis();
        String customerName = customerNameField.getText();
        String atvModel = atvModelComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String rentalDurationInput = rentalDurationField.getText();
        String status = statusComboBox.getValue();

        // Validate input
        if (customerName.isEmpty() || atvModel == null || startDate == null || endDate == null || rentalDurationInput.isEmpty() || status == null) {
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

        // Create a new RentalRecord
        String startTime = startDate.atStartOfDay().format(DATE_TIME_FORMATTER); // Default to start of the selected day
        String endTime = endDate.atStartOfDay().format(DATE_TIME_FORMATTER); // Default to start of the selected day
        RentalRecord newRecord = new RentalRecord(rentalId, customerName, atvModel, startTime, endTime, status, totalCost);

        // Save to database
        try {
            DatabaseManager.addRental(newRecord);
            // Mark the ATV as unavailable
            String atvId = atvModel.split(" - ")[0]; // Extract the ATV ID from the ComboBox value
            DatabaseManager.updateATVAvailability(atvId, false);
            // Refresh the ComboBox
            loadATVModels();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error saving to database.");
        }

        // Clear input fields
        clearForm();
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
        customerNameField.clear();
        atvModelComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        rentalDurationField.clear();
        statusComboBox.setValue(null);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void switchToRentalRecords() {
        try {
            Parent rentalRecordsRoot = FXMLLoader.load(getClass().getResource("RentalRecords.fxml"));
            Stage stage = (Stage) customerNameField.getScene().getWindow();
            stage.setScene(new Scene(rentalRecordsRoot));
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Rental Records view.");
        }
    }
}