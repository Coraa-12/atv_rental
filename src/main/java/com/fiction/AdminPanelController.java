package com.fiction;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

        statusComboBox.getItems().addAll("Complete", "Ongoing", "Cancelled");

        loadATVModels();
        loadRentalRecords();
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
        String totalCostInput = totalCostField.getText();

        // Validate input
        if (customerName.isEmpty() || atvModel == null || startDate == null || endDate == null || rentalDurationInput.isEmpty() || status == null || totalCostInput.isEmpty()) {
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
        String endTime = endDate.atStartOfDay().format(DATE_TIME_FORMATTER); // Default to start of the selected day
        RentalRecord newRecord = new RentalRecord(rentalId, customerName, atvModel, startTime, endTime, status, totalCost);

        // Add the new record to the TableView
        rentalTable.getItems().add(newRecord);

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

    private void loadRentalRecords() {
        try {
            List<RentalRecord> rentalRecords = DatabaseManager.getAllRentals();
            rentalTable.getItems().setAll(rentalRecords);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading rental records from database.");
        }
    }

    private void clearForm() {
        customerNameField.clear();
        atvModelComboBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        rentalDurationField.clear();
        statusComboBox.setValue(null);
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