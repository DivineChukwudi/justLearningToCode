package restaurantSystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import restaurantSystem.controllers.LoginController;

 
public class LoginScreen extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("fxml/LoginScreen.fxml"));

        Parent root = loader.load();

        LoginController controller = loader.getController();
        controller.setStage(stage);

        Scene scene = new Scene(root, 820, 600);

        
        java.net.URL css = getClass().getResource("css/styles.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle("La Etern – Exclusive Eatery");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
    }

    public static void main(String[] args) {
        launch(args);
    }
}