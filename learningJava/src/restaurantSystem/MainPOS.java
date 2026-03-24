package restaurantSystem;

import javafx.application.Application;
import javafx.stage.Stage;
import restaurantSystem.controllers.MainPOSController;


public class MainPOS extends Application {

    @Override
    public void start(Stage stage) {
        MainPOSController controller = new MainPOSController(stage);
        controller.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}