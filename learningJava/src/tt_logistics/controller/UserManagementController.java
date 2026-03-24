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

public class UserManagementController {

    private final MainApp app;
    private TableView<ObservableList<String>> userTable;
    private final Label feedback = UIFactory.feedbackLabel();

    private TextField     usernameField, fullNameField, phoneField, addressField, dobField;
    private PasswordField passwordField, confirmField;
    private ComboBox<String> roleCombo, personCombo;
    private CheckBox ftCheck, ctCheck, fmCheck;
    private TextField ftEmpNum, ftSalary, ftHireDate, ctContractNum, ctHourlyRate, fmLevel, fmOffice;

    public UserManagementController(MainApp app) { this.app = app; }

    public void show() {
        BorderPane root = UIFactory.darkRoot();
        root.setTop(UIFactory.topBar("User Management  —  Admin Only", "#E05C5C", app::showDashboard));

        userTable = new TableView<>();
        UIFactory.styleTable(userTable);
        String[] cols = {"ID","Username","Role","Full Name","Active"};
        for (int i = 0; i < cols.length; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(cols[i]);
            col.setCellValueFactory(d -> new SimpleStringProperty(
                    d.getValue().size() > idx ? d.getValue().get(idx) : ""));
            userTable.getColumns().add(col);
        }

        Button refreshBtn    = UIFactory.primaryBtn("⟳ Refresh", "#1E3A5F");
        Button deactivateBtn = UIFactory.dangerBtn("⊘ Deactivate");
        Button resetPassBtn  = UIFactory.primaryBtn("🔑 Reset Password", "#F5A623");
        refreshBtn.setOnAction(e    -> loadUsers());
        deactivateBtn.setOnAction(e -> deactivateUser());
        resetPassBtn.setOnAction(e  -> resetPassword());

        HBox tblActions = new HBox(10, refreshBtn, deactivateBtn, resetPassBtn);
        tblActions.setPadding(new Insets(0,0,10,0));

        VBox left = new VBox(10, tblActions, userTable);
        left.setPadding(new Insets(20));
        VBox.setVgrow(userTable, Priority.ALWAYS);

        ScrollPane formScroll = buildCreateForm();
        formScroll.setPrefWidth(360);

        SplitPane split = new SplitPane(left, formScroll);
        split.setDividerPositions(0.58);
        split.setStyle("-fx-background-color: #0D1B2A;");
        root.setCenter(split);

        app.getPrimaryStage().setScene(new Scene(root, 1100, 700));
        app.getPrimaryStage().setTitle("TT Logistics — User Management");
        loadUsers();
    }

    private ScrollPane buildCreateForm() {
        VBox form = UIFactory.card(24, 24);

        Label title = new Label("CREATE NEW USER");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill: #E05C5C;");

        usernameField = new TextField(); usernameField.setPromptText("Unique username");
        passwordField = new PasswordField(); passwordField.setPromptText("Initial password");
        confirmField  = new PasswordField(); confirmField.setPromptText("Confirm password");
        roleCombo = new ComboBox<>(FXCollections.observableArrayList("Admin","Driver","Manager"));
        roleCombo.setPromptText("Select role"); roleCombo.setMaxWidth(Double.MAX_VALUE);
        UIFactory.styleControl(roleCombo);

        fullNameField = new TextField(); fullNameField.setPromptText("First and last name");
        phoneField    = new TextField(); phoneField.setPromptText("Phone number");
        addressField  = new TextField(); addressField.setPromptText("Address");
        dobField      = new TextField(); dobField.setPromptText("YYYY-MM-DD");

        personCombo = new ComboBox<>();
        personCombo.setPromptText("Or link to existing person (optional)");
        personCombo.setMaxWidth(Double.MAX_VALUE);
        UIFactory.styleControl(personCombo);
        loadPersonCombo();

        ftCheck = new CheckBox("Full-Time Driver");
        ctCheck = new CheckBox("Contract Driver");
        fmCheck = new CheckBox("Fleet Manager");
        for (CheckBox cb : new CheckBox[]{ftCheck,ctCheck,fmCheck})
            cb.setStyle("-fx-text-fill: #AECBEB; -fx-font-family: 'Segoe UI';");
        HBox roleChecks = new HBox(12, ftCheck, ctCheck, fmCheck);

        ftEmpNum   = new TextField(); ftEmpNum.setPromptText("EMP001");
        ftSalary   = new TextField(); ftSalary.setPromptText("Monthly salary");
        ftHireDate = new TextField(); ftHireDate.setPromptText("YYYY-MM-DD");
        VBox ftBox = subForm("Full-Time Driver", "#00C896",
            UIFactory.fieldGroup("EMPLOYEE NUMBER", ftEmpNum),
            UIFactory.fieldGroup("SALARY (M)",      ftSalary),
            UIFactory.fieldGroup("HIRE DATE",       ftHireDate));
        ftBox.managedProperty().bind(ftCheck.selectedProperty());
        ftBox.visibleProperty().bind(ftCheck.selectedProperty());

        ctContractNum = new TextField(); ctContractNum.setPromptText("CON001");
        ctHourlyRate  = new TextField(); ctHourlyRate.setPromptText("Hourly rate");
        VBox ctBox = subForm("Contract Driver", "#F5A623",
            UIFactory.fieldGroup("CONTRACT NUMBER", ctContractNum),
            UIFactory.fieldGroup("HOURLY RATE",     ctHourlyRate));
        ctBox.managedProperty().bind(ctCheck.selectedProperty());
        ctBox.visibleProperty().bind(ctCheck.selectedProperty());

        fmLevel  = new TextField(); fmLevel.setPromptText("Junior / Senior");
        fmOffice = new TextField(); fmOffice.setPromptText("OFF-01");
        VBox fmBox = subForm("Fleet Manager", "#9B59B6",
            UIFactory.fieldGroup("MANAGER LEVEL", fmLevel),
            UIFactory.fieldGroup("OFFICE NUMBER", fmOffice));
        fmBox.managedProperty().bind(fmCheck.selectedProperty());
        fmBox.visibleProperty().bind(fmCheck.selectedProperty());

        Button createBtn = UIFactory.primaryBtn("✚ Create User", "#E05C5C");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setOnAction(e -> createUser());

        form.getChildren().addAll(
            title,
            UIFactory.sectionLabel("── LOGIN CREDENTIALS"),
            UIFactory.fieldGroup("USERNAME", usernameField),
            UIFactory.fieldGroup("ROLE",     roleCombo),
            rowOf(UIFactory.fieldGroup("PASSWORD", passwordField),
                  UIFactory.fieldGroup("CONFIRM",  confirmField)),
            UIFactory.sectionLabel("── PERSON DETAILS"),
            UIFactory.fieldGroup("FULL NAME", fullNameField),
            rowOf(UIFactory.fieldGroup("PHONE",   phoneField),
                  UIFactory.fieldGroup("DOB",     dobField)),
            UIFactory.fieldGroup("ADDRESS",   addressField),
            UIFactory.fieldGroup("LINK EXISTING PERSON", personCombo),
            UIFactory.sectionLabel("── ASSIGN ROLES  (optional)"),
            roleChecks, ftBox, ctBox, fmBox,
            createBtn, feedback
        );

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #0D1B2A; -fx-background-color: #0D1B2A;");
        scroll.setPadding(new Insets(20,20,20,0));
        return scroll;
    }

    private void createUser() {
        String username = usernameField.getText().trim();
        String pass     = passwordField.getText();
        String conf     = confirmField.getText();
        String role     = roleCombo.getValue();
        String fullName = fullNameField.getText().trim();

        if (username.isEmpty()) { UIFactory.setError(feedback, "Username required.");      return; }
        if (pass.isEmpty())     { UIFactory.setError(feedback, "Password required.");      return; }
        if (!pass.equals(conf)) { UIFactory.setError(feedback, "Passwords do not match."); return; }
        if (role == null)       { UIFactory.setError(feedback, "Select a role.");          return; }
        if (fullName.isEmpty()) { UIFactory.setError(feedback, "Full name required.");     return; }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int personID = -1;
                String existingSel = personCombo.getValue();
                if (existingSel != null && !existingSel.isBlank()) {
                    personID = Integer.parseInt(existingSel.split(" \\| ")[0].trim());
                } else {
                    PreparedStatement pp = conn.prepareStatement(
                        "INSERT INTO person (fullName, phone, address, dateOfBirth) VALUES (?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                    pp.setString(1, fullName);
                    pp.setString(2, phoneField.getText().trim().isEmpty()   ? null : phoneField.getText().trim());
                    pp.setString(3, addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());
                    pp.setString(4, dobField.getText().trim().isEmpty()     ? null : dobField.getText().trim());
                    pp.executeUpdate();
                    ResultSet k = pp.getGeneratedKeys(); k.next(); personID = k.getInt(1);
                }

                if (ftCheck.isSelected()) {
                    if (ftEmpNum.getText().trim().isEmpty() || ftSalary.getText().trim().isEmpty())
                    { UIFactory.setError(feedback,"Fill FT driver fields."); conn.rollback(); return; }
                    PreparedStatement fp = conn.prepareStatement(
                        "INSERT INTO fulltimedriver (personID,employeeNumber,salary,hiredate) VALUES (?,?,?,?)");
                    fp.setInt(1,personID); fp.setString(2,ftEmpNum.getText().trim());
                    fp.setDouble(3,Double.parseDouble(ftSalary.getText().trim()));
                    fp.setString(4,ftHireDate.getText().trim().isEmpty()?null:ftHireDate.getText().trim());
                    fp.executeUpdate();
                }
                if (ctCheck.isSelected()) {
                    if (ctContractNum.getText().trim().isEmpty() || ctHourlyRate.getText().trim().isEmpty())
                    { UIFactory.setError(feedback,"Fill contract driver fields."); conn.rollback(); return; }
                    PreparedStatement cp = conn.prepareStatement(
                        "INSERT INTO contractdriver (personID,employeeNumber,salary) VALUES (?,?,?)");
                    cp.setInt(1,personID); cp.setString(2,ctContractNum.getText().trim());
                    cp.setDouble(3,Double.parseDouble(ctHourlyRate.getText().trim()));
                    cp.executeUpdate();
                }
                if (fmCheck.isSelected()) {
                    PreparedStatement mp = conn.prepareStatement(
                        "INSERT INTO fleetmanager (personID,managerLevel,officeNumber) VALUES (?,?,?)");
                    mp.setInt(1,personID);
                    mp.setString(2,fmLevel.getText().trim().isEmpty()?null:fmLevel.getText().trim());
                    mp.setString(3,fmOffice.getText().trim().isEmpty()?null:fmOffice.getText().trim());
                    mp.executeUpdate();
                }

                PreparedStatement up = conn.prepareStatement(
                    "INSERT INTO app_users (username,password,role,personID) VALUES (?,?,?,?)");
                up.setString(1,username); up.setString(2,pass); up.setString(3,role);
                if (personID > 0) up.setInt(4, personID); else up.setNull(4, Types.INTEGER);
                up.executeUpdate();

                conn.commit();
                UIFactory.setSuccess(feedback, "User '" + username + "' created!  Password: " + pass);
                clearForm(); loadUsers();

            } catch (Exception ex) { conn.rollback(); UIFactory.setError(feedback, ex.getMessage()); }
            finally { conn.setAutoCommit(true); }
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void loadUsers() {
        userTable.getItems().clear();
        String sql = "SELECT u.userID, u.username, u.role, " +
                     "COALESCE(p.fullName,u.username), IF(u.isActive,'Yes','No') " +
                     "FROM app_users u LEFT JOIN person p ON u.personID=p.personID ORDER BY u.userID";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 5; i++) row.add(rs.getString(i)==null?"":rs.getString(i));
                userTable.getItems().add(row);
            }
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    private void deactivateUser() {
        ObservableList<String> sel = userTable.getSelectionModel().getSelectedItem();
        if (sel == null) { UIFactory.setError(feedback,"Select a user."); return; }
        new Alert(Alert.AlertType.CONFIRMATION, "Deactivate '"+sel.get(1)+"'?",
                ButtonType.YES, ButtonType.NO).showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try (PreparedStatement ps = DatabaseConnection.getConnection()
                        .prepareStatement("UPDATE app_users SET isActive=FALSE WHERE userID=?")) {
                    ps.setInt(1, Integer.parseInt(sel.get(0)));
                    ps.executeUpdate(); UIFactory.setSuccess(feedback,"User deactivated."); loadUsers();
                } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
            }
        });
    }

    private void resetPassword() {
        ObservableList<String> sel = userTable.getSelectionModel().getSelectedItem();
        if (sel == null) { UIFactory.setError(feedback,"Select a user."); return; }
        TextInputDialog dlg = new TextInputDialog("newpass123");
        dlg.setTitle("Reset Password"); dlg.setHeaderText("New password for: " + sel.get(1));
        dlg.showAndWait().ifPresent(np -> {
            if (np.isBlank()) return;
            try (PreparedStatement ps = DatabaseConnection.getConnection()
                    .prepareStatement("UPDATE app_users SET password=? WHERE userID=?")) {
                ps.setString(1, np); ps.setInt(2, Integer.parseInt(sel.get(0)));
                ps.executeUpdate(); UIFactory.setSuccess(feedback, "Password reset → " + np);
            } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
        });
    }

    private void loadPersonCombo() {
        personCombo.getItems().add("");
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery("SELECT personID,fullName FROM person ORDER BY fullName")) {
            while (rs.next())
                personCombo.getItems().add(rs.getString(1)+" | "+rs.getString(2));
        } catch (SQLException ignored) {}
    }

    private void clearForm() {
        usernameField.clear(); passwordField.clear(); confirmField.clear();
        roleCombo.setValue(null); fullNameField.clear(); phoneField.clear();
        addressField.clear(); dobField.clear(); personCombo.setValue(null);
        ftCheck.setSelected(false); ctCheck.setSelected(false); fmCheck.setSelected(false);
        ftEmpNum.clear(); ftSalary.clear(); ftHireDate.clear();
        ctContractNum.clear(); ctHourlyRate.clear(); fmLevel.clear(); fmOffice.clear();
    }

    private VBox subForm(String title, String color, javafx.scene.Node... nodes) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-border-color:"+color+"44;-fx-border-radius:8;" +
                     "-fx-background-color:#0D1B2A55;-fx-background-radius:8;");
        Label lbl = new Label("— "+title+" —");
        lbl.setStyle("-fx-text-fill:"+color+";-fx-font-weight:bold;-fx-font-family:'Segoe UI';-fx-font-size:11;");
        box.getChildren().add(lbl);
        box.getChildren().addAll(nodes);
        return box;
    }

    private HBox rowOf(javafx.scene.Node... nodes) {
        HBox h = new HBox(12);
        for (javafx.scene.Node n : nodes) { HBox.setHgrow(n,Priority.ALWAYS); h.getChildren().add(n); }
        return h;
    }
}
