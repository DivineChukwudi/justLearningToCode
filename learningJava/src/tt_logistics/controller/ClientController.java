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

public class ClientController {

    private final MainApp app;
    private TableView<ObservableList<String>> table;
    private final Label feedback = UIFactory.feedbackLabel();
    private TextField nameField, phoneField, addressField;

    public ClientController(MainApp app) { this.app = app; }

    public void show() {
        BorderPane root = UIFactory.darkRoot();
        root.setTop(UIFactory.topBar("Client Management", "#1ABC9C", app::showDashboard));

        table = new TableView<>();
        UIFactory.styleTable(table);
        String[] cols = {"ID","Client Name","Phone","Address"};
        for (int i = 0; i < cols.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(cols[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().size() > idx ? d.getValue().get(idx) : ""));
            table.getColumns().add(col);
        }

        Button refresh = UIFactory.primaryBtn("⟳ Refresh", "#1E3A5F");
        Button delete  = UIFactory.dangerBtn("✕ Delete");
        refresh.setOnAction(e -> loadClients());
        delete.setOnAction(e  -> deleteClient());

        HBox actions = new HBox(10, refresh, delete);
        actions.setPadding(new Insets(0,0,10,0));

        VBox left = new VBox(10, actions, table);
        left.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);

        table.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            if (sel != null) {
                nameField.setText(sel.get(1));
                phoneField.setText(sel.get(2));
                addressField.setText(sel.get(3));
            }
        });

        nameField    = new TextField(); nameField.setPromptText("Company or person name");
        phoneField   = new TextField(); phoneField.setPromptText("Contact phone");
        addressField = new TextField(); addressField.setPromptText("Address");

        Button addBtn = UIFactory.primaryBtn("+ Add Client", "#1ABC9C");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> addClient());

        Label formTitle = new Label("ADD CLIENT");
        formTitle.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        formTitle.setStyle("-fx-text-fill: #1ABC9C;");

        VBox form = UIFactory.card(24, 24);
        form.setPrefWidth(280);
        form.getChildren().addAll(
            formTitle,
            UIFactory.fieldGroup("CLIENT NAME",   nameField),
            UIFactory.fieldGroup("CONTACT PHONE", phoneField),
            UIFactory.fieldGroup("ADDRESS",       addressField),
            addBtn, feedback
        );

        VBox right = new VBox(form);
        right.setPadding(new Insets(20,20,20,0));

        SplitPane split = new SplitPane(left, right);
        split.setDividerPositions(0.65);
        split.setStyle("-fx-background-color: #0D1B2A;");

        root.setCenter(split);
        app.getPrimaryStage().setScene(new Scene(root, 960, 580));
        app.getPrimaryStage().setTitle("TT Logistics — Client Management");
        loadClients();
    }

    private void loadClients() {
        table.getItems().clear();
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT clientID, clientName, contactPhone, address FROM client ORDER BY clientID")) {
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 4; i++) row.add(rs.getString(i) == null ? "" : rs.getString(i));
                table.getItems().add(row);
            }
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void addClient() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { UIFactory.setError(feedback, "Client name required."); return; }
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("INSERT INTO client (clientName, contactPhone, address) VALUES (?,?,?)")) {
            ps.setString(1, name);
            ps.setString(2, phoneField.getText().trim().isEmpty()   ? null : phoneField.getText().trim());
            ps.setString(3, addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());
            ps.executeUpdate();
            UIFactory.setSuccess(feedback, "Client added.");
            nameField.clear(); phoneField.clear(); addressField.clear();
            loadClients();
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void deleteClient() {
        ObservableList<String> sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { UIFactory.setError(feedback, "Select a client first."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete client " + sel.get(1) + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try (PreparedStatement ps = DatabaseConnection.getConnection()
                        .prepareStatement("DELETE FROM client WHERE clientID=?")) {
                    ps.setInt(1, Integer.parseInt(sel.get(0)));
                    ps.executeUpdate();
                    UIFactory.setSuccess(feedback, "Client deleted.");
                    loadClients();
                } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
            }
        });
    }
}
