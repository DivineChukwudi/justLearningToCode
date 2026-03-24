package tt_logistics;

import tt_logistics.db.DatabaseConnection;
import tt_logistics.model.AppUser;
import tt_logistics.model.Session;
import tt_logistics.controller.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.*;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("TT Logistics — Fleet Management System");
        stage.setMinWidth(480);
        stage.setMinHeight(560);
        showLoginScreen();
        stage.show();
    }

    // ─── LOGIN ─────────────────────────────────────────────────────────────────
    public void showLoginScreen() {
        Session.logout();
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0D1B2A;");

        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(48, 52, 48, 52));
        card.setMaxWidth(420);
        card.setStyle("-fx-background-color: #162032; -fx-background-radius: 16;" +
                      "-fx-border-color: #1E3A5F; -fx-border-width: 1; -fx-border-radius: 16;");
        card.setEffect(new DropShadow(40, Color.color(0, 0.4, 0.9, 0.25)));

        Label logo = new Label("TT");
        logo.setFont(Font.font("Courier New", FontWeight.BOLD, 52));
        logo.setStyle("-fx-text-fill: #1E90FF;");

        Label title = new Label("LOGISTICS");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: #AECBEB;");

        Label subtitle = new Label("Fleet Management System");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setStyle("-fx-text-fill: #4A6FA5;");

        HBox roleBadges = new HBox(8);
        roleBadges.setAlignment(Pos.CENTER);
        for (String[] b : new String[][]{{"Admin","#1E90FF"},{"Driver","#00C896"},{"Manager","#F5A623"}}) {
            Label l = new Label(b[0]);
            l.setStyle("-fx-background-color:"+b[1]+"22;-fx-text-fill:"+b[1]+";" +
                       "-fx-background-radius:20;-fx-padding:3 12 3 12;" +
                       "-fx-font-family:'Segoe UI';-fx-font-size:11;-fx-font-weight:bold;");
            roleBadges.getChildren().add(l);
        }

        Label uLbl = mkLbl("USERNAME"); TextField userField = new TextField();
        userField.setPromptText("Enter username"); styleField(userField);
        Label pLbl = mkLbl("PASSWORD"); PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password"); styleField(passField);

        Label errorLbl = new Label(""); errorLbl.setStyle("-fx-text-fill:#FF6B6B;-fx-font-size:12;");
        errorLbl.setWrapText(true);

        Button loginBtn = new Button("LOGIN  →"); loginBtn.setMaxWidth(Double.MAX_VALUE);
        String bb="-fx-background-color:#1E90FF;-fx-text-fill:white;-fx-font-family:'Segoe UI';" +
                  "-fx-font-weight:bold;-fx-font-size:13;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:12 0 12 0;";
        String bh="-fx-background-color:#1070CC;-fx-text-fill:white;-fx-font-family:'Segoe UI';" +
                  "-fx-font-weight:bold;-fx-font-size:13;-fx-background-radius:8;-fx-cursor:hand;-fx-padding:12 0 12 0;";
        loginBtn.setStyle(bb);
        loginBtn.setOnMouseEntered(e->loginBtn.setStyle(bh)); loginBtn.setOnMouseExited(e->loginBtn.setStyle(bb));

        Label hint = new Label("Enter your credentials to access the dashboard.");
        hint.setFont(Font.font("Segoe UI",10)); hint.setStyle("-fx-text-fill:#2E4A6E;");

        Runnable doLogin = () -> {
            String u=userField.getText().trim(), p=passField.getText();
            if (u.isEmpty()) { errorLbl.setText("⚠  Username cannot be empty."); return; }
            try {
                AppUser au = authenticate(u,p);
                if (au==null) { errorLbl.setText("⚠  Invalid username or password."); return; }
                Session.login(au); showDashboard();
            } catch (Exception ex) { errorLbl.setText("⚠  "+ex.getMessage().split("\n")[0]); }
        };
        loginBtn.setOnAction(e->doLogin.run()); passField.setOnAction(e->doLogin.run());

        card.getChildren().addAll(logo,title,subtitle,roleBadges,new Separator(),
                uLbl,userField,pLbl,passField,errorLbl,loginBtn,hint);
        StackPane centre = new StackPane(card); centre.setPadding(new Insets(40));
        root.setCenter(centre);
        primaryStage.setScene(new Scene(root,520,640));
        primaryStage.setTitle("TT Logistics — Login");
    }

    // ─── AUTHENTICATE ──────────────────────────────────────────────────────────
    private AppUser authenticate(String username, String password) throws SQLException {
        String sql =
            "SELECT u.userID,u.username,u.role,COALESCE(u.personID,-1)," +
            "COALESCE(p.fullName,u.username),u.nickname,u.avatarColor " +
            "FROM app_users u LEFT JOIN person p ON u.personID=p.personID " +
            "WHERE u.username=? AND u.password=? AND u.isActive=TRUE";
        try (PreparedStatement ps=DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1,username); ps.setString(2,password);
            ResultSet rs=ps.executeQuery();
            if (rs.next()) return new AppUser(
                rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),
                rs.getString(5),rs.getString(6),rs.getString(7));
        }
        return null;
    }

    // ─── DASHBOARD ─────────────────────────────────────────────────────────────
    public void showDashboard() {
        AppUser user = Session.get();
        String rc = roleColor(user.getRole());

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0D1B2A;");

        // ── Top bar ──────────────────────────────────────────────────────────
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 24, 12, 24));
        topBar.setStyle("-fx-background-color:#162032;-fx-border-color:#1E3A5F;-fx-border-width:0 0 1 0;");

        Label appTitle = new Label("TT LOGISTICS");
        appTitle.setFont(Font.font("Courier New",FontWeight.BOLD,20));
        appTitle.setStyle("-fx-text-fill:#1E90FF;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        // Profile avatar button
        Label avatarBtn = new Label(user.getInitials());
        avatarBtn.setStyle(
            "-fx-background-color:"+user.getAvatarColor()+"33;" +
            "-fx-text-fill:"+user.getAvatarColor()+";" +
            "-fx-background-radius:20;-fx-border-color:"+user.getAvatarColor()+";" +
            "-fx-border-radius:20;-fx-border-width:2;" +
            "-fx-min-width:40;-fx-min-height:40;-fx-max-width:40;-fx-max-height:40;" +
            "-fx-font-weight:bold;-fx-font-family:'Segoe UI';-fx-cursor:hand;");
        avatarBtn.setAlignment(Pos.CENTER);
        Tooltip.install(avatarBtn, new Tooltip("My Profile — " + user.getFullName()));

        Label nameLbl = new Label(user.getDisplayName());
        nameLbl.setStyle("-fx-text-fill:#AECBEB;-fx-font-size:13;-fx-font-family:'Segoe UI';");

        Label roleLbl = new Label("  "+user.getRole().toUpperCase()+"  ");
        roleLbl.setStyle("-fx-background-color:"+rc+"22;-fx-text-fill:"+rc+";" +
            "-fx-background-radius:20;-fx-padding:4 12 4 12;" +
            "-fx-font-family:'Segoe UI';-fx-font-size:11;-fx-font-weight:bold;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:#4A6FA5;" +
            "-fx-border-color:#1E3A5F;-fx-border-radius:6;-fx-background-radius:6;" +
            "-fx-cursor:hand;-fx-padding:6 16 6 16;");
        logoutBtn.setOnAction(e -> { Session.logout(); showLoginScreen(); });

        avatarBtn.setOnMouseClicked(e -> new ProfileController(this).show());

        topBar.getChildren().addAll(appTitle, sp, avatarBtn, nameLbl, roleLbl, logoutBtn);

        // ── Welcome ───────────────────────────────────────────────────────────
        VBox welcomeBox = new VBox(4);
        welcomeBox.setPadding(new Insets(24, 40, 10, 40));
        Label wl = new Label("Welcome back, " + user.getDisplayName() + ".");
        wl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        wl.setStyle("-fx-text-fill:#AECBEB;");
        Label al = new Label(accessDesc(user.getRole()));
        al.setStyle("-fx-text-fill:#4A6FA5;-fx-font-family:'Segoe UI';-fx-font-size:13;");
        welcomeBox.getChildren().addAll(wl, al);

        // ── Menu grid ─────────────────────────────────────────────────────────
        GridPane grid = buildMenuGrid(user);
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#0D1B2A;-fx-background-color:#0D1B2A;");

        VBox centre = new VBox(welcomeBox, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.setTop(topBar); root.setCenter(centre);
        primaryStage.setScene(new Scene(root,920,680));
        primaryStage.setTitle("TT Logistics — "+user.getRole()+" Dashboard");
    }

    private GridPane buildMenuGrid(AppUser user) {
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(20);
        grid.setPadding(new Insets(20, 40, 40, 40));
        grid.setAlignment(Pos.TOP_LEFT);

        // {icon, label, color, desc, roles}
        Object[][] items = {
            {"🚗","Vehicle Management",  "#1E90FF","Add, edit and delete fleet vehicles",       new String[]{"Admin","Manager"}},
            {"👤","Driver Management",   "#00C896","Manage full-time, contract drivers",        new String[]{"Admin","Manager"}},
            {"📦","Trip Management",     "#F5A623","Deliveries, assignments & status updates",  new String[]{"Admin","Manager","Driver"}},
            {"🔧","Maintenance",         "#E05C5C","Log and review vehicle maintenance",        new String[]{"Admin","Manager"}},
            {"📊","Reports",             "#9B59B6","Views, functions and delivery log",         new String[]{"Admin","Manager"}},
            {"👥","Client Management",   "#1ABC9C","Manage client records",                     new String[]{"Admin"}},
            {"🔑","User Management",     "#E05C5C","Create and manage user logins",            new String[]{"Admin"}},
            {"👤","My Profile",          "#4A6FA5","Edit your personal details & avatar",       new String[]{"Admin","Driver","Manager"}},
        };

        int col=0, row=0;
        for (Object[] item : items) {
            String[] roles = (String[]) item[4];
            boolean ok=false;
            for (String r:roles) if (r.equals(user.getRole())) { ok=true; break; }
            if (!ok) continue;
            VBox card = buildCard((String)item[0],(String)item[1],(String)item[2],(String)item[3]);
            final String screen=(String)item[1];
            card.setOnMouseClicked(e->openScreen(screen));
            grid.add(card,col,row);
            col++; if (col==3) { col=0; row++; }
        }
        return grid;
    }

    private VBox buildCard(String icon, String label, String accent, String desc) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(24)); card.setPrefWidth(240); card.setPrefHeight(130);
        String base="-fx-background-color:#162032;-fx-background-radius:12;" +
                    "-fx-border-color:#1E3A5F;-fx-border-width:1;-fx-border-radius:12;-fx-cursor:hand;";
        String hover="-fx-background-color:#1A2B42;-fx-background-radius:12;" +
                     "-fx-border-color:"+accent+";-fx-border-width:1;-fx-border-radius:12;-fx-cursor:hand;";
        card.setStyle(base);
        card.setOnMouseEntered(e->card.setStyle(hover)); card.setOnMouseExited(e->card.setStyle(base));
        Label il=new Label(icon); il.setFont(Font.font(28));
        Label nl=new Label(label); nl.setFont(Font.font("Segoe UI",FontWeight.BOLD,15));
        nl.setStyle("-fx-text-fill:"+accent+";");
        Label dl=new Label(desc); dl.setFont(Font.font("Segoe UI",11));
        dl.setStyle("-fx-text-fill:#4A6FA5;"); dl.setWrapText(true);
        card.getChildren().addAll(il,nl,dl);
        return card;
    }

    private void openScreen(String screen) {
        switch (screen) {
            case "Vehicle Management" -> new VehicleController(this).show();
            case "Driver Management"  -> new DriverController(this).show();
            case "Trip Management"    -> new TripController(this).show();
            case "Maintenance"        -> new MaintenanceController(this).show();
            case "Reports"            -> new ReportsController(this).show();
            case "Client Management"  -> new ClientController(this).show();
            case "User Management"    -> new UserManagementController(this).show();
            case "My Profile"         -> new ProfileController(this).show();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String roleColor(String r) {
        return switch(r) { case "Admin"->"#1E90FF"; case "Driver"->"#00C896"; case "Manager"->"#F5A623"; default->"#4A6FA5"; };
    }
    private String accessDesc(String r) {
        return switch(r) {
            case "Admin"   -> "Full access — all modules available.";
            case "Driver"  -> "You can view and update your assigned deliveries.";
            case "Manager" -> "You can manage vehicles, drivers, maintenance and view reports.";
            default        -> "";
        };
    }
    private Label mkLbl(String text) {
        Label l=new Label(text); l.setFont(Font.font("Segoe UI",FontWeight.BOLD,10));
        l.setStyle("-fx-text-fill:#4A6FA5;"); return l;
    }
    private void styleField(TextInputControl f) {
        f.setStyle("-fx-background-color:#0D1B2A;-fx-text-fill:#AECBEB;" +
                   "-fx-prompt-text-fill:#2E4A6E;-fx-border-color:#1E3A5F;" +
                   "-fx-border-radius:8;-fx-background-radius:8;-fx-padding:10 14 10 14;-fx-font-size:13;");
    }
    public Stage getPrimaryStage() { return primaryStage; }
    public static void main(String[] args) { launch(args); }
}
