package com.fiction;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;

public class AdminPanelController {

    @FXML
    private TextField customerNameField;
    @FXML
    private TextField atvIdField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TextField rentalDurationField;
    @FXML
    private TextField endTimeField;
    @FXML
    private TextField totalCostField;

    @FXML
    private TableView<RentalRecord> rentalTable;
    @FXML
    private TableColumn<RentalRecord, String> rentalIdCol;
    @FXML
    private TableColumn<RentalRecord, String> customerNameCol;
    @FXML
    private TableColumn<RentalRecord, String> atvIdCol;
    @FXML
    private TableColumn<RentalRecord, String> startTimeCol;
    @FXML
    private TableColumn<RentalRecord, String> endTimeCol;
    @FXML
    private TableColumn<RentalRecord, String> statusCol;
    @FXML
    private TableColumn<RentalRecord, String> totalCostCol;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        rentalIdCol.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        atvIdCol.setCellValueFactory(new PropertyValueFactory<>("atvId"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
    }

    @FXML
    private void handleCalculateEndTime() {
        LocalDate startDate = startDatePicker.getValue();
        String rentalDurationInput = rentalDurationField.getText();

        if (startDate == null || rentalDurationInput.isEmpty()) {
            showError("Please select a start date and enter the rental duration.");
            return;
        }

        try {
            int rentalDurationHours = Integer.parseInt(rentalDurationInput);
            LocalDateTime startDateTime = startDate.atStartOfDay(); // Assuming start at 00:00
            LocalDateTime endDateTime = startDateTime.plusHours(rentalDurationHours);

            endTimeField.setText(endDateTime.format(DATE_TIME_FORMATTER));
        } catch (NumberFormatException e) {
            showError("Invalid rental duration. Please enter a valid number.");
        }
    }

    @FXML
    private void handleSubmit() {
        String rentalId = "R" + System.currentTimeMillis(); // Generate unique rental ID
        String customerName = customerNameField.getText();
        String atvId = atvIdField.getText();
        LocalDate startDate = startDatePicker.getValue();
        String rentalDurationInput = rentalDurationField.getText();
        String endTime = endTimeField.getText();
        String totalCostInput = totalCostField.getText();

        // Validate input
        if (customerName.isEmpty() || atvId.isEmpty() || startDate == null || rentalDurationInput.isEmpty() || endTime.isEmpty() || totalCostInput.isEmpty()) {
            showError("Please fill all fields.");
            return;
        }

        // Parse total cost
        double totalCost;
        try {
            totalCost = Double.parseDouble(totalCostInput);
        } catch (NumberFormatException e) {
            showError("Invalid total cost value.");
            return;
        }

        // Create a new RentalRecord
        String startTime = startDate.atStartOfDay().format(DATE_TIME_FORMATTER); // Default to start of the selected day
        RentalRecord newRecord = new RentalRecord(rentalId, customerName, atvId, startTime, endTime, "Active", totalCost);

        // Add the new record to the TableView
        rentalTable.getItems().add(newRecord);

        // Save to database
        try {
            DatabaseManager.addRental(newRecord);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error saving to database.");
        }

        // Clear input fields
        clearForm();
    }

    private void clearForm() {
        customerNameField.clear();
        atvIdField.clear();
        startDatePicker.setValue(null);
        rentalDurationField.clear();
        endTimeField.clear();
        totalCostField.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
