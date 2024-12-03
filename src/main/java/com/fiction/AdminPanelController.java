package com.fiction;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;

public class AdminPanelController {

    @FXML
    private TextField customerNameField;
    @FXML
    private TextField atvIdField;
    @FXML
    private TextField startTimeField;
    @FXML
    private TextField endTimeField;
    @FXML
    private TextField statusField;
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
    private void handleSubmit() {
        String rentalId = "R" + System.currentTimeMillis(); // Generate unique rental ID
        String customerName = customerNameField.getText();
        String atvId = atvIdField.getText();
        String startTime = startTimeField.getText();
        String endTime = endTimeField.getText();
        String status = statusField.getText();
        String totalCostInput = totalCostField.getText();

        // Validate input
        if (customerName.isEmpty() || atvId.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || status.isEmpty() || totalCostInput.isEmpty()) {
            // Optionally, add error messages
            System.err.println("Please fill all fields.");
            return;
        }

        // Parse total cost
        double totalCost;
        try {
            totalCost = Double.parseDouble(totalCostInput);
        } catch (NumberFormatException e) {
            System.err.println("Invalid total cost value.");
            return;
        }

        // Create a new RentalRecord
        RentalRecord newRecord = new RentalRecord(rentalId, customerName, atvId, startTime, endTime, status, totalCost);

        // Add the new record to the TableView
        rentalTable.getItems().add(newRecord);

        // Save to database
        try {
            DatabaseManager.addRental(newRecord);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Clear input fields
        customerNameField.clear();
        atvIdField.clear();
        startTimeField.clear();
        endTimeField.clear();
        statusField.clear();
        totalCostField.clear();
    }
}
