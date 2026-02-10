import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Button;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class form extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        File fxmlFile = new File("src/fxml/form.fxml");
        Parent root = FXMLLoader.load(fxmlFile.toURI().toURL());

        TextField nameField = (TextField) root.lookup("#nameField");
        TextField surnameField = (TextField) root.lookup("#surnameField");
        TextArea commentField = (TextArea) root.lookup("#commentField");
        Button submitButton = (Button) root.lookup("#submitButton");

        
        applyLetterOnlyFilter(nameField);
        applyLetterOnlyFilter(surnameField);

        
        submitButton.setOnAction(event -> {
            String name = nameField.getText();
            String surname = surnameField.getText();
            String comment = commentField.getText();
            
            if (!name.isEmpty() && !surname.isEmpty()) {
                saveToJSON(name, surname, comment);
                
                nameField.clear();
                surnameField.clear();
                commentField.clear();
                System.out.println("Data saved successfully!");
            } else {
                System.out.println("Please fill in name and surname!");
            }
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

    private void saveToJSON(String name, String surname, String comment) {
        String filePath = "database.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<User> users = new ArrayList<>();

        try {
            
            File file = new File(filePath);
            if (file.exists()) {
                FileReader reader = new FileReader(filePath);
                Type listType = new TypeToken<ArrayList<User>>(){}.getType();
                users = gson.fromJson(reader, listType);
                reader.close();
                
                if (users == null) {
                    users = new ArrayList<>();
                }
            }

        
            User newUser = new User(name, surname, comment, System.currentTimeMillis());
            users.add(newUser);

            
            FileWriter writer = new FileWriter(filePath);
            gson.toJson(users, writer);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    static class User {
        String name;
        String surname;
        String comment;
        long timestamp;

        User(String name, String surname, String comment, long timestamp) {
            this.name = name;
            this.surname = surname;
            this.comment = comment;
            this.timestamp = timestamp;
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}