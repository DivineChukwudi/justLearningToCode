package periAreaCalc;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.control.*;
import java.io.IOException;
import javafx.application.*;



public class periAreaCalc extends Application{
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/periAreaCalc/fxml/periAreaCalc.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Calculate Perimeter and Area");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
