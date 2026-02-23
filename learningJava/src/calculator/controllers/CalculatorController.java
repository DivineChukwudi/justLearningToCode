package calculator.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

public class CalculatorController {

    @FXML private Label displayLabel;
    @FXML private Label expressionLabel;
    @FXML private Button btnPercent;

    private double firstOperand = 0;
    private String pendingOperator = "";
    private boolean freshInput = true;
    private boolean justCalculated = false;

    @FXML
    public void initialize() {
        btnPercent.setText("%");
    }

    @FXML
    private void onNumber(ActionEvent event) {
        String digit = ((Button) event.getSource()).getText();
        if (freshInput || justCalculated) {
            displayLabel.setText(digit);
            freshInput = false;
            justCalculated = false;
        } else {
            String current = displayLabel.getText();
            displayLabel.setText(current.equals("0") ? digit : current + digit);
        }
    }

    @FXML
    private void onDot(ActionEvent event) {
        if (freshInput || justCalculated) {
            displayLabel.setText("0.");
            freshInput = false;
            justCalculated = false;
        } else if (!displayLabel.getText().contains(".")) {
            displayLabel.setText(displayLabel.getText() + ".");
        }
    }

    @FXML
    private void onOperator(ActionEvent event) {
        String op = ((Button) event.getSource()).getText();
        if (!freshInput && !pendingOperator.isEmpty()) {
            calculate();
        } else {
            firstOperand = Double.parseDouble(displayLabel.getText());
        }
        pendingOperator = op;
        expressionLabel.setText(formatNumber(firstOperand) + " " + op);
        freshInput = true;
        justCalculated = false;
    }

    @FXML
    private void onEquals(ActionEvent event) {
        if (pendingOperator.isEmpty()) return;
        double second = Double.parseDouble(displayLabel.getText());
        expressionLabel.setText(formatNumber(firstOperand) + " " + pendingOperator + " " + formatNumber(second) + " =");
        calculate();
        pendingOperator = "";
        justCalculated = true;
        freshInput = true;
    }

    @FXML
    private void onClear(ActionEvent event) {
        displayLabel.setText("0");
        expressionLabel.setText("");
        firstOperand = 0;
        pendingOperator = "";
        freshInput = true;
        justCalculated = false;
    }

    @FXML
    private void onNegate(ActionEvent event) {
        double value = Double.parseDouble(displayLabel.getText());
        displayLabel.setText(formatNumber(-value));
    }

    @FXML
    private void onPercent(ActionEvent event) {
        double value = Double.parseDouble(displayLabel.getText());
        displayLabel.setText(formatNumber(value / 100));
    }

    private void calculate() {
        double second = Double.parseDouble(displayLabel.getText());
        double result;
        switch (pendingOperator) {
            case "+": result = firstOperand + second; break;
            case "-": result = firstOperand - second; break;
            case "*": result = firstOperand * second; break;
            case "/":
                if (second == 0) {
                    displayLabel.setText("Error");
                    expressionLabel.setText("Division by zero");
                    firstOperand = 0;
                    pendingOperator = "";
                    freshInput = true;
                    return;
                }
                result = firstOperand / second;
                break;
            default: return;
        }
        firstOperand = result;
        displayLabel.setText(formatNumber(result));
    }

    private String formatNumber(double value) {
        if (value == (long) value && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.format("%.10f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}