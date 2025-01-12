package com.fiction;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.geometry.Pos;

public class CustomerDataController {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> customerIdColumn;
    @FXML
    private TableColumn<Customer, String> customerNameColumn;
    @FXML
    private TableColumn<Customer, String> customerEmailColumn;
    @FXML
    private TableColumn<Customer, String> customerPhoneColumn;
    @FXML
    private TableColumn<Customer, Void> actionColumn;

    @FXML
    private TextField searchField;
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField customerEmailField;
    @FXML
    private TextField customerPhoneField;

    @FXML
    public void initialize() {
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("customerEmail"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));

        customerTable.getStyleClass().add("custom-table");

        customerIdColumn.setStyle("-fx-alignment: CENTER;");
        customerNameColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        customerEmailColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        customerPhoneColumn.setStyle("-fx-alignment: CENTER;");
        actionColumn.setStyle("-fx-alignment: CENTER;");

        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ACTION BUTTONS
        addButtonToTable();

        loadCustomerData();
    }

    private void loadCustomerData() {
        try {
            List<Customer> customers = DatabaseManager.getAllCustomers();
            customerTable.getItems().setAll(customers);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading customer data from database.");
        }
    }

    private void addButtonToTable() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton);

            {
                editButton.setStyle(
                        "-fx-background-color: #4CAF50; " +
                                "-fx-text-fill: white; " +
                                "-fx-padding: 5 15; " +
                                "-fx-cursor: hand; " +
                                "-fx-background-radius: 3;"
                );
                editButton.setOnAction(event -> {
                    Customer data = getTableView().getItems().get(getIndex());
                    System.out.println("Edit button clicked for Customer ID: " + data.getCustomerId());
                });
                deleteButton.setStyle(
                        "-fx-background-color: #f44336; " +
                                "-fx-text-fill: white; " +
                                "-fx-padding: 5 15; " +
                                "-fx-cursor: hand; " +
                                "-fx-background-radius: 3;"
                );
                deleteButton.setOnAction(event -> {
                    Customer data = getTableView().getItems().get(getIndex());
                    System.out.println("Delete button clicked for Customer ID: " + data.getCustomerId());
                    deleteCustomer(data);
                });
                buttonBox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });
    }

    private void deleteCustomer(Customer customer) {
        try {
            DatabaseManager.deleteCustomer(customer.getCustomerId());
            customerTable.getItems().remove(customer);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error deleting customer.");
        }
    }

    @FXML
    private void handleSubmit() {
        String name = customerNameField.getText();
        String email = customerEmailField.getText();
        String phone = customerPhoneField.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        Customer customer = new Customer(0, name, email, phone);
        try {
            DatabaseManager.saveCustomer(customer);
            showSuccess("Customer data saved successfully.");
            loadCustomerData();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error saving customer data.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 15px;"
        );

        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 15px;"
        );

        alert.showAndWait();
    }

    private void clearForm() {
        customerNameField.clear();
        customerEmailField.clear();
        customerPhoneField.clear();
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        try {
            List<Customer> allCustomers = DatabaseManager.getAllCustomers();
            List<Customer> filteredCustomers = allCustomers.stream()
                    .filter(customer -> customer.getCustomerName().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            customerTable.getItems().setAll(filteredCustomers);

            System.out.println("Search performed with text: " + searchText);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error performing search.");
        }
    }

    @FXML
    private void switchToAdminPanel() {
        try {
            Parent adminPanelRoot = FXMLLoader.load(getClass().getResource("AdminPanel.fxml"));
            Stage stage = (Stage) customerTable.getScene().getWindow();
            stage.setScene(new Scene(adminPanelRoot));
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading Admin Panel view.");
        }
    }
}