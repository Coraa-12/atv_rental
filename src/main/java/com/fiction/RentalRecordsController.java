package com.fiction;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
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
    private TableColumn<RentalRecord, Void> actionColumn;
    @FXML
    private TableColumn<RentalRecord, Integer> rentalDurationColumn;

    @FXML
    public void initialize() {
        rentalIdColumn.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        atvIdColumn.setCellValueFactory(new PropertyValueFactory<>("atvId"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        rentalDurationColumn.setCellValueFactory(new PropertyValueFactory<>("rentalDuration")); // Add this line

        // Set preferred width for columns
        rentalIdColumn.setPrefWidth(100);
        customerNameColumn.setPrefWidth(150);
        atvIdColumn.setPrefWidth(100);
        startTimeColumn.setPrefWidth(150);
        endTimeColumn.setPrefWidth(150);
        statusColumn.setPrefWidth(100);
        totalCostColumn.setPrefWidth(100);
        rentalDurationColumn.setPrefWidth(100); // Add this line

        // Set column resize policy
        rentalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add action buttons
        addButtonToTable();

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

    private void addButtonToTable() {
        Callback<TableColumn<RentalRecord, Void>, TableCell<RentalRecord, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<RentalRecord, Void> call(final TableColumn<RentalRecord, Void> param) {
                final TableCell<RentalRecord, Void> cell = new TableCell<>() {

                    private final Button btnComplete = new Button("Complete");
                    private final Button btnCancel = new Button("Cancel");

                    {
                        btnComplete.setOnAction((event) -> {
                            RentalRecord data = getTableView().getItems().get(getIndex());
                            System.out.println("Complete button clicked for ATV ID: " + data.getAtvId());
                            updateStatus(data, "Complete");
                        });
                        btnCancel.setOnAction((event) -> {
                            RentalRecord data = getTableView().getItems().get(getIndex());
                            System.out.println("Cancel button clicked for ATV ID: " + data.getAtvId());
                            updateStatus(data, "Cancelled");
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            RentalRecord data = getTableView().getItems().get(getIndex());
                            HBox hBox = new HBox(btnComplete, btnCancel);
                            hBox.setSpacing(10);
                            setGraphic(hBox);
                            if ("Complete".equals(data.getStatus())) {
                                btnComplete.setDisable(true);
                                btnCancel.setDisable(true);
                            } else {
                                btnComplete.setDisable(false);
                                btnCancel.setDisable(false);
                            }
                        }
                    }
                };
                return cell;
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }


    private void updateStatus(RentalRecord record, String status) {
        try {
            System.out.println("Updating status for Rental ID: " + record.getRentalId());
            System.out.println("ATV ID: " + record.getAtvId() + ", New Status: " + status);

            DatabaseManager.updateRentalStatus(record.getRentalId(), status);
            record.setStatus(status);
            if ("Complete".equals(status)) {
                // Extract ATV ID
                String atvId = extractAtvId(record.getAtvId());
                System.out.println("Marking ATV as available: " + atvId);
                DatabaseManager.updateATVAvailability(atvId, true);
            }
            rentalTable.refresh();
            AdminPanelController.getInstance().refreshATVModels();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error updating rental status.");
        }
    }

    // Utility method to extract ATV ID
    private String extractAtvId(String fullAtvId) {
        if (fullAtvId == null || !fullAtvId.contains(" - ")) {
            return fullAtvId; // Return as-is if no delimiter
        }
        return fullAtvId.split(" - ")[0];
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