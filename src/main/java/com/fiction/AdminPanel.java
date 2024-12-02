package com.fiction;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminPanel extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Scooter Rental Admin Panel");

        // Layouts
        BorderPane mainLayout = new BorderPane();
        
        // Rental Form
        VBox rentalForm = new VBox(10);
        rentalForm.setPadding(new Insets(10));
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Customer Name");
        TextField scooterIdField = new TextField();
        scooterIdField.setPromptText("Scooter ID");
        TextField rentalDurationField = new TextField();
        rentalDurationField.setPromptText("Rental Duration (hours)");
        TextField paymentAmountField = new TextField();
        paymentAmountField.setPromptText("Payment Amount");
        Button submitButton = new Button("Submit");
        rentalForm.getChildren().addAll(
            new Label("New Rental"),
            customerNameField,
            scooterIdField,
            rentalDurationField,
            paymentAmountField,
            submitButton
        );

        // Rental Records Table
        TableView<RentalRecord> rentalTable = new TableView<>();
        TableColumn<RentalRecord, String> rentalIdCol = new TableColumn<>("Rental ID");
        TableColumn<RentalRecord, String> customerNameCol = new TableColumn<>("Customer Name");
        TableColumn<RentalRecord, String> scooterIdCol = new TableColumn<>("Scooter ID");
        TableColumn<RentalRecord, String> startTimeCol = new TableColumn<>("Start Time");
        TableColumn<RentalRecord, String> endTimeCol = new TableColumn<>("End Time");
        TableColumn<RentalRecord, String> statusCol = new TableColumn<>("Status");
        TableColumn<RentalRecord, String> totalCostCol = new TableColumn<>("Total Cost");
        rentalTable.getColumns().addAll(
            rentalIdCol, customerNameCol, scooterIdCol, startTimeCol, endTimeCol, statusCol, totalCostCol
        );

        // Bind columns
        rentalIdCol.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        scooterIdCol.setCellValueFactory(new PropertyValueFactory<>("scooterId"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        // Add event handling for the submit button
        submitButton.setOnAction(e -> {
            String rentalId = "R" + System.currentTimeMillis(); // Generate a unique rental ID
            String customerName = customerNameField.getText();
            String scooterId = scooterIdField.getText();
            String rentalDuration = rentalDurationField.getText();
            Double paymentAmount = Double.parseDouble(paymentAmountField.getText());
            String startTime = java.time.LocalDateTime.now().toString(); // Current time as start time
            String endTime = java.time.LocalDateTime.now().plusHours(Long.parseLong(rentalDuration)).toString(); // Calculate end time
            String status = "Active"; // Default status

            RentalRecord newRecord = new RentalRecord(rentalId, customerName, scooterId, startTime, endTime, status, paymentAmount);
            rentalTable.getItems().add(newRecord);

            // Save the record to the database
            try {
                DatabaseManager.addRental(newRecord);  // Save to database
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Database Error", "There was an error saving the rental.");
            }

            // Clear the form
            customerNameField.clear();
            scooterIdField.clear();
            rentalDurationField.clear();
            paymentAmountField.clear();
        });

        // Main Layout
        mainLayout.setLeft(rentalForm);
        mainLayout.setCenter(rentalTable);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
