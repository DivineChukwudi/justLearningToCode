package kineticEnergy.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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

public class kineticEnergyController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

   @FXML
   private TextField massField;

   @FXML
   public TextField velocityField;

   @FXML
   private TextField outputField;

   @FXML
   private Button calculateButton;

   @FXML
   private Button clearButton;

   @FXML
   private Button exitButton;

   

   @FXML
   private void exitApplication(ActionEvent event) {
    Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.close();

   }

   @FXML
   private void clearFields() {
    massField.clear();
    velocityField.clear();
    outputField.clear();
   }

   @FXML
   private void calculateKineticEnergy(ActionEvent event) {
    String massText = massField.getText();
    String velocityText = velocityField.getText();

    if (massText.isEmpty() || velocityText.isEmpty()) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText("Please enter both mass and velocity.");
        alert.showAndWait();
        return;
    }else{
            

            if(massText.matches("\\d*\\.?\\d+") && velocityText.matches("\\d*\\.?\\d+")){
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter valid numbers for mass and velocity.");
                alert.showAndWait();
                clearFields();
                return;
            }

        }

    try {
        double mass = Double.parseDouble(massText);
        double velocity = Double.parseDouble(velocityText);

        double kineticEnergy = 0.5 * mass * Math.pow(velocity, 2);
        outputField.setText(String.format("%.2f Joules", kineticEnergy));
    } catch (NumberFormatException e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText("Please enter valid numbers for mass and velocity.");
        alert.showAndWait();
        clearFields();
    }
    
}

}