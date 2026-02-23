package resturantSystem.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.Node;


public class loginController implements Initializable{
static ObservableList list = FXCollections.observableArrayList();

static String uname="";

String username = "manager";
String password = "1234";

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passField;
    
    @FXML
    private Button loginFocus;

    @FXML
    private Button cancelFocus;

    public void initialize(URL url, ResourceBundle rb) {

    }

    public static String getVariable() {
        return uname;
    }

    @FXML
    private void loginUser (ActionEvent event) throws IOException {
        String name = usernameField.getText();
        String pass = passField.getText();

        if (name.isEmpty() || pass.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Fill all required fields");
            alert.showAndWait();
            clearFields(usernameField, passField);
        }
        else if(name.equals(username) && pass.equals(password)){
            loginController.uname = name;
            ((Node)event.getSource()).getScene().getWindow().hide();
            loadWindow("/resturantSystem/fxml/resturantSystem.fxml", "Resturant Eten");

        }
        else {
            
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Combination of username and password is incorrect");
            alert.showAndWait();
            clearFields(usernameField, passField);
        }

    }
    @FXML
    private void cancelUser(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    private void loadWindow(String location, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(location));
        Scene scene = new Scene(root);
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();

        
    }
    public static void clearFields(TextField usernameField, PasswordField passField) {
        usernameField.clear();
        passField.clear();
    }
}
