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

public class MaintenanceController {

    private final MainApp app;
    private TableView<ObservableList<String>> table;
    private final Label feedback = UIFactory.feedbackLabel();

    private ComboBox<String> vehicleCombo;
    private TextField dateField, typeField, costField;
    private Label totalLabel;

    public MaintenanceController(MainApp app) { this.app = app; }

    public void show() {
        BorderPane root = UIFactory.darkRoot();
        root.setTop(UIFactory.topBar("Vehicle Maintenance", "#E05C5C", app::showDashboard));

        table = new TableView<>();
        UIFactory.styleTable(table);
        String[] cols = {"ID","Vehicle","Date","Type","Cost (M)"};
        for (int i = 0; i < cols.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(cols[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().size() > idx ? d.getValue().get(idx) : ""));
            table.getColumns().add(col);
        }

        totalLabel = new Label("Select a vehicle to see total maintenance cost");
        totalLabel.setStyle("-fx-text-fill: #AECBEB; -fx-font-family: 'Segoe UI'; -fx-font-size: 13;");

        Button refresh = UIFactory.primaryBtn("⟳ Refresh", "#1E3A5F");
        refresh.setOnAction(e -> loadMaintenance());

        VBox left = new VBox(10, new HBox(refresh), table, totalLabel);
        left.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);

        vehicleCombo = new ComboBox<>();
        vehicleCombo.setMaxWidth(Double.MAX_VALUE);
        UIFactory.styleControl(vehicleCombo);
        loadVehicleCombo();

        // FIX: calls vehicle_total_maintenance (renamed)
        vehicleCombo.setOnAction(e -> refreshTotalCost());

        dateField = new TextField(); dateField.setPromptText("YYYY-MM-DD");
        typeField = new TextField(); typeField.setPromptText("Oil Change / Tyres / etc.");
        costField = new TextField(); costField.setPromptText("Cost in Maloti");

        Button addBtn = UIFactory.primaryBtn("+ Record via Stored Procedure", "#E05C5C");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> recordMaintenance());

        Label formTitle = new Label("RECORD MAINTENANCE");
        formTitle.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        formTitle.setStyle("-fx-text-fill: #E05C5C;");

        VBox form = UIFactory.card(24, 24);
        form.setPrefWidth(300);
        form.getChildren().addAll(
            formTitle,
            UIFactory.fieldGroup("VEHICLE",          vehicleCombo),
            UIFactory.fieldGroup("MAINTENANCE DATE", dateField),
            UIFactory.fieldGroup("MAINTENANCE TYPE", typeField),
            UIFactory.fieldGroup("COST (M)",         costField),
            addBtn, feedback
        );

        VBox right = new VBox(form);
        right.setPadding(new Insets(20,20,20,0));

        SplitPane split = new SplitPane(left, right);
        split.setDividerPositions(0.65);
        split.setStyle("-fx-background-color: #0D1B2A;");

        root.setCenter(split);
        app.getPrimaryStage().setScene(new Scene(root, 1000, 620));
        app.getPrimaryStage().setTitle("TT Logistics — Maintenance");
        loadMaintenance();
    }

    private void loadMaintenance() {
        table.getItems().clear();
        String sql = "SELECT m.maintenanceID, v.registrationNumber, m.maintenanceDate, " +
                     "m.maintenanceType, m.cost " +
                     "FROM vehiclemaintenance m " +
                     "JOIN vehicle v ON m.vehicleID = v.vehicleID " +
                     "ORDER BY m.maintenanceID DESC";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 5; i++) row.add(rs.getString(i) == null ? "" : rs.getString(i));
                table.getItems().add(row);
            }
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void loadVehicleCombo() {
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT vehicleID, registrationNumber FROM vehicle ORDER BY registrationNumber")) {
            while (rs.next())
                vehicleCombo.getItems().add(rs.getString(1) + " | " + rs.getString(2));
        } catch (SQLException ignored) {}
    }

    private void refreshTotalCost() {
        String sel = vehicleCombo.getValue();
        if (sel == null) return;
        int vid = Integer.parseInt(sel.split(" \\| ")[0].trim());
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                // FIX: renamed function → vehicle_total_maintenance
                .prepareStatement("SELECT vehicle_total_maintenance(?)")) {
            ps.setInt(1, vid);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                totalLabel.setText("Total maintenance cost for " + sel.split("\\| ")[1] + ":  M " + rs.getString(1));
            rs.close();
        } catch (SQLException ex) { UIFactory.setError(feedback, ex.getMessage()); }
    }

    private void recordMaintenance() {
        String vSel = vehicleCombo.getValue();
        if (vSel == null)                          { UIFactory.setError(feedback, "Select a vehicle."); return; }
        if (dateField.getText().trim().isEmpty())  { UIFactory.setError(feedback, "Date required.");   return; }
        if (costField.getText().trim().isEmpty())  { UIFactory.setError(feedback, "Cost required.");   return; }

        int vid = Integer.parseInt(vSel.split(" \\| ")[0].trim());
        try (CallableStatement cs = DatabaseConnection.getConnection()
                // FIX: renamed procedure → record_vehicle_maintenance
                .prepareCall("{CALL record_vehicle_maintenance(?,?,?,?)}")) {
            cs.setInt(1, vid);
            cs.setString(2, dateField.getText().trim());
            cs.setString(3, typeField.getText().trim().isEmpty() ? "General" : typeField.getText().trim());
            cs.setDouble(4, Double.parseDouble(costField.getText().trim()));
            cs.execute();
            UIFactory.setSuccess(feedback, "Maintenance recorded via stored procedure.");
            dateField.clear(); typeField.clear(); costField.clear();
            loadMaintenance();
            refreshTotalCost();
        } catch (NumberFormatException ex) {
            UIFactory.setError(feedback, "Cost must be a valid number.");
        } catch (SQLException ex) {
            UIFactory.setError(feedback, ex.getMessage());
        }
    }
}