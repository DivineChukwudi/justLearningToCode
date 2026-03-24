package restaurantSystem.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    
    @FXML private StackPane   logoPane;
    @FXML private TextField   usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label       errorLabel;
    @FXML private Button      loginBtn;

    
    private Node loginCard;

    
    private Stage stage;

    private static final String VALID_USER = "admin";
    private static final String VALID_PASS = "admin123";

    
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        RotateTransition spinIn = new RotateTransition(Duration.millis(1000), logoPane);
        spinIn.setFromAngle(-360);
        spinIn.setToAngle(0);
        spinIn.setInterpolator(Interpolator.EASE_OUT);
        spinIn.play();

        
        logoPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                loginCard = logoPane.getParent(); // VBox card
                playEntranceAnimation(loginCard);
                playGlowAnimation(loginCard);
            }
        });
    }

    
    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            shake(errorLabel, "Please enter both username and password.");
            return;
        }

        if (user.equals(VALID_USER) && pass.equals(VALID_PASS)) {
            FadeTransition fadeOut = new FadeTransition(
                    Duration.millis(400), stage.getScene().getRoot());
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> openMainPOS());
            fadeOut.play();
        } else {
            shake(errorLabel, "Invalid credentials.  Try  admin / admin123");
        }
    }

   
    private void openMainPOS() {
        try {
            MainPOSController posCtrl = new MainPOSController(stage);
            posCtrl.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Failed to load POS screen.");
        }
    }

    
    private void playEntranceAnimation(Node card) {
        card.setOpacity(0);
        card.setTranslateY(30);

        FadeTransition fade = new FadeTransition(Duration.millis(800), card);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(800), card);
        slide.setFromY(30);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition entrance = new ParallelTransition(fade, slide);
        entrance.setDelay(Duration.millis(200));
        entrance.play();
    }

    private void playGlowAnimation(Node card) {
        Glow glow = new Glow(0.0);
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#C9A84C", 0.3));
        ds.setRadius(40);
        glow.setInput(ds);
        card.setEffect(glow);

        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO,       new KeyValue(glow.levelProperty(), 0.0)),
            new KeyFrame(Duration.seconds(2), new KeyValue(glow.levelProperty(), 0.4)),
            new KeyFrame(Duration.seconds(4), new KeyValue(glow.levelProperty(), 0.0))
        );
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setDelay(Duration.seconds(1));
        pulse.play();
    }

    private void shake(Label label, String message) {
        label.setText(message);
        TranslateTransition shake = new TranslateTransition(
                Duration.millis(60), label.getParent());
        shake.setFromX(-8);
        shake.setToX(8);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    
    private void applyStylesheet(Scene scene) {
        java.net.URL url = getClass().getResource("../css/styles.css");
        if (url != null) scene.getStylesheets().add(url.toExternalForm());
    }
}