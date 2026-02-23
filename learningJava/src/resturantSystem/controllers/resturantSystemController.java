package resturantSystem.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;

public class resturantSystemController implements Initializable {

    //Prices of items
    //Dinner
    private final double riceBeefPrice = 40.95;
    private final double riceChickenPrice = 35.50;
    private final double macaroniBeefPrice = 45.00;

    //Dessert
    private final double custardJellyPrice = 15.35;
    private final double chocolateCakePrice = 20.10;
    private final double cupcakesPrice = 12.50;

    //Drinks
    private final double cokePrice = 4.95;
    private final double spritePrice = 4.95;
    private final double fantaPrice = 4.95;
    private final double pepsiPrice = 4.95;
    private final double sevenupPrice = 4.95;
    private final double mirindaPrice = 4.95;
    private final double mtnDewPrice = 4.95;
    private final double liptonPrice = 4.95;

    double totalAmount = 0.0;

    @FXML
    private TextField totalAmountTextField;

    @FXML
    private TextField changeTextField;

    @FXML
    private TextField amountTenderedTextField;

    @FXML
    private VBox dinnerVBox;

    @FXML
    private VBox radioButtons;
    
    @FXML
    private Label titleLabel;

    @FXML
    private Label dinnerLabel;

    @FXML
    private Label dessertLabel;

    @FXML
    private Label drinksLabel;

    @FXML
    private CheckBox riceBeefCheckBox;

    @FXML
    private CheckBox riceChickenCheckBox;

    @FXML
    private CheckBox macaroniBeefCheckBox;

    @FXML
    private RadioButton custardJellyRadio;

    @FXML
    private RadioButton chocolateCakeRadio;

    @FXML
    private RadioButton cupcakesRadio;
    
    @FXML
    private ComboBox<String> drinkComboBox;

    @FXML
    private Button totalAmountButton;

    @FXML
    private Button changeButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button exitButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
   
    @FXML
    private void handleTotalAmount(ActionEvent event) {
        totalAmount = 0.0;

        Map<CheckBox, Double> menuItems = Map.of(
            riceBeefCheckBox, riceBeefPrice,
            riceChickenCheckBox, riceChickenPrice,
            macaroniBeefCheckBox, macaroniBeefPrice
        );
            for (Map.Entry<CheckBox, Double> entry : menuItems.entrySet()) {
                if (entry.getKey().isSelected()) {
                    totalAmount += entry.getValue();
                }
            }
    
            if (custardJellyRadio.isSelected()) {
                totalAmount += custardJellyPrice;
            } else if (chocolateCakeRadio.isSelected()) {
                totalAmount += chocolateCakePrice;
            } else if (cupcakesRadio.isSelected()) {
                totalAmount += cupcakesPrice;
            }
    
            String selectedDrink = drinkComboBox.getValue();

            if (selectedDrink != null) {
                switch (selectedDrink) {
                    case "Coke":
                        totalAmount += cokePrice;
                        break;
                    case "Sprite":
                        totalAmount += spritePrice;
                        break;
                    case "Fanta":
                        totalAmount += fantaPrice;
                        break;
                    case "Pepsi":
                        totalAmount += pepsiPrice;
                        break;
                    case "7up":
                        totalAmount += sevenupPrice;
                        break;
                    case "Mirinda":
                        totalAmount += mirindaPrice;
                        break;
                    case "Mtn Dew":
                        totalAmount += mtnDewPrice;
                        break;
                    case "Lipton":
                        totalAmount += liptonPrice;
                        break;
                }
            }

        totalAmountTextField.setText(String.format("M%.2f", totalAmount));
        
       }

    @FXML
    private void handleChange(ActionEvent event) {
        handleTotalAmount(event);

        String amountTendered = amountTenderedTextField.getText();
        if (amountTendered.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText(null);
            
        }
        try{
            double amountTenderedVal = Double.parseDouble(amountTendered);
            if (amountTenderedVal < totalAmount) {
                Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Insufficient Amount Error");
            alert.setContentText("Please enter a sufficient amount");
            alert.showAndWait();
            }
            else {
                double change = amountTenderedVal - totalAmount;
                changeTextField.setText(String.format("M%.2f", change));
               }
            }
               catch(NumberFormatException e){
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("Invalid Amount Error");
                alert.setContentText("Please enter a valid number for tendered amount");
                alert.showAndWait();
            }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        
        riceBeefCheckBox.setSelected(false);
        riceChickenCheckBox.setSelected(false);
        macaroniBeefCheckBox.setSelected(false);

        custardJellyRadio.setSelected(false);
        chocolateCakeRadio.setSelected(false);
        cupcakesRadio.setSelected(false);

        drinkComboBox.getSelectionModel().clearSelection();

        totalAmountTextField.clear();
        amountTenderedTextField.clear();
        changeTextField.clear();
    }

    @FXML
    private void handleExit(ActionEvent event) {
         Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
         stage.close();
    }
}