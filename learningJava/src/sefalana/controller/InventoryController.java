package sefalana.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {

    // ── Form fields ──────────────────────────────────────────────
    @FXML private TextField txtProductName;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtPrice;

    // ── Buttons ───────────────────────────────────────────────────
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    // ── Table ─────────────────────────────────────────────────────
    @FXML private TableView<Product>           tableView;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Double> colPrice;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private static final String HISTORY_FILE = "history.txt";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate category dropdown
        cmbCategory.setItems(FXCollections.observableArrayList(
                "Electronics", "Groceries", "Clothing", "Furniture", "Stationery"
        ));
        cmbCategory.getSelectionModel().selectFirst();

        // Bind table columns to Product properties
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        tableView.setItems(productList);
        tableView.setPlaceholder(new Label("No products added yet."));

        // Row click → populate fields
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, selected) -> {
                    if (selected != null) {
                        txtProductName.setText(selected.getProductName());
                        cmbCategory.setValue(selected.getCategory());
                        txtPrice.setText(String.valueOf(selected.getPrice()));
                    }
                });
    }

    // ── Add ───────────────────────────────────────────────────────
    @FXML
    private void handleAdd() {
        String name      = txtProductName.getText().trim();
        String category  = cmbCategory.getValue();
        String priceText = txtPrice.getText().trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            showAlert("Validation Error", "Product Name and Price are required.");
            return;
        }
        try {
            double price    = Double.parseDouble(priceText);
            Product product = new Product(name, category, price);
            productList.add(product);
            logHistory("ADD", product);
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Price must be a valid number.");
        }
    }

    // ── Update ────────────────────────────────────────────────────
    @FXML
    private void handleUpdate() {
        Product selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select a product to update.");
            return;
        }
        String name      = txtProductName.getText().trim();
        String priceText = txtPrice.getText().trim();
        if (name.isEmpty() || priceText.isEmpty()) {
            showAlert("Validation Error", "Product Name and Price are required.");
            return;
        }
        try {
            double price = Double.parseDouble(priceText);
            selected.setProductName(name);
            selected.setCategory(cmbCategory.getValue());
            selected.setPrice(price);
            tableView.refresh();
            logHistory("UPDATE", selected);
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Price must be a valid number.");
        }
    }

    // ── Delete ────────────────────────────────────────────────────
    @FXML
    private void handleDelete() {
        Product selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select a product to delete.");
            return;
        }
        logHistory("DELETE", selected);
        productList.remove(selected);
        clearFields();
    }

    // ── Helpers ───────────────────────────────────────────────────
    private void clearFields() {
        txtProductName.clear();
        txtPrice.clear();
        cmbCategory.getSelectionModel().selectFirst();
        tableView.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void logHistory(String action, Product product) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(HISTORY_FILE, true))) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pw.printf("[%s] %s -> Name: %s | Category: %s | Price: %.2f%n",
                    timestamp, action,
                    product.getProductName(), product.getCategory(), product.getPrice());
        } catch (IOException e) {
            System.err.println("Could not write to history file: " + e.getMessage());
        }
    }
}