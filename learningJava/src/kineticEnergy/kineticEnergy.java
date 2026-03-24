package kineticEnergy;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class kineticEnergy extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/kineticEnergy/fxml/kineticEnergy.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Calculate Kinetic Energy");
        stage.setScene(scene);
        stage.show();
    }
   public static void main(String[] args) {
        launch(args);
    }
}

