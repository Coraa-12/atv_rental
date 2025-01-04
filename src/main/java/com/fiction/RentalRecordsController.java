package com.fiction;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RentalRecordsController {

    @FXML
    private TableView<RentalRecord> rentalTable;
    @FXML
    private TableColumn<RentalRecord, String> rentalIdColumn;
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
    public void initialize() {
        rentalIdColumn.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        atvIdColumn.setCellValueFactory(new PropertyValueFactory<>("atvId"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        // Set preferred width for columns
        rentalIdColumn.setPrefWidth(100);
        customerNameColumn.setPrefWidth(150);
        atvIdColumn.setPrefWidth(100);
        startTimeColumn.setPrefWidth(150);
        endTimeColumn.setPrefWidth(150);
        statusColumn.setPrefWidth(100);
        totalCostColumn.setPrefWidth(100);

        // Set column resize policy
        rentalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadRentalRecords();
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void switchToAdminPanel() {
        try {
            Parent adminPanelRoot = FXMLLoader.load(getClass().getResource("AdminPanel.fxml"));
            Stage stage = (Stage) rentalTable.getScene().getWindow();
            stage.setScene(new Scene(adminPanelRoot));
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Admin Panel view.");
        }
    }
}