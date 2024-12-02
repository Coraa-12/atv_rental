package com.fiction;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;

public class AdminPanelController {

    @FXML
    private TextField customerNameField;
    @FXML
    private TextField scooterIdField;
    @FXML
    private TextField rentalDurationField;
    @FXML
    private TextField paymentAmountField;
    @FXML
    private TableView<RentalRecord> rentalTable;
    @FXML
    private TableColumn<RentalRecord, String> rentalIdCol;
    @FXML
    private TableColumn<RentalRecord, String> customerNameCol;
    @FXML
    private TableColumn<RentalRecord, String> scooterIdCol;
    @FXML
    private TableColumn<RentalRecord, String> startTimeCol;
    @FXML
    private TableColumn<RentalRecord, String> endTimeCol;
    @FXML
    private TableColumn<RentalRecord, String> statusCol;
    @FXML
    private TableColumn<RentalRecord, String> totalCostCol;

    @FXML
    public void initialize() {
        rentalIdCol.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        scooterIdCol.setCellValueFactory(new PropertyValueFactory<>("scooterId"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
    }

    @FXML
    private void handleSubmit() {
        String rentalId = "R" + System.currentTimeMillis();  // Unique ID based on current time
        String customerName = customerNameField.getText();
        String scooterId = scooterIdField.getText();
        String rentalDuration = rentalDurationField.getText();
        String paymentAmount = paymentAmountField.getText();

        // Check if the rental duration and payment amount are valid
        if (rentalDuration.isEmpty() || paymentAmount.isEmpty()) {
            // You may want to show an error message if these fields are empty
            return;
        }

        // Convert rentalDuration to hours (assuming it's in hours)
        long durationInHours = Long.parseLong(rentalDuration);

        // Calculate start and end times
        String startTime = java.time.LocalDateTime.now().toString();
        String endTime = java.time.LocalDateTime.now().plusHours(durationInHours).toString();

        // Set status
        String status = "Active";

        // Parse payment amount as a double or BigDecimal
        double totalCost = Double.parseDouble(paymentAmount);

        // Create a new RentalRecord
        RentalRecord newRecord = new RentalRecord(rentalId, customerName, scooterId, startTime, endTime, status, totalCost);

        // Add new record to the TableView
        rentalTable.getItems().add(newRecord);

        // Save to database
        try {
            DatabaseManager.addRental(newRecord);  // Save to database
        } catch (SQLException e) {
            e.printStackTrace();  // Handle database error (you might want to show an error message)
        }

        // Clear input fields
        customerNameField.clear();
        scooterIdField.clear();
        rentalDurationField.clear();
        paymentAmountField.clear();
    }
}
