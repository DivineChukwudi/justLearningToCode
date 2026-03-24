package tt_logistics.controller;

import tt_logistics.MainApp;
import tt_logistics.db.DatabaseConnection;
import tt_logistics.model.AppUser;
import tt_logistics.model.Session;
import tt_logistics.util.UIFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.*;

public class ProfileController {

    private final MainApp app;
    private final Label feedback = UIFactory.feedbackLabel();

    public ProfileController(MainApp app) { this.app = app; }

    public void show() {
        AppUser user = Session.get();

        BorderPane root = UIFactory.darkRoot();
        root.setTop(UIFactory.topBar("My Profile", user.getAvatarColor(), app::showDashboard));

        ScrollPane scroll = new ScrollPane(buildContent(user));
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #0D1B2A; -fx-background-color: #0D1B2A;");

        root.setCenter(scroll);
        app.getPrimaryStage().setScene(new Scene(root, 860, 660));
        app.getPrimaryStage().setTitle("TT Logistics — My Profile");
    }

    private VBox buildContent(AppUser user) {
        VBox outer = new VBox(24);
        outer.setPadding(new Insets(32));

        // ── Avatar + name header ──────────────────────────────────────────────
        HBox header = new HBox(24);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(24));
        header.setStyle("-fx-background-color: #162032; -fx-background-radius: 14;" +
                        "-fx-border-color: #1E3A5F; -fx-border-width: 1; -fx-border-radius: 14;");

        // Avatar circle (just styled label)
        Label avatar = makeAvatar(user);

        VBox nameBox = new VBox(4);
        Label displayName = new Label(user.getFullName());
        displayName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        displayName.setStyle("-fx-text-fill: #AECBEB;");

        Label nickLbl = new Label(user.getNickname().isBlank() ? "@" + user.getUsername() : "\"" + user.getNickname() + "\"");
        nickLbl.setStyle("-fx-text-fill: #4A6FA5; -fx-font-family: 'Segoe UI'; -fx-font-size: 13;");

        String roleColor = roleColor(user.getRole());
        Label roleBadge = new Label("  " + user.getRole().toUpperCase() + "  ");
        roleBadge.setStyle(
            "-fx-background-color: " + roleColor + "22; -fx-text-fill: " + roleColor + ";" +
            "-fx-background-radius: 20; -fx-padding: 4 14 4 14;" +
            "-fx-font-family: 'Segoe UI'; -fx-font-size: 12; -fx-font-weight: bold;"
        );
        nameBox.getChildren().addAll(displayName, nickLbl, roleBadge);

        Region hSpacer = new Region(); HBox.setHgrow(hSpacer, Priority.ALWAYS);

        // Colour picker for avatar
        VBox colorBox = new VBox(8);
        colorBox.setAlignment(Pos.CENTER);
        Label colorLbl = new Label("AVATAR COLOUR");
        colorLbl.setStyle("-fx-text-fill: #4A6FA5; -fx-font-size: 10; -fx-font-weight: bold;");
        HBox colorRow = new HBox(8);
        colorRow.setAlignment(Pos.CENTER);
        String[] colors = {"#1E90FF","#00C896","#F5A623","#E05C5C","#9B59B6","#1ABC9C","#E67E22","#E91E8C"};
        for (String c : colors) {
            Label dot = new Label("  ");
            dot.setStyle("-fx-background-color: " + c + "; -fx-background-radius: 20;" +
                         "-fx-min-width: 24; -fx-min-height: 24; -fx-cursor: hand;" +
                         (c.equals(user.getAvatarColor()) ? "-fx-border-color: white; -fx-border-radius: 20; -fx-border-width: 2;" : ""));
            dot.setOnMouseClicked(e -> {
                user.setAvatarColor(c);
                avatar.setStyle(avatarStyle(c));
                // update dot borders
                colorRow.getChildren().forEach(n -> {
                    Label d = (Label) n;
                    String dc = (String) d.getUserData();
                    d.setStyle("-fx-background-color: " + dc + "; -fx-background-radius: 20;" +
                               "-fx-min-width: 24; -fx-min-height: 24; -fx-cursor: hand;" +
                               (dc.equals(c) ? "-fx-border-color: white; -fx-border-radius: 20; -fx-border-width: 2;" : ""));
                });
            });
            dot.setUserData(c);
            colorRow.getChildren().add(dot);
        }
        colorBox.getChildren().addAll(colorLbl, colorRow);

        header.getChildren().addAll(avatar, nameBox, hSpacer, colorBox);

        // ── Editable personal details ─────────────────────────────────────────
        VBox editCard = UIFactory.card(28, 24);

        Label editTitle = new Label("PERSONAL DETAILS  —  You can edit these");
        editTitle.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        editTitle.setStyle("-fx-text-fill: #00C896;");

        TextField fullNameField = new TextField(user.getFullName());
        TextField nicknameField = new TextField(user.getNickname());
        nicknameField.setPromptText("How you want to be called");

        // Load phone + address from person table
        String[] personData = loadPersonData(user.getPersonID());
        TextField phoneField   = new TextField(personData[0]);
        TextField addressField = new TextField(personData[1]);

        PasswordField newPassField    = new PasswordField(); newPassField.setPromptText("Leave blank to keep current");
        PasswordField confirmPassField= new PasswordField(); confirmPassField.setPromptText("Confirm new password");

        Button saveBtn = UIFactory.primaryBtn("Save Changes", "#00C896");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> savePersonalDetails(
            user, fullNameField, nicknameField, phoneField, addressField,
            newPassField, confirmPassField, displayName, nickLbl, avatar
        ));

        editCard.getChildren().addAll(
            editTitle,
            row(
                UIFactory.fieldGroup("FULL NAME",  fullNameField),
                UIFactory.fieldGroup("NICKNAME",   nicknameField)
            ),
            row(
                UIFactory.fieldGroup("PHONE",    phoneField),
                UIFactory.fieldGroup("ADDRESS",  addressField)
            ),
            new Separator(),
            UIFactory.sectionLabel("CHANGE PASSWORD"),
            row(
                UIFactory.fieldGroup("NEW PASSWORD",     newPassField),
                UIFactory.fieldGroup("CONFIRM PASSWORD", confirmPassField)
            ),
            saveBtn, feedback
        );

        // ── Read-only sensitive section ───────────────────────────────────────
        VBox sensitiveCard = UIFactory.card(28, 24);
        Label sensTitle = new Label("ROLE DETAILS  —  Contact admin to change these");
        sensTitle.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        sensTitle.setStyle("-fx-text-fill: #E05C5C;");

        VBox sensitiveContent = loadSensitiveDetails(user);
        sensitiveCard.getChildren().addAll(sensTitle, sensitiveContent);

        outer.getChildren().addAll(header, editCard, sensitiveCard);
        return outer;
    }

    // ── Save editable fields ──────────────────────────────────────────────────
    private void savePersonalDetails(AppUser user, TextField fullNameF, TextField nicknameF,
            TextField phoneF, TextField addressF,
            PasswordField newPassF, PasswordField confirmPassF,
            Label displayName, Label nickLbl, Label avatar) {

        String name     = fullNameF.getText().trim();
        String nickname = nicknameF.getText().trim();
        String phone    = phoneF.getText().trim();
        String address  = addressF.getText().trim();
        String newPass  = newPassF.getText();
        String confPass = confirmPassF.getText();

        if (name.isEmpty()) { UIFactory.setError(feedback, "Full name cannot be empty."); return; }
        if (!newPass.isEmpty() && !newPass.equals(confPass)) {
            UIFactory.setError(feedback, "Passwords do not match."); return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update app_users
                String sql = newPass.isEmpty()
                    ? "UPDATE app_users SET nickname=?, avatarColor=? WHERE userID=?"
                    : "UPDATE app_users SET nickname=?, avatarColor=?, password=? WHERE userID=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nickname.isEmpty() ? null : nickname);
                ps.setString(2, user.getAvatarColor());
                if (!newPass.isEmpty()) { ps.setString(3, newPass); ps.setInt(4, user.getUserID()); }
                else                   { ps.setInt(3, user.getUserID()); }
                ps.executeUpdate();

                // Update person table if linked
                if (user.getPersonID() > 0) {
                    PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE person SET fullName=?, phone=?, address=? WHERE personID=?");
                    ps2.setString(1, name);
                    ps2.setString(2, phone.isEmpty()   ? null : phone);
                    ps2.setString(3, address.isEmpty() ? null : address);
                    ps2.setInt(4, user.getPersonID());
                    ps2.executeUpdate();
                }

                conn.commit();

                // Update session object
                user.setFullName(name);
                user.setNickname(nickname);

                // Refresh header labels live
                displayName.setText(name);
                nickLbl.setText(nickname.isBlank() ? "@" + user.getUsername() : "\"" + nickname + "\"");
                avatar.setText(user.getInitials());

                UIFactory.setSuccess(feedback, "Profile updated successfully.");
                newPassF.clear(); confirmPassF.clear();

            } catch (Exception ex) { conn.rollback(); UIFactory.setError(feedback, ex.getMessage()); }
            finally { conn.setAutoCommit(true); }
        } catch (SQLException e) { UIFactory.setError(feedback, e.getMessage()); }
    }

    // ── Load phone/address from person table ──────────────────────────────────
    private String[] loadPersonData(int personID) {
        if (personID <= 0) return new String[]{"", ""};
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT phone, address FROM person WHERE personID=?")) {
            ps.setInt(1, personID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new String[]{
                rs.getString(1) == null ? "" : rs.getString(1),
                rs.getString(2) == null ? "" : rs.getString(2)
            };
        } catch (SQLException ignored) {}
        return new String[]{"", ""};
    }

    // ── Load read-only sensitive details based on role ────────────────────────
    private VBox loadSensitiveDetails(AppUser user) {
        VBox box = new VBox(10);
        if (user.getPersonID() <= 0) {
            box.getChildren().add(readonlyField("No linked person record", "—"));
            return box;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Full-time driver details
            PreparedStatement ps = conn.prepareStatement(
                "SELECT employeeNumber, salary, hiredate FROM fulltimedriver WHERE personID=?");
            ps.setInt(1, user.getPersonID());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                box.getChildren().addAll(
                    new Label("Full-Time Driver") {{
                        setStyle("-fx-text-fill: #00C896; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';"); }},
                    row(readonlyField("Employee No.",  rs.getString(1)),
                        readonlyField("Salary (M)",    rs.getString(2)),
                        readonlyField("Hire Date",     rs.getString(3) == null ? "—" : rs.getString(3)))
                );
            }
            rs.close(); ps.close();

            // Contract driver details
            ps = conn.prepareStatement(
                "SELECT employeeNumber, salary FROM contractdriver WHERE personID=?");
            ps.setInt(1, user.getPersonID());
            rs = ps.executeQuery();
            if (rs.next()) {
                box.getChildren().addAll(
                    new Label("Contract Driver") {{
                        setStyle("-fx-text-fill: #F5A623; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';"); }},
                    row(readonlyField("Contract No.", rs.getString(1)),
                        readonlyField("Hourly Rate",  rs.getString(2)))
                );
            }
            rs.close(); ps.close();

            // Fleet manager
            ps = conn.prepareStatement(
                "SELECT managerLevel, officeNumber FROM fleetmanager WHERE personID=?");
            ps.setInt(1, user.getPersonID());
            rs = ps.executeQuery();
            if (rs.next()) {
                box.getChildren().addAll(
                    new Label("Fleet Manager") {{
                        setStyle("-fx-text-fill: #9B59B6; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';"); }},
                    row(readonlyField("Manager Level", rs.getString(1) == null ? "—" : rs.getString(1)),
                        readonlyField("Office",        rs.getString(2) == null ? "—" : rs.getString(2)))
                );
            }
            rs.close(); ps.close();

        } catch (SQLException e) { box.getChildren().add(new Label("Error: " + e.getMessage())); }

        if (box.getChildren().isEmpty())
            box.getChildren().add(readonlyField("No role details found", "—"));
        return box;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private HBox row(javafx.scene.Node... nodes) {
        HBox h = new HBox(14);
        for (javafx.scene.Node n : nodes) {
            HBox.setHgrow(n, Priority.ALWAYS);
            h.getChildren().add(n);
        }
        return h;
    }

    private VBox readonlyField(String label, String value) {
        Label lbl = new Label(label.toUpperCase());
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        lbl.setStyle("-fx-text-fill: #4A6FA5;");
        Label val = new Label(value == null ? "—" : value);
        val.setStyle("-fx-text-fill: #5A7A9A; -fx-font-family: 'Segoe UI'; -fx-font-size: 13;" +
                     "-fx-background-color: #0D1B2A; -fx-background-radius: 8;" +
                     "-fx-border-color: #1E2F42; -fx-border-radius: 8; -fx-padding: 8 12 8 12;");
        val.setMaxWidth(Double.MAX_VALUE);
        return new VBox(5, lbl, val);
    }

    private Label makeAvatar(AppUser user) {
        Label av = new Label(user.getInitials());
        av.setStyle(avatarStyle(user.getAvatarColor()));
        av.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        av.setMinSize(72, 72);
        av.setMaxSize(72, 72);
        av.setAlignment(Pos.CENTER);
        return av;
    }

    private String avatarStyle(String color) {
        return "-fx-background-color: " + color + "33;" +
               "-fx-text-fill: " + color + ";" +
               "-fx-background-radius: 36;" +
               "-fx-border-color: " + color + ";" +
               "-fx-border-radius: 36; -fx-border-width: 2;";
    }

    private String roleColor(String role) {
        return switch (role) {
            case "Admin"   -> "#1E90FF";
            case "Driver"  -> "#00C896";
            case "Manager" -> "#F5A623";
            default        -> "#4A6FA5";
        };
    }
}
