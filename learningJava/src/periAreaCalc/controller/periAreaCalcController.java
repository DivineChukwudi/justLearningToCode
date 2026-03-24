package periAreaCalc.controller;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.control.*;
import java.io.*;
import java.util.*;
import javafx.event.*;
import java.net.*;
import javafx.scene.control.Alert.*;


public class periAreaCalcController implements Initializable{

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    @FXML
    private TextField lengthField;

    @FXML
    private TextField widthField;

    @FXML
    private TextField perimeterField;

    @FXML
    private TextField areaField;




    @FXML
    public void calculatePerimeter(){
        Double length = Double.parseDouble(lengthField.getText());
        Double width = Double.parseDouble(widthField.getText());

        Double perimeter = 2 * (length + width);
        perimeterField.setText(String.format("%.2fcm", perimeter));
    }

    @FXML
    public void calculateArea(){
        Double length = Double.parseDouble(lengthField.getText());
        Double width = Double.parseDouble(widthField.getText());

        Double area = length * width;
        areaField.setText(String.format("%.2fcm²", area));
    }

    @FXML
    private void clearButton() {
        lengthField.clear();
        widthField.clear();
        perimeterField.clear();
        areaField.clear();
    }

    @FXML
    private void submitButton() {
        try{
            if(lengthField.getText().isEmpty() || widthField.getText().isEmpty()){
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Missing Input");
                alert.setContentText("Please enter both length and width.");
                alert.showAndWait();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        calculatePerimeter();
        calculateArea();
    }
}
