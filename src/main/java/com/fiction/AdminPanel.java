package com.fiction;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
        primaryStage.setTitle("ATV Rental Admin Panel");  // Changed title for ATV rentals

        // Layouts
        BorderPane mainLayout = new BorderPane();
        
        // Rental Form
        VBox rentalForm = new VBox(10);
        rentalForm.setPadding(new Insets(10));
        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Customer Name");
        TextField atvIdField = new TextField();  // Changed from scooterId to atvId
        atvIdField.setPromptText("ATV ID");
        TextField rentalDurationField = new TextField();
        rentalDurationField.setPromptText("Rental Duration (hours)");
        TextField paymentAmountField = new TextField();
        paymentAmountField.setPromptText("Payment Amount");
        Button submitButton = new Button("Submit");
        rentalForm.getChildren().addAll(
            new Label("New Rental"),
            customerNameField,
            atvIdField,  // Updated to ATV ID
            rentalDurationField,
            paymentAmountField,
            submitButton
        );

        // Rental Records Table
        TableView<RentalRecord> rentalTable = new TableView<>();
        TableColumn<RentalRecord, String> rentalIdCol = new TableColumn<>("Rental ID");
        TableColumn<RentalRecord, String> customerNameCol = new TableColumn<>("Customer Name");
        TableColumn<RentalRecord, String> atvIdCol = new TableColumn<>("ATV ID");  // Changed column name to ATV ID
        TableColumn<RentalRecord, String> startTimeCol = new TableColumn<>("Start Time");
        TableColumn<RentalRecord, String> endTimeCol = new TableColumn<>("End Time");
        TableColumn<RentalRecord, String> statusCol = new TableColumn<>("Status");
        TableColumn<RentalRecord, String> totalCostCol = new TableColumn<>("Total Cost");
        List<TableColumn<RentalRecord, ?>> columns = Arrays.asList(
        rentalIdCol, customerNameCol, atvIdCol, startTimeCol, endTimeCol, statusCol, totalCostCol
        );
        rentalTable.getColumns().addAll(columns);


        // Bind columns
        rentalIdCol.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        atvIdCol.setCellValueFactory(new PropertyValueFactory<>("atvId"));  // Updated to ATV ID
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        // Add event handling for the submit button
        submitButton.setOnAction(e -> {
            String rentalId = "R" + System.currentTimeMillis(); // Generate a unique rental ID
            String customerName = customerNameField.getText();
            String atvId = atvIdField.getText();  // Updated to ATV ID
            String rentalDuration = rentalDurationField.getText();
            Double paymentAmount = Double.parseDouble(paymentAmountField.getText());
            String startTime = java.time.LocalDateTime.now().toString(); // Current time as start time
            String endTime = java.time.LocalDateTime.now().plusHours(Long.parseLong(rentalDuration)).toString(); // Calculate end time
            String status = "Active"; // Default status

            RentalRecord newRecord = new RentalRecord(rentalId, customerName, atvId, startTime, endTime, status, paymentAmount);  // Updated to ATV ID
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
            atvIdField.clear();  // Updated to ATV ID
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
