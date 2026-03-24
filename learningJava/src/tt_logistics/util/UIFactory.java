package tt_logistics.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UIFactory {

    public static final String BG_DARK   = "#0D1B2A";
    public static final String BG_CARD   = "#162032";
    public static final String BG_ROW    = "#111D2B";
    public static final String BORDER    = "#1E3A5F";
    public static final String ACCENT    = "#1E90FF";
    public static final String TEXT_MAIN = "#AECBEB";
    public static final String TEXT_SUB  = "#4A6FA5";

    public static BorderPane darkRoot() {
        BorderPane p = new BorderPane();
        p.setStyle("-fx-background-color: " + BG_DARK + ";");
        return p;
    }

    public static HBox topBar(String title, String accent, Runnable onBack) {
        HBox bar = new HBox(14);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(16, 24, 16, 24));
        bar.setStyle("-fx-background-color: " + BG_CARD + ";" +
                     "-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        Button back = new Button("← Back");
        String backBase = "-fx-background-color: transparent; -fx-text-fill: " + TEXT_SUB + ";" +
                "-fx-border-color: " + BORDER + "; -fx-border-radius: 6;" +
                "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 14 6 14;";
        String backHover = "-fx-background-color: " + BORDER + "; -fx-text-fill: " + TEXT_MAIN + ";" +
                "-fx-border-color: " + BORDER + "; -fx-border-radius: 6;" +
                "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 14 6 14;";
        back.setStyle(backBase);
        back.setOnMouseEntered(e -> back.setStyle(backHover));
        back.setOnMouseExited(e  -> back.setStyle(backBase));
        back.setOnAction(e -> onBack.run());

        Label lbl = new Label(title);
        lbl.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        lbl.setStyle("-fx-text-fill: " + accent + ";");

        bar.getChildren().addAll(back, lbl);
        return bar;
    }

    public static Button primaryBtn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;" +
                   "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 12;" +
                   "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 9 20 9 20;");
        b.setOnMouseEntered(e -> b.setOpacity(0.82));
        b.setOnMouseExited(e  -> b.setOpacity(1.0));
        return b;
    }

    public static Button dangerBtn(String text) { return primaryBtn(text, "#E05C5C"); }

    public static VBox fieldGroup(String labelText, Control field) {
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        lbl.setStyle("-fx-text-fill: " + TEXT_SUB + "; -fx-letter-spacing: 1;");
        styleControl(field);
        return new VBox(5, lbl, field);
    }

    public static void styleControl(Control c) {
        c.setStyle(
            "-fx-background-color: " + BG_ROW + ";" +
            "-fx-text-fill: " + TEXT_MAIN + ";" +
            "-fx-prompt-text-fill: #2E4A6E;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-padding: 8 12 8 12; -fx-font-size: 13;" +
            "-fx-font-family: 'Segoe UI';"
        );
    }

    public static VBox card(double hPad, double vPad) {
        VBox v = new VBox(14);
        v.setPadding(new Insets(vPad, hPad, vPad, hPad));
        v.setStyle("-fx-background-color: " + BG_CARD + ";" +
                   "-fx-background-radius: 12; -fx-border-color: " + BORDER + ";" +
                   "-fx-border-width: 1; -fx-border-radius: 12;");
        return v;
    }

    public static Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        l.setStyle("-fx-text-fill: " + TEXT_SUB + ";");
        return l;
    }

    public static Label feedbackLabel() {
        Label l = new Label("");
        l.setFont(Font.font("Segoe UI", 12));
        l.setWrapText(true);
        return l;
    }

    public static void setError(Label l, String msg) {
        l.setText("⚠  " + msg);
        l.setStyle("-fx-text-fill: #FF6B6B;");
    }

    public static void setSuccess(Label l, String msg) {
        l.setText("✓  " + msg);
        l.setStyle("-fx-text-fill: #00C896;");
    }

    public static <T> void styleTable(TableView<T> tv) {
        tv.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-table-cell-border-color: " + BORDER + ";" +
            "-fx-font-family: 'Segoe UI'; -fx-font-size: 12;" +
            "-fx-text-fill: " + TEXT_MAIN + ";"
        );
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
