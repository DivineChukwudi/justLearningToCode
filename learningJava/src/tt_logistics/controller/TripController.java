package tt_logistics.controller;

import tt_logistics.MainApp;
import tt_logistics.db.DatabaseConnection;
import tt_logistics.model.Session;
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
import javafx.stage.Stage;

import java.sql.*;

public class TripController {

    private final MainApp app;
    private final boolean isDriver;

    private TableView<ObservableList<String>> deliveryTable;
    private TableView<ObservableList<String>> assignTable;
    private final Label feedback = UIFactory.feedbackLabel();

    private DatePicker datePicker;
    private TextField  originField, destField;
    private ComboBox<String> clientCombo, vehicleCombo, statusCombo;
    private ComboBox<String> driverCombo, roleCombo;
    private TextField hoursField;

    public TripController(MainApp app) {
        this.app      = app;
        this.isDriver = Session.isDriver();
    }

    public void show() {
        BorderPane root = UIFactory.darkRoot();

        if (isDriver) {
            root.setTop(UIFactory.topBar("My Deliveries", "#00C896", app::showDashboard));
            root.setCenter(buildDriverView());
            app.getPrimaryStage().setScene(new Scene(root, 980, 640));
            app.getPrimaryStage().setTitle("TT Logistics — My Deliveries");
            checkAndShowNotifications();
        } else {
            root.setTop(UIFactory.topBar("Trip / Delivery Management", "#F5A623", app::showDashboard));
            TabPane tabs = new TabPane();
            tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            tabs.setStyle("-fx-background-color: #0D1B2A;");
            tabs.getTabs().addAll(
                new Tab("📦  All Deliveries",   buildDeliveryPane()),
                new Tab("👤  Assign Drivers",   buildAssignPane())
            );
            root.setCenter(tabs);
            app.getPrimaryStage().setScene(new Scene(root, 1060, 660));
            app.getPrimaryStage().setTitle("TT Logistics — Trip Management");
            loadDeliveries();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NOTIFICATION POPUP
    // ═══════════════════════════════════════════════════════════════════════════
    private void checkAndShowNotifications() {
        int personID = Session.get().getPersonID();
        if (personID <= 0) return;

        String sql =
            "SELECT n.notificationID, n.assignmentID, n.deliveryID, n.message, " +
            "       d.origin, d.destination, d.deliveryDate, da.role " +
            "FROM notifications n " +
            "JOIN delivery d          ON n.deliveryID   = d.deliveryID " +
            "JOIN driverassignment da ON n.assignmentID = da.assignmentID " +
            "WHERE n.personID = ? AND n.isRead = FALSE " +
            "ORDER BY n.createdAt DESC";

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, personID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int notifID    = rs.getInt(1);
                int assignID   = rs.getInt(2);
                int deliveryID = rs.getInt(3);
                String origin  = rs.getString(5);
                String dest    = rs.getString(6);
                String date    = rs.getString(7) == null ? "TBD" : rs.getString(7);
                String role    = rs.getString(8);
                showJobOfferDialog(notifID, assignID, deliveryID, origin, dest, date, role);
            }
            rs.close();
        } catch (SQLException e) {
            UIFactory.setError(feedback, e.getMessage());
        }
    }

    private void showJobOfferDialog(int notifID, int assignID, int deliveryID,
                                    String origin, String dest, String date, String role) {
        Stage dialogStage = new javafx.stage.Stage();
        dialogStage.setTitle("New Job Assignment");
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox root = new VBox(18);
        root.setPadding(new Insets(32));
        root.setStyle("-fx-background-color: #0D1B2A;");
        root.setAlignment(Pos.TOP_CENTER);

        Label icon = new Label("📦");
        icon.setFont(Font.font(42));

        Label title = new Label("NEW JOB ASSIGNED TO YOU");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #F5A623;");

        Label sub = new Label("Admin has assigned you to a delivery. Do you accept?");
        sub.setStyle("-fx-text-fill: #4A6FA5; -fx-font-family: 'Segoe UI'; -fx-font-size: 12;");
        sub.setWrapText(true);

        VBox details = UIFactory.card(20, 16);
        details.setMaxWidth(400);
        details.getChildren().addAll(
            detailRow("Delivery #",  String.valueOf(deliveryID)),
            detailRow("From",        origin),
            detailRow("To",          dest),
            detailRow("Date",        date),
            detailRow("Your Role",   role.toUpperCase())
        );

        Button acceptBtn  = UIFactory.primaryBtn("✓  Accept Job", "#00C896");
        Button declineBtn = UIFactory.dangerBtn("✕  Decline Job");
        acceptBtn.setPrefWidth(160);
        declineBtn.setPrefWidth(160);

        HBox btnRow = new HBox(16, acceptBtn, declineBtn);
        btnRow.setAlignment(Pos.CENTER);

        Label resultLbl = new Label("");
        resultLbl.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 12;");

        acceptBtn.setOnAction(e -> {
            if (respondToJob(notifID, assignID, true)) {
                UIFactory.setSuccess(resultLbl, "Job accepted! It will appear in your deliveries as PENDING.");
                acceptBtn.setDisable(true);
                declineBtn.setDisable(true);
                loadMyDeliveries();
                javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.8));
                pause.setOnFinished(ev -> dialogStage.close());
                pause.play();
            }
        });

        declineBtn.setOnAction(e -> {
            if (respondToJob(notifID, assignID, false)) {
                UIFactory.setError(resultLbl, "Job declined.");
                acceptBtn.setDisable(true);
                declineBtn.setDisable(true);
                javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
                pause.setOnFinished(ev -> dialogStage.close());
                pause.play();
            }
        });

        root.getChildren().addAll(icon, title, sub, details, btnRow, resultLbl);
        dialogStage.setScene(new Scene(root, 460, 420));
        dialogStage.showAndWait();
    }

    private boolean respondToJob(int notifID, int assignID, boolean accepted) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String newStatus = accepted ? "ACCEPTED" : "DECLINED";
                PreparedStatement ps1 = conn.prepareStatement(
                    "UPDATE driverassignment SET acceptanceStatus=? WHERE assignmentID=?");
                ps1.setString(1, newStatus);
                ps1.setInt(2, assignID);
                ps1.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE notifications SET isRead=TRUE WHERE notificationID=?");
                ps2.setInt(1, notifID);
                ps2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                UIFactory.setError(feedback, ex.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            UIFactory.setError(feedback, e.getMessage());
            return false;
        }
    }

    private HBox detailRow(String label, String value) {
        Label lbl = new Label(label + ":");
        lbl.setStyle("-fx-text-fill: #4A6FA5; -fx-font-family: 'Segoe UI'; " +
                     "-fx-font-weight: bold; -fx-font-size: 12; -fx-min-width: 90;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: #AECBEB; -fx-font-family: 'Segoe UI'; -fx-font-size: 13;");
        HBox row = new HBox(10, lbl, val);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DRIVER VIEW
    // ═══════════════════════════════════════════════════════════════════════════
    private BorderPane buildDriverView() {
        BorderPane pane = new BorderPane();
        pane.setStyle("-fx-background-color: #0D1B2A;");

        deliveryTable = new TableView<>();
        UIFactory.styleTable(deliveryTable);
        String[] cols = {"Del ID","Date","Origin","Destination","Role","Status","Client","Vehicle"};
        for (int i = 0; i < cols.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(cols[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().size() > idx ? d.getValue().get(idx) : ""));
            if (i == 5) {
                col.setCellFactory(tc -> new TableCell<>() {
                    @Override protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) { setText(null); setGraphic(null); return; }
                        setText(item);
                        setStyle("-fx-text-fill: " + statusColor(item) + "; -fx-font-weight: bold;");
                    }
                });
            }
            deliveryTable.getColumns().add(col);
        }

        Button notifBtn = UIFactory.primaryBtn("🔔 Check New Jobs", "#F5A623");
        notifBtn.setOnAction(e -> checkAndShowNotifications());
        Button refresh = UIFactory.primaryBtn("⟳ Refresh", "#1E3A5F");
        refresh.setOnAction(e -> loadMyDeliveries());
        Label statsLabel = buildDriverStats();

        HBox topRow = new HBox(10, refresh, notifBtn, statsLabel);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setPadding(new Insets(0, 0, 10, 0));
        HBox.setHgrow(statsLabel, Priority.ALWAYS);

        VBox left = new VBox(10, topRow, deliveryTable);
        left.setPadding(new Insets(20));
        VBox.setVgrow(deliveryTable, Priority.ALWAYS);

        VBox statusPanel = UIFactory.card(24, 20);
        statusPanel.setPrefWidth(300);

        Label panelTitle = new Label("UPDATE JOB STATUS");
        panelTitle.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        panelTitle.setStyle("-fx-text-fill: #00C896;");

        Label selectedInfo = new Label("Select a delivery from the table");
        selectedInfo.setStyle("-fx-text-fill: #4A6FA5; -fx-font-family: 'Segoe UI'; -fx-font-size: 12;");
        selectedInfo.setWrapText(true);

        ComboBox<String> newStatus = new ComboBox<>(FXCollections.observableArrayList(
                "PENDING", "IN TRANSIT", "COMPLETED", "CANCELLED"));
        newStatus.setMaxWidth(Double.MAX_VALUE);
        newStatus.setValue("IN TRANSIT");
        UIFactory.styleControl(newStatus);

        Button updateBtn = UIFactory.primaryBtn("✓ Update Status", "#00C896");
        updateBtn.setMaxWidth(Double.MAX_VALUE);
        updateBtn.setDisable(true);

        deliveryTable.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            if (sel != null) {
                selectedInfo.setText(
                    "Delivery #" + sel.get(0) + "\n" +
                    sel.get(2) + "  →  " + sel.get(3) + "\n" +
                    "Current status: " + sel.get(5));
                newStatus.setValue(sel.get(5));
                updateBtn.setDisable(false);
            }
        });

        updateBtn.setOnAction(e -> {
            ObservableList<String> sel = deliveryTable.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            updateDeliveryStatus(Integer.parseInt(sel.get(0)), newStatus.getValue(), selectedInfo);
        });

        VBox legend = new VBox(6);
        legend.setPadding(new Insets(12, 0, 0, 0));
        legend.getChildren().add(UIFactory.sectionLabel("STATUS GUIDE"));
        for (String[] s : new String[][]{
                {"PENDING","#4A6FA5","Job accepted, not started"},
                {"IN TRANSIT","#F5A623","Currently on the road"},
                {"COMPLETED","#00C896","Job finished"},
                {"CANCELLED","#E05C5C","Job cancelled"}}) {
            Label l = new Label("● " + s[0] + " — " + s[2]);
            l.setStyle("-fx-text-fill: " + s[1] + "; -fx-font-family: 'Segoe UI'; -fx-font-size: 11;");
            legend.getChildren().add(l);
        }

        statusPanel.getChildren().addAll(
            panelTitle, new Separator(),
            UIFactory.sectionLabel("SELECTED DELIVERY"), selectedInfo,
            UIFactory.fieldGroup("SET STATUS TO", newStatus),
            updateBtn, legend, feedback);

        VBox right = new VBox(statusPanel);
        right.setPadding(new Insets(20, 20, 20, 0));

        SplitPane split = new SplitPane(left, right);
        split.setDividerPositions(0.65);
        split.setStyle("-fx-background-color: #0D1B2A;");
        pane.setCenter(split);
        loadMyDeliveries();
        return pane;
    }

    private Label buildDriverStats() {
        int personID = Session.get().getPersonID();
        Label lbl = new Label("");
        lbl.setStyle("-fx-text-fill: #4A6FA5; -fx-font-family: 'Segoe UI'; -fx-font-size: 12;");
        if (personID <= 0) return lbl;
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                // FIX: renamed function
                .prepareStatement("SELECT total_deliveries_by_driver(?)")) {
            ps.setInt(1, personID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) lbl.setText("Total deliveries assigned: " + rs.getString(1));
        } catch (SQLException ignored) {}
        return lbl;
    }

    private void loadMyDeliveries() {
        if (deliveryTable == null) return;
        deliveryTable.getItems().clear();
        int personID = Session.get().getPersonID();
        if (personID <= 0) {
            UIFactory.setError(feedback, "Your account is not linked to a person record. Contact admin.");
            return;
        }
        String sql =
            "SELECT d.deliveryID, d.deliveryDate, d.origin, d.destination, " +
            "da.role, d.deliveryStatus, c.clientName, v.registrationNumber " +
            "FROM driverassignment da " +
            "JOIN delivery d  ON da.deliveryID = d.deliveryID " +
            "JOIN client c    ON d.clientID    = c.clientID " +
            "JOIN vehicle v   ON d.vehicleID   = v.vehicleID " +
            "WHERE da.personID = ? AND da.acceptanceStatus = 'ACCEPTED' " +
            "ORDER BY d.deliveryID DESC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, personID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 8; i++) row.add(rs.getString(i) == null ? "" : rs.getString(i));
                deliveryTable.getItems().add(row);
            }
            if (deliveryTable.getItems().isEmpty())
                UIFactory.setError(feedback, "No active jobs. Click '🔔 Check New Jobs' if you have pending requests.");
            else
                feedback.setText("");
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void updateDeliveryStatus(int deliveryID, String newStatusVal, Label selectedInfo) {
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE delivery SET deliveryStatus=? WHERE deliveryID=?")) {
            ps.setString(1, newStatusVal);
            ps.setInt(2, deliveryID);
            ps.executeUpdate();
            UIFactory.setSuccess(feedback, "Delivery #" + deliveryID + " → " + newStatusVal);
            loadMyDeliveries();
            selectedInfo.setText("Select a delivery from the table");
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ADMIN / MANAGER VIEWS
    // ═══════════════════════════════════════════════════════════════════════════
    private SplitPane buildDeliveryPane() {
        deliveryTable = new TableView<>();
        UIFactory.styleTable(deliveryTable);
        String[] cols = {"ID","Date","Origin","Destination","Status","Client","Vehicle"};
        for (int i = 0; i < cols.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(cols[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().size() > idx ? d.getValue().get(idx) : ""));
            if (i == 4) {
                col.setCellFactory(tc -> new TableCell<>() {
                    @Override protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) { setText(null); return; }
                        setText(item);
                        setStyle("-fx-text-fill: " + statusColor(item) + "; -fx-font-weight: bold;");
                    }
                });
            }
            deliveryTable.getColumns().add(col);
        }

        Button refresh = UIFactory.primaryBtn("⟳ Refresh", "#1E3A5F");
        Button delete  = UIFactory.dangerBtn("✕ Delete");
        refresh.setOnAction(e -> loadDeliveries());
        delete.setOnAction(e  -> deleteDelivery());

        VBox left = new VBox(10, new HBox(10, refresh, delete), deliveryTable);
        left.setPadding(new Insets(20));
        VBox.setVgrow(deliveryTable, Priority.ALWAYS);

        datePicker   = new DatePicker();
        originField  = new TextField(); originField.setPromptText("Origin depot / city");
        destField    = new TextField(); destField.setPromptText("Destination");
        clientCombo  = new ComboBox<>(); clientCombo.setMaxWidth(Double.MAX_VALUE);
        vehicleCombo = new ComboBox<>(); vehicleCombo.setMaxWidth(Double.MAX_VALUE);
        statusCombo  = new ComboBox<>(FXCollections.observableArrayList(
                "PENDING","IN TRANSIT","COMPLETED","CANCELLED"));
        statusCombo.setValue("PENDING"); statusCombo.setMaxWidth(Double.MAX_VALUE);
        UIFactory.styleControl(datePicker);
        UIFactory.styleControl(clientCombo);
        UIFactory.styleControl(vehicleCombo);
        UIFactory.styleControl(statusCombo);
        loadClientCombo(); loadVehicleCombo();

        deliveryTable.getSelectionModel().selectedItemProperty().addListener((obs,o,sel) -> {
            if (sel != null) statusCombo.setValue(sel.get(4));
        });

        Button addBtn    = UIFactory.primaryBtn("+ Create Delivery", "#F5A623");
        Button updateBtn = UIFactory.primaryBtn("↑ Update Status of Selected", "#1E90FF");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        updateBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e    -> addDelivery());
        updateBtn.setOnAction(e -> updateSelectedStatus());

        Label ft = new Label("NEW DELIVERY");
        ft.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        ft.setStyle("-fx-text-fill: #F5A623;");

        VBox form = UIFactory.card(24, 24); form.setPrefWidth(300);
        form.getChildren().addAll(ft,
            UIFactory.fieldGroup("DATE",        datePicker),
            UIFactory.fieldGroup("ORIGIN",      originField),
            UIFactory.fieldGroup("DESTINATION", destField),
            UIFactory.fieldGroup("CLIENT",      clientCombo),
            UIFactory.fieldGroup("VEHICLE",     vehicleCombo),
            UIFactory.fieldGroup("STATUS",      statusCombo),
            addBtn, updateBtn, feedback);

        VBox right = new VBox(form); right.setPadding(new Insets(20,20,20,0));
        SplitPane split = new SplitPane(left, right);
        split.setDividerPositions(0.65);
        split.setStyle("-fx-background-color: #0D1B2A;");
        return split;
    }

    private SplitPane buildAssignPane() {
        assignTable = new TableView<>();
        UIFactory.styleTable(assignTable);
        String[] cols = {"Assign ID","Driver","Delivery ID","Role","Hours","Accepted"};
        for (int i = 0; i < cols.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(cols[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().size() > idx ? d.getValue().get(idx) : ""));
            if (i == 5) {
                col.setCellFactory(tc -> new TableCell<>() {
                    @Override protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) { setText(null); return; }
                        setText(item);
                        String c = switch (item) {
                            case "ACCEPTED"           -> "#00C896";
                            case "DECLINED"           -> "#E05C5C";
                            case "PENDING_ACCEPTANCE" -> "#F5A623";
                            default                   -> "#AECBEB";
                        };
                        setStyle("-fx-text-fill: " + c + "; -fx-font-weight: bold;");
                    }
                });
            }
            assignTable.getColumns().add(col);
        }

        Button refresh = UIFactory.primaryBtn("⟳ Refresh", "#1E3A5F");
        refresh.setOnAction(e -> loadAssignments());

        VBox left = new VBox(10, new HBox(refresh), assignTable);
        left.setPadding(new Insets(20));
        VBox.setVgrow(assignTable, Priority.ALWAYS);

        ComboBox<String> deliveryCombo = new ComboBox<>();
        deliveryCombo.setMaxWidth(Double.MAX_VALUE);
        driverCombo = new ComboBox<>(); driverCombo.setMaxWidth(Double.MAX_VALUE);
        roleCombo   = new ComboBox<>(FXCollections.observableArrayList("main driver","assistant"));
        roleCombo.setValue("assistant"); roleCombo.setMaxWidth(Double.MAX_VALUE);
        hoursField  = new TextField(); hoursField.setPromptText("e.g. 4.5");
        UIFactory.styleControl(deliveryCombo);
        UIFactory.styleControl(driverCombo);
        UIFactory.styleControl(roleCombo);
        loadDeliveryCombo(deliveryCombo); loadDriverCombo();

        Button assignBtn = UIFactory.primaryBtn("📤 Assign & Notify Driver", "#F5A623");
        assignBtn.setMaxWidth(Double.MAX_VALUE);
        assignBtn.setOnAction(e -> {
            String dSel   = driverCombo.getValue();
            String delSel = deliveryCombo.getValue();
            String role   = roleCombo.getValue();
            String hours  = hoursField.getText().trim();
            if (dSel == null || delSel == null || hours.isEmpty()) {
                UIFactory.setError(feedback, "Fill all fields."); return;
            }
            int personID   = Integer.parseInt(dSel.split(" \\| ")[0].trim());
            int deliveryID = Integer.parseInt(delSel.split(" \\| ")[0].trim());
            String driverName = dSel.split(" \\| ").length > 1 ? dSel.split(" \\| ")[1] : "Driver";
            assignDriverAndNotify(personID, deliveryID, role, hours, driverName, delSel);
        });

        Label at = new Label("ASSIGN DRIVER TO DELIVERY");
        at.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        at.setStyle("-fx-text-fill: #F5A623;");

        Label notifNote = new Label("ℹ  Driver will receive a notification and must accept before the job appears in their portal.");
        notifNote.setStyle("-fx-text-fill: #4A6FA5; -fx-font-family: 'Segoe UI'; -fx-font-size: 11;");
        notifNote.setWrapText(true);

        VBox form = UIFactory.card(24, 24); form.setPrefWidth(310);
        form.getChildren().addAll(at,
            UIFactory.fieldGroup("DELIVERY",     deliveryCombo),
            UIFactory.fieldGroup("DRIVER",       driverCombo),
            UIFactory.fieldGroup("ROLE",         roleCombo),
            UIFactory.fieldGroup("HOURS WORKED", hoursField),
            notifNote, assignBtn, feedback);

        VBox right = new VBox(form); right.setPadding(new Insets(20,20,20,0));
        SplitPane split = new SplitPane(left, right);
        split.setDividerPositions(0.65);
        split.setStyle("-fx-background-color: #0D1B2A;");
        loadAssignments();
        return split;
    }

    private void assignDriverAndNotify(int personID, int deliveryID, String role,
                                       String hours, String driverName, String delLabel) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // FIX: renamed procedure → assign_driver_to_delivery
                CallableStatement cs = conn.prepareCall("{CALL assign_driver_to_delivery(?,?,?,?)}");
                cs.setInt(1, personID);
                cs.setInt(2, deliveryID);
                cs.setString(3, role);
                cs.setDouble(4, Double.parseDouble(hours));
                cs.execute();
                cs.close();

                PreparedStatement getID = conn.prepareStatement(
                    "SELECT assignmentID FROM driverassignment " +
                    "WHERE personID=? AND deliveryID=? ORDER BY assignmentID DESC LIMIT 1");
                getID.setInt(1, personID); getID.setInt(2, deliveryID);
                ResultSet rs = getID.executeQuery();
                if (!rs.next()) { conn.rollback(); UIFactory.setError(feedback,"Could not get assignment ID."); return; }
                int assignmentID = rs.getInt(1);
                rs.close(); getID.close();

                PreparedStatement dInfo = conn.prepareStatement(
                    "SELECT origin, destination, deliveryDate FROM delivery WHERE deliveryID=?");
                dInfo.setInt(1, deliveryID);
                ResultSet dr = dInfo.executeQuery();
                String origin="?", destination="?", date="TBD";
                if (dr.next()) {
                    origin      = dr.getString(1) == null ? "?" : dr.getString(1);
                    destination = dr.getString(2) == null ? "?" : dr.getString(2);
                    date        = dr.getString(3) == null ? "TBD" : dr.getString(3);
                }
                dr.close(); dInfo.close();

                String message = "Admin has assigned you to delivery #" + deliveryID +
                                 " (" + origin + " → " + destination + ") on " + date +
                                 " as " + role + ". Please accept or decline.";
                PreparedStatement notif = conn.prepareStatement(
                    "INSERT INTO notifications (personID, deliveryID, assignmentID, message) VALUES (?,?,?,?)");
                notif.setInt(1, personID);
                notif.setInt(2, deliveryID);
                notif.setInt(3, assignmentID);
                notif.setString(4, message);
                notif.executeUpdate();
                notif.close();

                conn.commit();
                UIFactory.setSuccess(feedback,
                    "✓ " + driverName + " assigned and notified. Waiting for their acceptance.");
                hoursField.clear();
                loadAssignments();

            } catch (Exception ex) {
                conn.rollback();
                UIFactory.setError(feedback, ex.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    // ── Data loaders ──────────────────────────────────────────────────────────
    private void loadDeliveries() {
        deliveryTable.getItems().clear();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT d.deliveryID,d.deliveryDate,d.origin,d.destination,d.deliveryStatus," +
                "c.clientName,v.registrationNumber FROM delivery d " +
                "JOIN client c ON d.clientID=c.clientID " +
                "JOIN vehicle v ON d.vehicleID=v.vehicleID ORDER BY d.deliveryID DESC")) {
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 7; i++) row.add(rs.getString(i) == null ? "" : rs.getString(i));
                deliveryTable.getItems().add(row);
            }
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void loadAssignments() {
        if (assignTable == null) return;
        assignTable.getItems().clear();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT da.assignmentID, p.fullName, da.deliveryID, da.role, " +
                "da.hoursWorked, da.acceptanceStatus " +
                "FROM driverassignment da JOIN person p ON da.personID=p.personID " +
                "ORDER BY da.assignmentID DESC")) {
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 6; i++) row.add(rs.getString(i) == null ? "" : rs.getString(i));
                assignTable.getItems().add(row);
            }
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void loadClientCombo() {
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT clientID,clientName FROM client ORDER BY clientName"))
        { while (rs.next()) clientCombo.getItems().add(rs.getString(1)+" | "+rs.getString(2)); }
        catch (SQLException ignored) {}
    }

    private void loadVehicleCombo() {
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT vehicleID,registrationNumber FROM vehicle ORDER BY registrationNumber"))
        { while (rs.next()) vehicleCombo.getItems().add(rs.getString(1)+" | "+rs.getString(2)); }
        catch (SQLException ignored) {}
    }

    private void loadDeliveryCombo(ComboBox<String> cb) {
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT deliveryID,origin,destination FROM delivery ORDER BY deliveryID DESC"))
        { while (rs.next()) cb.getItems().add(rs.getString(1)+" | "+rs.getString(2)+" → "+rs.getString(3)); }
        catch (SQLException ignored) {}
    }

    private void loadDriverCombo() {
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT personID,fullName FROM person ORDER BY fullName"))
        { while (rs.next()) driverCombo.getItems().add(rs.getString(1)+" | "+rs.getString(2)); }
        catch (SQLException ignored) {}
    }

    private void addDelivery() {
        String cs = clientCombo.getValue(), vs = vehicleCombo.getValue();
        if (cs == null || vs == null) { UIFactory.setError(feedback, "Select client and vehicle."); return; }
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(
                "INSERT INTO delivery (deliveryDate,origin,destination,deliveryStatus,clientID,vehicleID) VALUES (?,?,?,?,?,?)")) {
            ps.setString(1, datePicker.getValue() == null ? null : datePicker.getValue().toString());
            ps.setString(2, originField.getText().trim().isEmpty() ? null : originField.getText().trim());
            ps.setString(3, destField.getText().trim().isEmpty()   ? null : destField.getText().trim());
            ps.setString(4, statusCombo.getValue());
            ps.setInt(5, Integer.parseInt(cs.split(" \\| ")[0].trim()));
            ps.setInt(6, Integer.parseInt(vs.split(" \\| ")[0].trim()));
            ps.executeUpdate();
            UIFactory.setSuccess(feedback, "Delivery created — trigger logged it."); loadDeliveries();
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void updateSelectedStatus() {
        ObservableList<String> sel = deliveryTable.getSelectionModel().getSelectedItem();
        if (sel == null) { UIFactory.setError(feedback, "Select a row first."); return; }
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("UPDATE delivery SET deliveryStatus=? WHERE deliveryID=?")) {
            ps.setString(1, statusCombo.getValue()); ps.setInt(2, Integer.parseInt(sel.get(0)));
            ps.executeUpdate();
            UIFactory.setSuccess(feedback, "Status updated to " + statusCombo.getValue()); loadDeliveries();
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void deleteDelivery() {
        ObservableList<String> sel = deliveryTable.getSelectionModel().getSelectedItem();
        if (sel == null) { UIFactory.setError(feedback, "Select a delivery."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Delete delivery #" + sel.get(0) + "?",
                ButtonType.YES, ButtonType.NO).showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.YES) return;
            int did = Integer.parseInt(sel.get(0));
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    for (String q : new String[]{
                        "DELETE FROM notifications WHERE deliveryID=?",
                        "DELETE FROM driverassignment WHERE deliveryID=?",
                        "DELETE FROM deliverylog WHERE deliveryID=?",
                        "DELETE FROM delivery WHERE deliveryID=?"}) {
                        PreparedStatement s = conn.prepareStatement(q);
                        s.setInt(1, did); s.executeUpdate();
                    }
                    conn.commit();
                    UIFactory.setSuccess(feedback, "Delivery #" + did + " deleted.");
                    loadDeliveries();
                } catch (SQLException ex) {
                    conn.rollback();
                    UIFactory.setError(feedback, "Delete failed: " + ex.getMessage());
                } finally { conn.setAutoCommit(true); }
            } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
        });
    }

    private String statusColor(String status) {
        if (status == null) return "#4A6FA5";
        return switch (status.toUpperCase()) {
            case "COMPLETED"  -> "#00C896";
            case "IN TRANSIT" -> "#F5A623";
            case "PENDING"    -> "#4A6FA5";
            case "CANCELLED"  -> "#E05C5C";
            default           -> "#AECBEB";
        };
    }
}