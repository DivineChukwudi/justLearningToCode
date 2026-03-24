package tt_logistics.controller;

import tt_logistics.MainApp;
import tt_logistics.db.DatabaseConnection;
import tt_logistics.util.UIFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.*;

public class VehicleController {

    private final MainApp app;
    private TableView<ObservableList<String>> table;
    private final Label feedback = UIFactory.feedbackLabel();

    private TextField regField, typeField, capField, purchaseField;
    private ComboBox<String> depotCombo;

    public VehicleController(MainApp app) { this.app = app; }

    public void show() {
        BorderPane root = UIFactory.darkRoot();
        root.setTop(UIFactory.topBar("Vehicle Management", "#1E90FF", app::showDashboard));

        table = new TableView<>();
        UIFactory.styleTable(table);
        String[] cols = {"ID", "Registration", "Type", "Capacity", "Purchase Date", "Depot"};
        for (int i = 0; i < cols.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(cols[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().size() > idx ? d.getValue().get(idx) : ""));
            table.getColumns().add(col);
        }

        Button refreshBtn = UIFactory.primaryBtn("⟳ Refresh", "#1E3A5F");
        Button deleteBtn  = UIFactory.dangerBtn("✕ Delete Selected");
        refreshBtn.setOnAction(e -> loadVehicles());
        deleteBtn.setOnAction(e  -> deleteSelected());

        HBox tableActions = new HBox(10, refreshBtn, deleteBtn);
        tableActions.setPadding(new Insets(0, 0, 10, 0));

        VBox leftPane = new VBox(10, tableActions, table);
        leftPane.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);

        regField      = new TextField(); regField.setPromptText("e.g. ABC-001");
        typeField     = new TextField(); typeField.setPromptText("Truck / Van / Sedan");
        capField      = new TextField(); capField.setPromptText("kg or litres");
        purchaseField = new TextField(); purchaseField.setPromptText("YYYY-MM-DD");
        depotCombo    = new ComboBox<>();
        depotCombo.setMaxWidth(Double.MAX_VALUE);
        depotCombo.setPromptText("Select depot");
        UIFactory.styleControl(depotCombo);
        loadDepotCombo();

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                regField.setText(sel.get(1));
                typeField.setText(sel.get(2));
                capField.setText(sel.get(3));
                purchaseField.setText(sel.get(4));
                depotCombo.setValue(sel.get(5));
            }
        });

        Button addBtn = UIFactory.primaryBtn("+ Add Vehicle", "#1E90FF");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> addVehicle());

        Label formTitle = new Label("ADD / EDIT VEHICLE");
        formTitle.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        formTitle.setStyle("-fx-text-fill: #1E90FF;");

        VBox form = UIFactory.card(24, 24);
        form.setPrefWidth(300);
        form.getChildren().addAll(
            formTitle,
            UIFactory.fieldGroup("REGISTRATION NUMBER", regField),
            UIFactory.fieldGroup("VEHICLE TYPE",        typeField),
            UIFactory.fieldGroup("CAPACITY",            capField),
            UIFactory.fieldGroup("PURCHASE DATE",       purchaseField),
            UIFactory.fieldGroup("DEPOT",               depotCombo),
            addBtn, feedback
        );

        VBox rightPane = new VBox(form);
        rightPane.setPadding(new Insets(20, 20, 20, 0));

        SplitPane split = new SplitPane(leftPane, rightPane);
        split.setDividerPositions(0.65);
        split.setStyle("-fx-background-color: #0D1B2A;");

        root.setCenter(split);
        app.getPrimaryStage().setScene(new Scene(root, 1000, 620));
        app.getPrimaryStage().setTitle("TT Logistics — Vehicle Management");
        loadVehicles();
    }

    private void loadVehicles() {
        table.getItems().clear();
        String sql = "SELECT v.vehicleID, v.registrationNumber, v.vehicleType, " +
                     "v.capacity, v.purchaseDate, COALESCE(d.depotName,'—') " +
                     "FROM vehicle v LEFT JOIN depot d ON v.depotID = d.depotID ORDER BY v.vehicleID";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 6; i++) row.add(rs.getString(i) == null ? "" : rs.getString(i));
                table.getItems().add(row);
            }
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void loadDepotCombo() {
        depotCombo.getItems().clear();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT depotName FROM depot ORDER BY depotName")) {
            while (rs.next()) depotCombo.getItems().add(rs.getString(1));
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void addVehicle() {
        String reg = regField.getText().trim();
        if (reg.isEmpty()) { UIFactory.setError(feedback, "Registration number required."); return; }

        String sql = "INSERT INTO vehicle (registrationNumber, vehicleType, capacity, purchaseDate, depotID) " +
                     "VALUES (?, ?, ?, ?, (SELECT depotID FROM depot WHERE depotName = ?))";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, reg);
            ps.setString(2, typeField.getText().trim().isEmpty()     ? null : typeField.getText().trim());
            ps.setString(3, capField.getText().trim().isEmpty()      ? null : capField.getText().trim());
            ps.setString(4, purchaseField.getText().trim().isEmpty() ? null : purchaseField.getText().trim());
            ps.setString(5, depotCombo.getValue());
            ps.executeUpdate();
            UIFactory.setSuccess(feedback, "Vehicle added successfully.");
            regField.clear(); typeField.clear(); capField.clear();
            purchaseField.clear(); depotCombo.setValue(null);
            loadVehicles();
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void deleteSelected() {
        ObservableList<String> sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { UIFactory.setError(feedback, "Select a vehicle first."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete vehicle \"" + sel.get(1) + "\"?\n\n" +
                "This will also delete all deliveries, driver assignments,\n" +
                "delivery logs and maintenance records for this vehicle.",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Vehicle");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.YES) return;
            int vid = Integer.parseInt(sel.get(0));

            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    // 1. Remove driver assignments linked to deliveries of this vehicle
                    PreparedStatement s1 = conn.prepareStatement(
                        "DELETE da FROM driverassignment da " +
                        "JOIN delivery d ON da.deliveryID = d.deliveryID " +
                        "WHERE d.vehicleID = ?");
                    s1.setInt(1, vid); s1.executeUpdate();

                    // 2. Remove delivery logs linked to deliveries of this vehicle
                    PreparedStatement s2 = conn.prepareStatement(
                        "DELETE dl FROM deliverylog dl " +
                        "JOIN delivery d ON dl.deliveryID = d.deliveryID " +
                        "WHERE d.vehicleID = ?");
                    s2.setInt(1, vid); s2.executeUpdate();

                    // 3. Remove deliveries
                    PreparedStatement s3 = conn.prepareStatement(
                        "DELETE FROM delivery WHERE vehicleID = ?");
                    s3.setInt(1, vid); s3.executeUpdate();

                    // 4. Remove maintenance records
                    PreparedStatement s4 = conn.prepareStatement(
                        "DELETE FROM vehiclemaintenance WHERE vehicleID = ?");
                    s4.setInt(1, vid); s4.executeUpdate();

                    // 5. Finally delete the vehicle
                    PreparedStatement s5 = conn.prepareStatement(
                        "DELETE FROM vehicle WHERE vehicleID = ?");
                    s5.setInt(1, vid); s5.executeUpdate();

                    conn.commit();
                    UIFactory.setSuccess(feedback, "Vehicle \"" + sel.get(1) + "\" deleted successfully.");
                    loadVehicles();

                } catch (SQLException ex) {
                    conn.rollback();
                    UIFactory.setError(feedback, "Delete failed: " + ex.getMessage());
                } finally {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
        });
    }
}