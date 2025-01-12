package com.fiction;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class RentalRecordsController {

    @FXML
    private TableView<RentalRecord> rentalTable;
    @FXML
    private TableColumn<RentalRecord, String> rentalIdColumn;
    @FXML
    private TableColumn<RentalRecord, Integer> customerIdColumn;
    @FXML
    private TableColumn<RentalRecord, String> customerNameColumn;
    @FXML
    private TableColumn<RentalRecord, String> atvIdColumn;
    @FXML
    private TableColumn<RentalRecord, String> startTimeColumn;
    @FXML
    private TableColumn<RentalRecord, String> endTimeColumn;
    @FXML
    private TableColumn<RentalRecord, String> statusColumn;
    @FXML
    private TableColumn<RentalRecord, Double> totalCostColumn;
    @FXML
    private TableColumn<RentalRecord, Integer> rentalDurationColumn;
    @FXML
    private TableColumn<RentalRecord, Void> actionColumn;
    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        // Set up the column mappings using the exact property names from RentalRecord
        rentalIdColumn.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        atvIdColumn.setCellValueFactory(new PropertyValueFactory<>("atvId"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        rentalDurationColumn.setCellValueFactory(new PropertyValueFactory<>("rentalDuration"));

        setupActionColumn();
        loadRentalRecords();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(column -> new TableCell<RentalRecord, Void>() {
            private final Button button = new Button("Complete");
            {
                button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                button.setOnAction(event -> {
                    RentalRecord rental = getTableView().getItems().get(getIndex());
                    completeRental(rental);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RentalRecord rental = getTableView().getItems().get(getIndex());
                    // Only show button for "Ongoing" rentals
                    setGraphic("Ongoing".equals(rental.getStatus()) ? button : null);
                }
            }
        });
    }

    private void completeRental(RentalRecord rental) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Complete rental for ATV " + rental.getRawAtvId() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Complete Rental");
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    DatabaseManager.updateRentalStatus(rental.getRentalId(), "Completed");
                    DatabaseManager.updateATVAvailability(rental.getRawAtvId(), true);
                    loadRentalRecords(); // Refresh the table
                } catch (SQLException e) {
                    showError("Failed to complete rental: " + e.getMessage());
                }
            }
        });
    }

    private void loadRentalRecords() {
        try {
            List<RentalRecord> rentals = DatabaseManager.getAllRentals();
            rentalTable.getItems().setAll(rentals);
        } catch (SQLException e) {
            showError("Error loading rental records: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        try {
            List<RentalRecord> allRecords = DatabaseManager.getAllRentals();
            List<RentalRecord> filteredRecords = allRecords.stream()
                    .filter(record -> record.getCustomerName().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            rentalTable.getItems().setAll(filteredRecords);
        } catch (SQLException e) {
            showError("Error performing search: " + e.getMessage());
        }
    }

    @FXML
    private void switchToAdminPanel() {
        try {
            Parent adminPanelRoot = FXMLLoader.load(getClass().getResource("AdminPanel.fxml"));
            Stage stage = (Stage) rentalTable.getScene().getWindow();
            stage.setScene(new Scene(adminPanelRoot));
        } catch (IOException e) {
            showError("Error loading Admin Panel: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}