package tt_logistics.controller;

import tt_logistics.MainApp;
import tt_logistics.db.DatabaseConnection;
import tt_logistics.util.UIFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.*;

public class ReportsController {

    private final MainApp app;
    private final Label feedback = UIFactory.feedbackLabel();

    public ReportsController(MainApp app) { this.app = app; }

    public void show() {
        BorderPane root = UIFactory.darkRoot();
        root.setTop(UIFactory.topBar("Reports & Analytics", "#9B59B6", app::showDashboard));

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color: #0D1B2A;");

        tabs.getTabs().addAll(
            new Tab("📋  Active Deliveries",       buildActiveDeliveriesTab()),
            new Tab("👤  Driver Workload",          buildDriverWorkloadTab()),
            new Tab("🔢  Deliveries by Driver",     buildDriverCountTab()),
            new Tab("🔧  Vehicle Maintenance Cost", buildVehicleCostTab()),
            new Tab("📜  Delivery Log",             buildDeliveryLogTab())
        );

        root.setCenter(tabs);
        app.getPrimaryStage().setScene(new Scene(root, 1060, 660));
        app.getPrimaryStage().setTitle("TT Logistics — Reports");
    }

    private VBox buildActiveDeliveriesTab() {
        TableView<ObservableList<String>> tv = makeTable(
                new String[]{"Delivery ID","Date","Client","Vehicle Reg","Status"});

        Button refresh = UIFactory.primaryBtn("⟳ Refresh", "#1E3A5F");
        Label info = new Label("Source: active_deliveries_view  (excludes Completed)");
        info.setStyle("-fx-text-fill: #4A6FA5; -fx-font-size: 11;");

        refresh.setOnAction(e -> {
            tv.getItems().clear();
            try (Statement st = DatabaseConnection.getConnection().createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT deliveryID, deliveryDate, clientName, vehicleRegistration, deliveryStatus " +
                     "FROM active_deliveries_view")) {
                while (rs.next()) tv.getItems().add(rowOf(rs, 5));
            } catch (SQLException ex) { UIFactory.setError(feedback, ex.getMessage()); }
        });
        refresh.fire();

        VBox vb = new VBox(12, new HBox(10, refresh, info), tv, feedback);
        vb.setPadding(new Insets(20));
        VBox.setVgrow(tv, Priority.ALWAYS);
        return vb;
    }

    private VBox buildDriverWorkloadTab() {
        TableView<ObservableList<String>> tv = makeTable(
                new String[]{"Person ID","Driver Name","Deliveries","Total Hours"});

        Button refresh = UIFactory.primaryBtn("⟳ Refresh", "#1E3A5F");
        Label info = new Label("Source: driverworkloadview");
        info.setStyle("-fx-text-fill: #4A6FA5; -fx-font-size: 11;");

        refresh.setOnAction(e -> {
            tv.getItems().clear();
            try (Statement st = DatabaseConnection.getConnection().createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT personID, driverName, numberOfDeliveries, totalHoursWorked " +
                     "FROM driverworkloadview ORDER BY numberOfDeliveries DESC")) {
                while (rs.next()) tv.getItems().add(rowOf(rs, 4));
            } catch (SQLException ex) { UIFactory.setError(feedback, ex.getMessage()); }
        });
        refresh.fire();

        VBox vb = new VBox(12, new HBox(10, refresh, info), tv, feedback);
        vb.setPadding(new Insets(20));
        VBox.setVgrow(tv, Priority.ALWAYS);
        return vb;
    }

    private VBox buildDriverCountTab() {
        ComboBox<String> driverCombo = new ComboBox<>();
        driverCombo.setMaxWidth(300);
        UIFactory.styleControl(driverCombo);
        loadPersonCombo(driverCombo);

        Label result = new Label("—");
        result.setFont(Font.font("Courier New", FontWeight.BOLD, 52));
        result.setStyle("-fx-text-fill: #1E90FF;");

        Label sub = new Label("total deliveries");
        sub.setStyle("-fx-text-fill: #4A6FA5; -fx-font-family: 'Segoe UI'; -fx-font-size: 14;");

        Button runBtn = UIFactory.primaryBtn("Run  totalDeliveriesByDriver()", "#9B59B6");
        runBtn.setOnAction(e -> {
            String sel = driverCombo.getValue();
            if (sel == null) return;
            int pid = Integer.parseInt(sel.split(" \\| ")[0].trim());
            try (PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("SELECT totalDeliveriesByDriver(?)")) {
                ps.setInt(1, pid);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) result.setText(rs.getString(1));
            } catch (SQLException ex) { UIFactory.setError(feedback, ex.getMessage()); }
        });

        VBox resultBox = new VBox(6, result, sub);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setPadding(new Insets(20));

        VBox vb = new VBox(20,
            UIFactory.sectionLabel("Function: totalDeliveriesByDriver(personID)"),
            UIFactory.fieldGroup("SELECT DRIVER", driverCombo),
            runBtn, resultBox, feedback
        );
        vb.setPadding(new Insets(30));
        vb.setMaxWidth(500);
        return vb;
    }

    private VBox buildVehicleCostTab() {
        ComboBox<String> vehicleCombo = new ComboBox<>();
        vehicleCombo.setMaxWidth(300);
        UIFactory.styleControl(vehicleCombo);
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT vehicleID, registrationNumber FROM vehicle ORDER BY registrationNumber")) {
            while (rs.next())
                vehicleCombo.getItems().add(rs.getString(1) + " | " + rs.getString(2));
        } catch (SQLException ignored) {}

        Label result = new Label("M —");
        result.setFont(Font.font("Courier New", FontWeight.BOLD, 52));
        result.setStyle("-fx-text-fill: #E05C5C;");

        Label sub = new Label("total maintenance cost");
        sub.setStyle("-fx-text-fill: #4A6FA5; -fx-font-family: 'Segoe UI'; -fx-font-size: 14;");

        Button runBtn = UIFactory.primaryBtn("Run  vehicleTotalMaintenance()", "#9B59B6");
        runBtn.setOnAction(e -> {
            String sel = vehicleCombo.getValue();
            if (sel == null) return;
            int vid = Integer.parseInt(sel.split(" \\| ")[0].trim());
            try (PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("SELECT vehicleTotalMaintenance(?)")) {
                ps.setInt(1, vid);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) result.setText("M " + rs.getString(1));
            } catch (SQLException ex) { UIFactory.setError(feedback, ex.getMessage()); }
        });

        VBox resultBox = new VBox(6, result, sub);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setPadding(new Insets(20));

        VBox vb = new VBox(20,
            UIFactory.sectionLabel("Function: vehicleTotalMaintenance(vehicleID)"),
            UIFactory.fieldGroup("SELECT VEHICLE", vehicleCombo),
            runBtn, resultBox, feedback
        );
        vb.setPadding(new Insets(30));
        vb.setMaxWidth(500);
        return vb;
    }

    private VBox buildDeliveryLogTab() {
        TableView<ObservableList<String>> tv = makeTable(
                new String[]{"Log ID","Delivery ID","Log Date","Description"});
        tv.getColumns().get(3).setPrefWidth(450);

        Button refresh = UIFactory.primaryBtn("⟳ Refresh", "#1E3A5F");
        Label info = new Label("Populated automatically by trgLogNewDelivery trigger");
        info.setStyle("-fx-text-fill: #4A6FA5; -fx-font-size: 11;");

        refresh.setOnAction(e -> {
            tv.getItems().clear();
            try (Statement st = DatabaseConnection.getConnection().createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT logID, deliveryID, logDate, description FROM deliverylog ORDER BY logID DESC")) {
                while (rs.next()) tv.getItems().add(rowOf(rs, 4));
            } catch (SQLException ex) { UIFactory.setError(feedback, ex.getMessage()); }
        });
        refresh.fire();

        VBox vb = new VBox(12, new HBox(10, refresh, info), tv, feedback);
        vb.setPadding(new Insets(20));
        VBox.setVgrow(tv, Priority.ALWAYS);
        return vb;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private TableView<ObservableList<String>> makeTable(String[] colNames) {
        TableView<ObservableList<String>> tv = new TableView<>();
        UIFactory.styleTable(tv);
        for (int i = 0; i < colNames.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(colNames[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().size() > idx ? d.getValue().get(idx) : ""));
            tv.getColumns().add(col);
        }
        return tv;
    }

    private ObservableList<String> rowOf(ResultSet rs, int cols) throws SQLException {
        ObservableList<String> row = FXCollections.observableArrayList();
        for (int i = 1; i <= cols; i++) row.add(rs.getString(i) == null ? "" : rs.getString(i));
        return row;
    }

    private void loadPersonCombo(ComboBox<String> cb) {
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT personID, fullName FROM person ORDER BY fullName")) {
            while (rs.next())
                cb.getItems().add(rs.getString(1) + " | " + rs.getString(2));
        } catch (SQLException ignored) {}
    }
}
