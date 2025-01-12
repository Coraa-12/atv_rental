package com.fiction;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
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

        // Set preferred width for columns
        customerIdColumn.setPrefWidth(100);
        customerNameColumn.setPrefWidth(150);
        customerEmailColumn.setPrefWidth(200);
        customerPhoneColumn.setPrefWidth(150);

        // Set column resize policy
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add action buttons
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
        Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Customer, Void> call(final TableColumn<Customer, Void> param) {
                final TableCell<Customer, Void> cell = new TableCell<>() {

                    private final Button btnEdit = new Button("Edit");
                    private final Button btnDelete = new Button("Delete");

                    {
                        btnEdit.setOnAction((event) -> {
                            Customer data = getTableView().getItems().get(getIndex());
                            System.out.println("Edit button clicked for Customer ID: " + data.getCustomerId());
                            // Implement edit functionality
                        });
                        btnDelete.setOnAction((event) -> {
                            Customer data = getTableView().getItems().get(getIndex());
                            System.out.println("Delete button clicked for Customer ID: " + data.getCustomerId());
                            deleteCustomer(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hBox = new HBox(btnEdit, btnDelete);
                            hBox.setSpacing(10);
                            setGraphic(hBox);
                        }
                    }
                };
                return cell;
            }
        };

        actionColumn.setCellFactory(cellFactory);
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
            loadCustomerData(); // Refresh the table after adding a new customer
            clearForm(); // Clear the form fields
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
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        customerNameField.clear();
        customerEmailField.clear();
        customerPhoneField.clear();
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