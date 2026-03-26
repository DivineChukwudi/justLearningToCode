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
import java.sql.Connection;

public class DriverController {

    private final MainApp app;
    private TableView<ObservableList<String>> personTable;
    private final Label feedback = UIFactory.feedbackLabel();

    private TextField nameField, addressField, phoneField, dobField;
    private CheckBox ftCheck, ctCheck, fmCheck;
    private TextField ftEmpNum, ftSalary, ftHireDate;
    // FIX: renamed to match new column names: contractNumber, hourlyRate
    private TextField ctContractNum, ctHourlyRate;
    private TextField fmLevel, fmOffice;

    public DriverController(MainApp app) { this.app = app; }

    public void show() {
        BorderPane root = UIFactory.darkRoot();
        root.setTop(UIFactory.topBar("Driver & Personnel Management", "#00C896", app::showDashboard));

        personTable = new TableView<>();
        UIFactory.styleTable(personTable);
        String[] cols = {"ID", "Full Name", "Phone", "DOB", "Roles"};
        for (int i = 0; i < cols.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(cols[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().size() > idx ? d.getValue().get(idx) : ""));
            personTable.getColumns().add(col);
        }

        Button refreshBtn = UIFactory.primaryBtn("⟳ Refresh", "#1E3A5F");
        Button deleteBtn  = UIFactory.dangerBtn("✕ Delete Person");
        refreshBtn.setOnAction(e -> loadPersons());
        deleteBtn.setOnAction(e  -> deletePerson());

        HBox tblActions = new HBox(10, refreshBtn, deleteBtn);
        tblActions.setPadding(new Insets(0, 0, 10, 0));

        VBox leftPane = new VBox(10, tblActions, personTable);
        leftPane.setPadding(new Insets(20));
        VBox.setVgrow(personTable, Priority.ALWAYS);

        personTable.getSelectionModel().selectedItemProperty().addListener((obs, o, sel) -> {
            if (sel != null) {
                nameField.setText(sel.get(1));
                phoneField.setText(sel.get(2));
                dobField.setText(sel.get(3));
                loadRoleDetails(Integer.parseInt(sel.get(0)));
            }
        });

        ScrollPane formScroll = buildForm();
        formScroll.setPrefWidth(340);

        SplitPane split = new SplitPane(leftPane, formScroll);
        split.setDividerPositions(0.60);
        split.setStyle("-fx-background-color: #0D1B2A;");

        root.setCenter(split);
        app.getPrimaryStage().setScene(new Scene(root, 1060, 660));
        app.getPrimaryStage().setTitle("TT Logistics — Driver Management");
        loadPersons();
    }

    private ScrollPane buildForm() {
        VBox form = UIFactory.card(24, 24);

        Label title = new Label("ADD / EDIT PERSON");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill: #00C896;");

        nameField    = new TextField(); nameField.setPromptText("Full name");
        addressField = new TextField(); addressField.setPromptText("Address");
        phoneField   = new TextField(); phoneField.setPromptText("Phone number");
        dobField     = new TextField(); dobField.setPromptText("YYYY-MM-DD");

        ftCheck = new CheckBox("Full-Time Driver");
        ctCheck = new CheckBox("Contract Driver");
        fmCheck = new CheckBox("Fleet Manager");
        for (CheckBox cb : new CheckBox[]{ftCheck, ctCheck, fmCheck})
            cb.setStyle("-fx-text-fill: #AECBEB; -fx-font-family: 'Segoe UI';");

        HBox roles = new HBox(14, ftCheck, ctCheck, fmCheck);

        ftEmpNum   = new TextField(); ftEmpNum.setPromptText("EMP001");
        ftSalary   = new TextField(); ftSalary.setPromptText("Monthly salary");
        ftHireDate = new TextField(); ftHireDate.setPromptText("YYYY-MM-DD");

        VBox ftBox = new VBox(8,
            UIFactory.sectionLabel("— Full-Time Driver Details —"),
            UIFactory.fieldGroup("EMPLOYEE NUMBER", ftEmpNum),
            UIFactory.fieldGroup("SALARY",          ftSalary),
            UIFactory.fieldGroup("HIRE DATE",       ftHireDate)
        );
        ftBox.setStyle("-fx-border-color: #1E3A5F; -fx-border-radius: 8;" +
                       "-fx-background-color: #0D1B2A55; -fx-background-radius: 8;");
        ftBox.setPadding(new Insets(12));
        ftBox.managedProperty().bind(ftCheck.selectedProperty());
        ftBox.visibleProperty().bind(ftCheck.selectedProperty());

        // FIX: field labels updated to reflect spec column names
        ctContractNum = new TextField(); ctContractNum.setPromptText("CON001");
        ctHourlyRate  = new TextField(); ctHourlyRate.setPromptText("Hourly rate");

        VBox ctBox = new VBox(8,
            UIFactory.sectionLabel("— Contract Driver Details —"),
            UIFactory.fieldGroup("CONTRACT NUMBER", ctContractNum),
            UIFactory.fieldGroup("HOURLY RATE",     ctHourlyRate)
        );
        ctBox.setStyle("-fx-border-color: #1E3A5F; -fx-border-radius: 8;" +
                       "-fx-background-color: #0D1B2A55; -fx-background-radius: 8;");
        ctBox.setPadding(new Insets(12));
        ctBox.managedProperty().bind(ctCheck.selectedProperty());
        ctBox.visibleProperty().bind(ctCheck.selectedProperty());

        fmLevel  = new TextField(); fmLevel.setPromptText("Junior / Senior");
        fmOffice = new TextField(); fmOffice.setPromptText("OFF-01");

        VBox fmBox = new VBox(8,
            UIFactory.sectionLabel("— Fleet Manager Details —"),
            UIFactory.fieldGroup("MANAGER LEVEL", fmLevel),
            UIFactory.fieldGroup("OFFICE NUMBER", fmOffice)
        );
        fmBox.setStyle("-fx-border-color: #1E3A5F; -fx-border-radius: 8;" +
                       "-fx-background-color: #0D1B2A55; -fx-background-radius: 8;");
        fmBox.setPadding(new Insets(12));
        fmBox.managedProperty().bind(fmCheck.selectedProperty());
        fmBox.visibleProperty().bind(fmCheck.selectedProperty());

        Button saveBtn = UIFactory.primaryBtn("+ Save Person & Roles", "#00C896");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> savePerson());

        form.getChildren().addAll(
            title,
            UIFactory.fieldGroup("FULL NAME",     nameField),
            UIFactory.fieldGroup("ADDRESS",       addressField),
            UIFactory.fieldGroup("PHONE",         phoneField),
            UIFactory.fieldGroup("DATE OF BIRTH", dobField),
            UIFactory.sectionLabel("ASSIGN ROLES"),
            roles, ftBox, ctBox, fmBox,
            saveBtn, feedback
        );

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #0D1B2A; -fx-background-color: #0D1B2A;");
        scroll.setPadding(new Insets(20, 20, 20, 0));
        return scroll;
    }

    private void loadPersons() {
        personTable.getItems().clear();
        String sql =
            "SELECT p.personID, p.fullName, p.phone, p.dateOfBirth, " +
            "  CONCAT_WS(', '," +
            "    IF(ftd.personID IS NOT NULL,'Full-Time Driver',NULL)," +
            "    IF(cd.personID  IS NOT NULL,'Contract Driver',NULL)," +
            "    IF(fm.personID  IS NOT NULL,'Fleet Manager',NULL)" +
            "  ) AS roles " +
            "FROM person p " +
            "LEFT JOIN fulltimedriver ftd ON p.personID = ftd.personID " +
            "LEFT JOIN contractdriver cd  ON p.personID = cd.personID " +
            "LEFT JOIN fleetmanager   fm  ON p.personID = fm.personID " +
            "ORDER BY p.personID";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 5; i++) row.add(rs.getString(i) == null ? "" : rs.getString(i));
                personTable.getItems().add(row);
            }
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void loadRoleDetails(int personID) {
        ftCheck.setSelected(false); ctCheck.setSelected(false); fmCheck.setSelected(false);
        ftEmpNum.clear(); ftSalary.clear(); ftHireDate.clear();
        ctContractNum.clear(); ctHourlyRate.clear();
        fmLevel.clear(); fmOffice.clear();

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT employeeNumber, salary, hiredate FROM fulltimedriver WHERE personID=?");
            ps.setInt(1, personID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ftCheck.setSelected(true);
                ftEmpNum.setText(rs.getString(1));
                ftSalary.setText(rs.getString(2));
                ftHireDate.setText(rs.getString(3) == null ? "" : rs.getString(3));
            }
            rs.close(); ps.close();

            // FIX: use renamed columns contractNumber and hourlyRate
            ps = conn.prepareStatement(
                    "SELECT contractNumber, hourlyRate FROM contractdriver WHERE personID=?");
            ps.setInt(1, personID);
            rs = ps.executeQuery();
            if (rs.next()) {
                ctCheck.setSelected(true);
                ctContractNum.setText(rs.getString(1));
                ctHourlyRate.setText(rs.getString(2));
            }
            rs.close(); ps.close();

            ps = conn.prepareStatement(
                    "SELECT managerLevel, officeNumber FROM fleetmanager WHERE personID=?");
            ps.setInt(1, personID);
            rs = ps.executeQuery();
            if (rs.next()) {
                fmCheck.setSelected(true);
                fmLevel.setText(rs.getString(1) == null ? "" : rs.getString(1));
                fmOffice.setText(rs.getString(2) == null ? "" : rs.getString(2));
            }
            rs.close(); ps.close();
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void savePerson() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { UIFactory.setError(feedback, "Full name required."); return; }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO person (fullName, address, phone, dateOfBirth) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
                ins.setString(1, name);
                ins.setString(2, addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());
                ins.setString(3, phoneField.getText().trim().isEmpty()   ? null : phoneField.getText().trim());
                ins.setString(4, dobField.getText().trim().isEmpty()     ? null : dobField.getText().trim());
                ins.executeUpdate();
                ResultSet keys = ins.getGeneratedKeys();
                keys.next();
                int pid = keys.getInt(1);

                if (ftCheck.isSelected()) {
                    if (ftEmpNum.getText().trim().isEmpty() || ftSalary.getText().trim().isEmpty()) {
                        UIFactory.setError(feedback, "Fill employee number & salary."); conn.rollback(); return;
                    }
                    PreparedStatement ftPs = conn.prepareStatement(
                        "INSERT INTO fulltimedriver (personID, employeeNumber, salary, hiredate) VALUES (?,?,?,?)");
                    ftPs.setInt(1, pid);
                    ftPs.setString(2, ftEmpNum.getText().trim());
                    ftPs.setDouble(3, Double.parseDouble(ftSalary.getText().trim()));
                    ftPs.setString(4, ftHireDate.getText().trim().isEmpty() ? null : ftHireDate.getText().trim());
                    ftPs.executeUpdate();
                }
                if (ctCheck.isSelected()) {
                    if (ctContractNum.getText().trim().isEmpty() || ctHourlyRate.getText().trim().isEmpty()) {
                        UIFactory.setError(feedback, "Fill contract number & hourly rate."); conn.rollback(); return;
                    }
                    // FIX: INSERT uses renamed columns contractNumber and hourlyRate
                    PreparedStatement ctPs = conn.prepareStatement(
                        "INSERT INTO contractdriver (personID, contractNumber, hourlyRate) VALUES (?,?,?)");
                    ctPs.setInt(1, pid);
                    ctPs.setString(2, ctContractNum.getText().trim());
                    ctPs.setDouble(3, Double.parseDouble(ctHourlyRate.getText().trim()));
                    ctPs.executeUpdate();
                }
                if (fmCheck.isSelected()) {
                    PreparedStatement fmPs = conn.prepareStatement(
                        "INSERT INTO fleetmanager (personID, managerLevel, officeNumber) VALUES (?,?,?)");
                    fmPs.setInt(1, pid);
                    fmPs.setString(2, fmLevel.getText().trim().isEmpty()  ? null : fmLevel.getText().trim());
                    fmPs.setString(3, fmOffice.getText().trim().isEmpty() ? null : fmOffice.getText().trim());
                    fmPs.executeUpdate();
                }

                conn.commit();
                UIFactory.setSuccess(feedback, "Person saved (ID " + pid + ").");
                nameField.clear(); addressField.clear(); phoneField.clear(); dobField.clear();
                ftCheck.setSelected(false); ctCheck.setSelected(false); fmCheck.setSelected(false);
                loadPersons();
            } catch (Exception ex) {
                conn.rollback();
                UIFactory.setError(feedback, ex.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void deletePerson() {
        ObservableList<String> sel = personTable.getSelectionModel().getSelectedItem();
        if (sel == null) { UIFactory.setError(feedback, "Select a person first."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete person \"" + sel.get(1) + "\"?\n\n" +
                "This will also remove all their delivery assignments,\n" +
                "role records (driver/manager), and supervision links.",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Person");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.YES) return;
            int pid = Integer.parseInt(sel.get(0));

            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    PreparedStatement s1 = conn.prepareStatement(
                        "DELETE FROM driversupervision WHERE supervisorID=? OR superviseeID=?");
                    s1.setInt(1, pid); s1.setInt(2, pid); s1.executeUpdate();

                    PreparedStatement s2 = conn.prepareStatement(
                        "DELETE FROM driverassignment WHERE personID=?");
                    s2.setInt(1, pid); s2.executeUpdate();

                    PreparedStatement s3 = conn.prepareStatement(
                        "DELETE FROM app_users WHERE personID=?");
                    s3.setInt(1, pid); s3.executeUpdate();

                    for (String tbl : new String[]{"fulltimedriver","contractdriver","fleetmanager"}) {
                        PreparedStatement sx = conn.prepareStatement(
                            "DELETE FROM " + tbl + " WHERE personID=?");
                        sx.setInt(1, pid); sx.executeUpdate();
                    }

                    PreparedStatement s5 = conn.prepareStatement(
                        "DELETE FROM person WHERE personID=?");
                    s5.setInt(1, pid); s5.executeUpdate();

                    conn.commit();
                    UIFactory.setSuccess(feedback, "Person \"" + sel.get(1) + "\" deleted successfully.");
                    loadPersons();

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