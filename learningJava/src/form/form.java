package form;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class form extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        File fxmlFile = new File("src/form/fxml/form.fxml");
        Parent root = FXMLLoader.load(fxmlFile.toURI().toURL());

        Label errorView = (Label) root.lookup("#errorView");
        TextField nameField = (TextField) root.lookup("#nameField");
        TextField surnameField = (TextField) root.lookup("#surnameField");
        TextField emailField = (TextField) root.lookup("#emailField");
        TextArea commentField = (TextArea) root.lookup("#commentField");
        Button submitButton = (Button) root.lookup("#submitButton");
        

        
        applyLetterOnlyFilter(nameField);
        applyLetterOnlyFilter(surnameField);
        applyEmailOnlyFilter(emailField);

        
        submitButton.setOnAction(event -> {
            String name = nameField.getText();
            String surname = surnameField.getText();
            String email = emailField.getText();
            String comment = commentField.getText();
            
            if (name.isEmpty() || surname.isEmpty()) {
                errorView.setText("Please fill in name and surname!");
                return;
            }else {
                errorView.setText("");
            }

            if (!isValidEmail(email)) {
                errorView.setText("Invalid email address!");
                return;
            }else{
                errorView.setText("");
            }
                saveToNotepad(name, surname, email, comment);
                
                nameField.clear();
                surnameField.clear();
                emailField.clear();
                commentField.clear();
                System.out.println("Data saved successfully!");
                errorView.setText("Data saved successfully!");
        });

        Scene scene = new Scene(root);
        primaryStage.setTitle("Registration Form");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void applyLetterOnlyFilter(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[a-zA-Z]*")) {
                return change;
            }
            return null;
        }));
    }
    private void applyEmailOnlyFilter(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[A-Za-z0-9._%+@-]*")) {
                return change;
            }
            return null;
        }));
    }

    private boolean isValidEmail(String email) {
    return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
}


   
    private void saveToNotepad(String name, String surname, String email, String comment) {
    String filePath = "database.txt";

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
        writer.write("Name: " + name);
        writer.newLine();
        writer.write("Surname: " + surname);
        writer.newLine();
        writer.write("Email: " + email);
        writer.newLine();
        writer.write("Comment: " + comment);
        writer.newLine();
        writer.write("Timestamp: " + System.currentTimeMillis());
        writer.newLine();
        writer.write("----------------------------");
        writer.newLine();

        System.out.println("Successfully wrote to file");

    } catch (IOException e) {
        System.out.println("An error occurred while trying to save information to file.");
        e.printStackTrace();
    }
}

    
    static class User {
        
        String name;
        String surname;
        String email;
        String comment;
        int timestamp;

        User(String studentID, String name, String surname, String email, String comment, int timestamp) {
            
            this.name = name;
            this.surname = surname;
            this.email = email;
            this.comment = comment;
            this.timestamp = timestamp;
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}