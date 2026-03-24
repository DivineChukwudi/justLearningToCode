package restaurantSystem.controllers;

import javafx.animation.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Duration;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class MainPOSController {

    private static final String[] DINNER_ITEMS = {
        "Beef Tenderloin", "Grilled Salmon", "Rack of Lamb",
        "Chicken Supreme", "Vegetable Risotto"
    };
    private static final double[] DINNER_PRICES = { 18.50, 16.00, 22.00, 14.50, 12.00 };

    private static final String[] DESSERT_ITEMS = {
        "Crème Brûlée", "Chocolate Fondant", "Tiramisu",
        "Cheesecake",   "Fruit Sorbet"
    };
    private static final double[] DESSERT_PRICES = { 7.50, 8.00, 7.00, 6.50, 5.50 };

    private static final String[] DRINK_ITEMS = {
        "-- Select Drink --",
        "House Red Wine",    "House White Wine",  "Sparkling Water",
        "Fresh Orange Juice","Espresso",          "Cappuccino",
        "Iced Tea",          "Lemonade",          "Craft Beer"
    };
    private static final double[] DRINK_PRICES = {
        0, 9.00, 9.00, 3.50, 4.50, 4.00, 4.50, 4.00, 3.50, 6.00
    };

    // Image file names — must exactly match filenames in extras/ (without .png)
    private static final String[] DINNER_IMAGES = {
        "beefTenderloin", "grilledSalmon", "rackOfLamb",
        "chickenSupreme", "vegetableRisotto"
    };
    private static final String[] DESSERT_IMAGES = {
        "cremeBrulee", "chocolateFondant", "tiramisu",
        "cheeseCake",  "fruitSorbet"
    };

    private Stage stage;

    private CheckBox[]               dinnerBoxes;
    private RadioButton[]            dessertBtns;
    private ComboBox<String>         drinksCombo;
    private Spinner<Integer>         drinkQtySpinner;
    private TextField                cashField;
    private Label                    totalLabel;
    private Label                    changeLabel;
    private Label                    clockLabel;
    private TableView<OrderRow>      orderTable;
    private ObservableList<OrderRow> orderRows = FXCollections.observableArrayList();

    private double runningTotal = 0.0;

    public MainPOSController() {}

    public MainPOSController(Stage stage) {
        this.stage = stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void onSceneReady() {
        startClock();
        if (stage != null && stage.getScene() != null) {
            Node root = stage.getScene().getRoot();
            root.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(600), root);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

    public void show() {
        stage.setTitle("La Etern – Point of Sale");
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1A1A1A;");
        root.setTop(buildHeader());
        root.setCenter(buildCenter());
        root.setBottom(buildFooter());

        Scene scene = new Scene(root);
        applyStylesheet(scene);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();

        onSceneReady();
    }

    private void applyStylesheet(Scene scene) {
        java.net.URL url = getClass().getResource("/css/styles.css");
        if (url == null) url = getClass().getClassLoader().getResource("css/styles.css");
        if (url != null) scene.getStylesheets().add(url.toExternalForm());
    }

   
    private Node buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 30, 0, 30));
        header.setPrefHeight(64);
        header.setStyle(
            "-fx-background-color: #C9A84C;" +
            "-fx-border-color: #A07830;" +
            "-fx-border-width: 0 0 2 0;"
        );

        VBox titleBox = new VBox(1);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("La Etern");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#1A1A1A"));
        Label sub = new Label("Exclusive Eatery  •  Point of Sale");
        sub.setFont(Font.font("Georgia", FontPosture.ITALIC, 11));
        sub.setTextFill(Color.web("#3A2800"));
        titleBox.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        clockLabel = new Label();
        clockLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        clockLabel.setTextFill(Color.web("#1A1A1A"));

        header.getChildren().addAll(buildSmallLogo(), new Label("  "), titleBox, spacer, clockLabel);
        return header;
    }

    private StackPane buildSmallLogo() {
        StackPane pane = new StackPane();
        pane.setPrefSize(48, 48);
        java.net.URL imageUrl = getClass().getResource("../extras/posLogo.png");
        if (imageUrl != null) {
            ImageView logoImage = new ImageView(new Image(imageUrl.toExternalForm()));
            logoImage.setFitWidth(48);
            logoImage.setFitHeight(48);
            logoImage.setPreserveRatio(true);
            pane.getChildren().add(logoImage);
        } else {
            Circle c1 = new Circle(22); c1.setFill(Color.web("#1A1A1A", 0.15));
            Circle c2 = new Circle(22); c2.setFill(Color.TRANSPARENT);
            c2.setStroke(Color.web("#1A1A1A", 0.5)); c2.setStrokeWidth(1.5);
            Label lm = new Label("LE");
            lm.setFont(Font.font("Georgia", FontWeight.BOLD, 14));
            lm.setTextFill(Color.web("#1A1A1A"));
            pane.getChildren().addAll(c1, c2, lm);
        }
        return pane;
    }

   
    private Node buildCenter() {
        HBox center = new HBox(0);
        center.getChildren().addAll(buildOrderPanel(), buildOrderTable());
        return center;
    }

    private Node buildOrderPanel() {
        VBox panel = new VBox(14);
        panel.setPrefWidth(460);
        panel.setPadding(new Insets(20, 24, 20, 24));
        panel.setStyle(
            "-fx-background-color: #232323;" +
            "-fx-border-color: #333333;" +
            "-fx-border-width: 0 1 0 0;"
        );

    
        panel.getChildren().add(sectionLabel("DINNER"));
        VBox dinnerBox = new VBox(5);
        dinnerBoxes = new CheckBox[DINNER_ITEMS.length];
        for (int i = 0; i < DINNER_ITEMS.length; i++) {
            CheckBox cb = new CheckBox();
            cb.getStyleClass().add("menu-check");
            cb.setOnAction(e -> onSelectionChange());
            dinnerBoxes[i] = cb;

            ImageView iv = loadMenuImage(DINNER_IMAGES[i], 44);

            Label nameLabel = new Label(DINNER_ITEMS[i]);
            nameLabel.setTextFill(Color.web("#CCCCCC"));
            nameLabel.setFont(Font.font("Arial", 12));
            nameLabel.setMinWidth(150);

            Label priceLabel = new Label(String.format("M %.2f", DINNER_PRICES[i]));
            priceLabel.setTextFill(Color.web("#C9A84C"));
            priceLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 12));

            Region stretch = new Region();
            HBox.setHgrow(stretch, Priority.ALWAYS);

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(3, 6, 3, 6));
            row.setStyle("-fx-background-color: #2A2A2A; -fx-background-radius: 4;");
            row.getChildren().addAll(cb, iv, nameLabel, stretch, priceLabel);

            final int idx = i;
            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #313131; -fx-background-radius: 4;"));
            row.setOnMouseExited(e  -> row.setStyle("-fx-background-color: #2A2A2A; -fx-background-radius: 4;"));
            row.setOnMouseClicked(e -> { dinnerBoxes[idx].setSelected(!dinnerBoxes[idx].isSelected()); onSelectionChange(); });

            dinnerBox.getChildren().add(row);
        }
        panel.getChildren().add(dinnerBox);
        panel.getChildren().add(separator());

        
        panel.getChildren().add(sectionLabel("DESSERT"));
        VBox dessertBox = new VBox(5);
        dessertBtns = new RadioButton[DESSERT_ITEMS.length];
        for (int i = 0; i < DESSERT_ITEMS.length; i++) {
            RadioButton rb = new RadioButton();
            // No ToggleGroup — allows multiple selections
            rb.setStyle("-fx-text-fill: #CCCCCC; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");
            rb.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) rb.setStyle("-fx-text-fill: #E8C55A; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");
                else            rb.setStyle("-fx-text-fill: #CCCCCC; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");
            });
            rb.setOnAction(e -> onSelectionChange());
            dessertBtns[i] = rb;

            ImageView iv = loadMenuImage(DESSERT_IMAGES[i], 44);

            Label nameLabel = new Label(DESSERT_ITEMS[i]);
            nameLabel.setTextFill(Color.web("#CCCCCC"));
            nameLabel.setFont(Font.font("Arial", 12));
            nameLabel.setMinWidth(150);

            Label priceLabel = new Label(String.format("M %.2f", DESSERT_PRICES[i]));
            priceLabel.setTextFill(Color.web("#C9A84C"));
            priceLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 12));

            Region stretch = new Region();
            HBox.setHgrow(stretch, Priority.ALWAYS);

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(3, 6, 3, 6));
            row.setStyle("-fx-background-color: #2A2A2A; -fx-background-radius: 4;");
            row.getChildren().addAll(rb, iv, nameLabel, stretch, priceLabel);

            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #313131; -fx-background-radius: 4;"));
            row.setOnMouseExited(e  -> row.setStyle("-fx-background-color: #2A2A2A; -fx-background-radius: 4;"));
            final int idx = i;
            row.setOnMouseClicked(e -> { dessertBtns[idx].setSelected(!dessertBtns[idx].isSelected()); onSelectionChange(); });

            dessertBox.getChildren().add(row);
        }
        panel.getChildren().add(dessertBox);
        panel.getChildren().add(separator());

        
        panel.getChildren().add(sectionLabel("DRINKS"));

        ImageView drinksImg = loadMenuImage("drinksVariety", 80);
        drinksImg.setFitWidth(412);
        drinksImg.setPreserveRatio(true);
        panel.getChildren().add(drinksImg);

        drinksCombo = new ComboBox<>(FXCollections.observableArrayList(DRINK_ITEMS));
        drinksCombo.getSelectionModel().selectFirst();
        drinksCombo.setPrefWidth(260);
        drinksCombo.getStyleClass().add("menu-combo");
        drinksCombo.setOnAction(e -> onSelectionChange());

        drinkQtySpinner = new Spinner<>(1, 20, 1);
        drinkQtySpinner.setPrefWidth(75);
        drinkQtySpinner.setStyle("-fx-background-color: #2C2C2C; -fx-border-color: #444444; -fx-border-width: 0 0 1.5 0;");
        drinkQtySpinner.getEditor().setStyle("-fx-text-fill: #C9A84C; -fx-font-family: 'Courier New'; -fx-font-weight: bold;");
        drinkQtySpinner.valueProperty().addListener((obs, o, n) -> onSelectionChange());

        Label qtyLabel = new Label("Qty");
        qtyLabel.setTextFill(Color.web("#888888"));
        qtyLabel.setFont(Font.font("Arial", 11));

        HBox drinkRow = new HBox(10);
        drinkRow.setAlignment(Pos.CENTER_LEFT);
        drinkRow.getChildren().addAll(drinksCombo, qtyLabel, drinkQtySpinner);
        panel.getChildren().add(drinkRow);
        panel.getChildren().add(separator());

        
        panel.getChildren().add(sectionLabel("💵  CASH TENDERED"));
        HBox cashRow = new HBox(10);
        cashRow.setAlignment(Pos.CENTER_LEFT);
        Label mLabel = new Label("M");
        mLabel.setTextFill(Color.web("#C9A84C"));
        mLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 14));
        cashField = new TextField("");
        cashField.setPromptText("Enter cash amount");
        cashField.setPrefWidth(180);
        cashField.getStyleClass().add("cash-field");
        cashField.textProperty().addListener((obs, o, n) -> recalcChange());
        cashRow.getChildren().addAll(mLabel, cashField);
        panel.getChildren().add(cashRow);

        ScrollPane sp = new ScrollPane(panel);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color: #232323; -fx-background: #232323; -fx-border-width: 0;");
        return sp;
    }

   
    private ImageView loadMenuImage(String baseName, double size) {
        ImageView iv = new ImageView();
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);

        String fileName = baseName + ".png";

        
        java.net.URL url = getClass().getResource("../extras/" + fileName);

        
        if (url == null) url = getClass().getResource("/restaurantSystem/extras/" + fileName);

        
        if (url == null) url = getClass().getClassLoader()
                                         .getResource("restaurantSystem/extras/" + fileName);

        if (url != null) {
            try { iv.setImage(new Image(url.toExternalForm())); return iv; }
            catch (Exception ignored) {}
        }

        
        String[] fsPaths = {
            "src/restaurantSystem/extras/" + fileName,
            "restaurantSystem/extras/"     + fileName,
            "extras/"                      + fileName,
        };
        for (String path : fsPaths) {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                try { iv.setImage(new Image(file.toURI().toString())); return iv; }
                catch (Exception ignored) {}
            }
        }

        return iv; 
    }

    
    @SuppressWarnings("unchecked")
    private Node buildOrderTable() {
        VBox right = new VBox(0);
        HBox.setHgrow(right, Priority.ALWAYS);
        right.setStyle("-fx-background-color: #1E1E1E;");

        HBox tableHeader = new HBox();
        tableHeader.setPadding(new Insets(12, 20, 12, 20));
        tableHeader.setStyle("-fx-background-color: #2A2A2A; -fx-border-color: #333; -fx-border-width: 0 0 1 0;");
        Label orderTitle = new Label("LIVE ORDER");
        orderTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 14));
        orderTitle.setTextFill(Color.web("#C9A84C"));
        Label orderSub = new Label("  — items update as you select");
        orderSub.setFont(Font.font("Arial", FontPosture.ITALIC, 11));
        orderSub.setTextFill(Color.web("#666666"));
        tableHeader.getChildren().addAll(orderTitle, orderSub);

        TableColumn<OrderRow, String> colItem  = col("ITEM",     "item",     260);
        TableColumn<OrderRow, String> colCat   = col("CATEGORY", "category", 90);
        TableColumn<OrderRow, String> colPrice = col("UNIT (M)", "price",    85);

        orderTable = new TableView<>(orderRows);
        orderTable.setPlaceholder(grayLabel("No items selected yet…"));
        orderTable.getColumns().addAll(colItem, colCat, colPrice);
        orderTable.setStyle("-fx-background-color: #1E1E1E;");
        orderTable.getStyleClass().add("order-table");
        VBox.setVgrow(orderTable, Priority.ALWAYS);

        VBox totals = new VBox(8);
        totals.setPadding(new Insets(16, 20, 16, 20));
        totals.setStyle("-fx-background-color: #222222; -fx-border-color: #333; -fx-border-width: 1 0 0 0;");
        totalLabel  = amountLabel("ORDER TOTAL :   M 0.00");
        changeLabel = amountLabel("CHANGE       :   M 0.00");
        changeLabel.setTextFill(Color.web("#6FCF97"));
        totals.getChildren().addAll(totalLabel, new Separator(), changeLabel);

        right.getChildren().addAll(tableHeader, orderTable, totals);
        return right;
    }

    
    private Node buildFooter() {
        HBox footer = new HBox(12);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(14, 30, 14, 30));
        footer.setStyle("-fx-background-color: #1A1A1A; -fx-border-color: #C9A84C; -fx-border-width: 1 0 0 0;");

        Button saleBtn  = actionBtn("✔   COMPLETE SALE",  "#C9A84C", "#1A1A1A");
        Button resetBtn = actionBtn("↺   RESET",          "#3A3A3A", "#BBBBBB");
        Button viewBtn  = actionBtn("📄  VIEW RECEIPTS",  "#1E3A5F", "#CCCCCC");
        Button exitBtn  = actionBtn("✕   EXIT",           "#5C2020", "#CCCCCC");

        saleBtn.setOnAction(e  -> handleCompleteSale());
        resetBtn.setOnAction(e -> handleReset());
        viewBtn.setOnAction(e  -> handleViewReceipts());
        exitBtn.setOnAction(e  -> handleExit());

        footer.getChildren().addAll(saleBtn, resetBtn, viewBtn, exitBtn);
        return footer;
    }

   
    private void onSelectionChange() {
        orderRows.clear();
        runningTotal = 0;

        for (int i = 0; i < DINNER_ITEMS.length; i++) {
            if (dinnerBoxes[i].isSelected()) {
                orderRows.add(new OrderRow(DINNER_ITEMS[i], "Dinner",
                        String.format("%.2f", DINNER_PRICES[i])));
                runningTotal += DINNER_PRICES[i];
            }
        }

        for (int i = 0; i < DESSERT_ITEMS.length; i++) {
            if (dessertBtns[i].isSelected()) {
                orderRows.add(new OrderRow(DESSERT_ITEMS[i], "Dessert",
                        String.format("%.2f", DESSERT_PRICES[i])));
                runningTotal += DESSERT_PRICES[i];
            }
        }

        int di  = drinksCombo.getSelectionModel().getSelectedIndex();
        int qty = drinkQtySpinner.getValue();
        if (di > 0) {
            double lineTotal = DRINK_PRICES[di] * qty;
            String itemLabel = qty > 1 ? DRINK_ITEMS[di] + " x" + qty : DRINK_ITEMS[di];
            orderRows.add(new OrderRow(itemLabel, "Drink", String.format("%.2f", lineTotal)));
            runningTotal += lineTotal;
        }

        totalLabel.setText(String.format("ORDER TOTAL :   M %.2f", runningTotal));
        recalcChange();
        animateTotal();
    }

    private void recalcChange() {
        try {
            double cash   = Double.parseDouble(cashField.getText().trim());
            double change = cash - runningTotal;
            if (change < 0) {
                changeLabel.setText(String.format("CHANGE       :   INSUFFICIENT  (M %.2f short)", Math.abs(change)));
                changeLabel.setTextFill(Color.web("#CF6679"));
            } else {
                changeLabel.setText(String.format("CHANGE       :   M %.2f", change));
                changeLabel.setTextFill(Color.web("#6FCF97"));
            }
        } catch (NumberFormatException ex) {
            changeLabel.setText("CHANGE       :   —  (enter cash amount)");
            changeLabel.setTextFill(Color.web("#888888"));
        }
    }

   
    private void handleCompleteSale() {
        if (orderRows.isEmpty()) {
            alert("No Items Selected", "Please select at least one item before completing the sale.");
            return;
        }
        double cash;
        try {
            cash = Double.parseDouble(cashField.getText().trim());
        } catch (NumberFormatException ex) {
            alert("No Cash Amount", "Please enter the cash tendered before completing the sale.");
            return;
        }
        if (cash < runningTotal) {
            alert("Insufficient Cash",
                String.format("Cash tendered (M %.2f) is less than the order total (M %.2f).%nShort by M %.2f.",
                        cash, runningTotal, runningTotal - cash));
            return;
        }

        double change    = cash - runningTotal;
        String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

        StringBuilder sb = new StringBuilder();
        sb.append("=============================================\n");
        sb.append("             La Etern FINE DINING           \n");
        sb.append("         Receipt  ·  Point of Sale          \n");
        sb.append("=============================================\n");
        sb.append("Date/Time : ").append(timestamp).append("\n");
        sb.append("---------------------------------------------\n");
        sb.append(String.format("%-28s  %s%n", "ITEM", "AMOUNT (M)"));
        sb.append("---------------------------------------------\n");
        for (OrderRow r : orderRows) {
            sb.append(String.format("%-28s  %s%n", r.getItem(), r.getPrice()));
        }
        sb.append("---------------------------------------------\n");
        sb.append(String.format("%-28s  %.2f%n", "TOTAL",         runningTotal));
        sb.append(String.format("%-28s  %.2f%n", "CASH TENDERED", cash));
        sb.append(String.format("%-28s  %.2f%n", "CHANGE",        change));
        sb.append("=============================================\n\n");

        try (FileWriter fw = new FileWriter("LaMaison_Receipts.txt", true)) {
            fw.write(sb.toString());
        } catch (IOException ex) {
            alert("Save Error", "Could not write receipt file: " + ex.getMessage());
            return;
        }

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Sale Complete");
        success.setHeaderText("✔  Sale completed successfully");
        success.setContentText(String.format(
            "Total:          M %.2f%nCash Tendered:  M %.2f%nChange Due:     M %.2f%n%nReceipt saved to LaMaison_Receipts.txt",
            runningTotal, cash, change));
        success.showAndWait();

        clearAll();
    }

    private void handleReset() { clearAll(); }

    private void handleViewReceipts() {
        File file = new File("LaMaison_Receipts.txt");
        if (!file.exists()) {
            alert("No Receipts Yet", "No receipts have been saved yet. Complete a sale first.");
            return;
        }
        try { java.awt.Desktop.getDesktop().open(file); }
        catch (IOException ex) { alert("Cannot Open File", "Could not open receipts file: " + ex.getMessage()); }
    }

    private void handleExit() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Exit");
        confirm.setHeaderText("Exit La Etern POS?");
        confirm.setContentText("Any unsaved orders will be lost.");
        confirm.showAndWait().ifPresent(btn -> { if (btn == ButtonType.OK) System.exit(0); });
    }

    private void clearAll() {
        for (CheckBox cb : dinnerBoxes) cb.setSelected(false);
        for (RadioButton rb : dessertBtns) rb.setSelected(false);
        drinksCombo.getSelectionModel().selectFirst();
        drinkQtySpinner.getValueFactory().setValue(1);
        cashField.clear();
        orderRows.clear();
        runningTotal = 0;
        totalLabel.setText("ORDER TOTAL :   M 0.00");
        changeLabel.setText("CHANGE       :   M 0.00");
        changeLabel.setTextFill(Color.web("#6FCF97"));
    }

    
    private void startClock() {
        updateClock();
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateClock()));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();
    }

    private void updateClock() {
        clockLabel.setText(DateTimeFormatter.ofPattern("EEE  dd MMM yyyy   HH:mm:ss").format(LocalDateTime.now()));
    }

    
    private void animateTotal() {
        ScaleTransition st = new ScaleTransition(Duration.millis(160), totalLabel);
        st.setFromX(1.0); st.setToX(1.06);
        st.setFromY(1.0); st.setToY(1.06);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    
    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        l.setTextFill(Color.web("#C9A84C"));
        l.setStyle("-fx-border-color: #C9A84C44; -fx-border-width: 0 0 1 0; -fx-padding: 6 0 4 0;");
        return l;
    }

    private Label amountLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        l.setTextFill(Color.web("#CCCCCC"));
        return l;
    }

    private Label grayLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.web("#555555"));
        l.setFont(Font.font("Arial", FontPosture.ITALIC, 12));
        return l;
    }

    private Separator separator() {
        Separator s = new Separator();
        s.setStyle("-fx-border-color: #333333; -fx-opacity: 0.6;");
        return s;
    }

    private Button actionBtn(String text, String bgColor, String fgColor) {
        Button btn = new Button(text);
        btn.setPrefHeight(42);
        btn.setMinWidth(160);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        String base  = String.format("-fx-background-color:%s;-fx-text-fill:%s;-fx-border-radius:2;-fx-background-radius:2;-fx-cursor:hand;", bgColor, fgColor);
        String hover = String.format("-fx-background-color:derive(%s,20%%);-fx-text-fill:%s;-fx-border-radius:2;-fx-background-radius:2;-fx-cursor:hand;", bgColor, fgColor);
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e  -> btn.setStyle(base));
        return btn;
    }

    private <T> TableColumn<OrderRow, T> col(String title, String property, int width) {
        TableColumn<OrderRow, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        c.setStyle("-fx-alignment: CENTER-LEFT;");
        return c;
    }

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}