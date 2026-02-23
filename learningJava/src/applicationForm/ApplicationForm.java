package applicationForm;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ApplicationForm extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
       try {
            java.net.URL resource = getClass().getResource("/applicationForm/fxml/applicationForm.fxml");
            System.out.println("Resource URL: " + resource);
            if (resource == null) {
                System.err.println("ERROR: FXML resource not found!");
                return;
            }
            Parent root = FXMLLoader.load(resource);
            
            System.out.println("Root class: " + root.getClass().getName());
            
            // Try to find the VBox inside the ScrollPane
            Pane vbox = null;
            if (root instanceof javafx.scene.control.ScrollPane) {
                javafx.scene.control.ScrollPane pane = (javafx.scene.control.ScrollPane) root;
                System.out.println("Root is ScrollPane, content: " + pane.getContent());
                if (pane.getContent() instanceof Parent) {
                    vbox = (Pane) pane.getContent();
                }
            }
            
            Parent searchRoot = (vbox != null) ? vbox : root;

        Label errorView = (Label) searchRoot.lookup("#errorView");
        TextField studentNumber = (TextField) searchRoot.lookup("#studentNumber");
        TextField nameField = (TextField) searchRoot.lookup("#nameField");
        TextField surnameField = (TextField) searchRoot.lookup("#surnameField");
        TextField emailField = (TextField) searchRoot.lookup("#emailField");
        TextField phoneNumber = (TextField) searchRoot.lookup("#phoneNumber");
        TextField addressField = (TextField) searchRoot.lookup("#addressField");
        TextField cityField = (TextField) searchRoot.lookup("#cityField");
        TextField postalCodeField = (TextField) searchRoot.lookup("#postalCodeField");
        TextField dateOfBirthField = (TextField) searchRoot.lookup("#dateOfBirthField");
        TextField idNumberField = (TextField) searchRoot.lookup("#idNumberField");
        Button submitButton = (Button) searchRoot.lookup("#submitButton");

        @SuppressWarnings("unchecked")
        ComboBox<String> courseComboBox = (ComboBox<String>) searchRoot.lookup("#courseComboBox");
        @SuppressWarnings("unchecked")
        ComboBox<String> genderComboBox = (ComboBox<String>) searchRoot.lookup("#genderComboBox");

        if (errorView == null || studentNumber == null || nameField == null ||
            surnameField == null || emailField == null || phoneNumber == null ||
            addressField == null || cityField == null || postalCodeField == null ||
            dateOfBirthField == null || idNumberField == null ||
            submitButton == null || courseComboBox == null || genderComboBox == null) {
            System.err.println("Error: Failed to load one or more FXML components!");
            System.err.println("Missing components:");
            System.err.println("  errorView: " + (errorView != null));
            System.err.println("  studentNumber: " + (studentNumber != null));
            System.err.println("  nameField: " + (nameField != null));
            System.err.println("  surnameField: " + (surnameField != null));
            System.err.println("  emailField: " + (emailField != null));
            System.err.println("  phoneNumber: " + (phoneNumber != null));
            System.err.println("  addressField: " + (addressField != null));
            System.err.println("  cityField: " + (cityField != null));
            System.err.println("  postalCodeField: " + (postalCodeField != null));
            System.err.println("  dateOfBirthField: " + (dateOfBirthField != null));
            System.err.println("  idNumberField: " + (idNumberField != null));
            System.err.println("  submitButton: " + (submitButton != null));
            System.err.println("  courseComboBox: " + (courseComboBox != null));
            System.err.println("  genderComboBox: " + (genderComboBox != null));
            return;
        }

        applyLetterOnlyFilter(nameField);
        applyLetterOnlyFilter(surnameField);
        applyLetterOnlyFilter(addressField);
        applyLetterOnlyFilter(cityField);
        applyEmailOnlyFilter(emailField);
        applyNumberOnlyFilter(studentNumber);
        applyPhoneNumberFilter(phoneNumber);
        applyNumberOnlyFilter(postalCodeField);
        applyAlphanumericFilter(idNumberField);

        submitButton.setOnAction(event -> {

            String studentIDValue = studentNumber.getText().trim();
            String phoneNumberValue = phoneNumber.getText().trim();
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();
            String city = cityField.getText().trim();
            String postalCode = postalCodeField.getText().trim();
            String dateOfBirth = dateOfBirthField.getText().trim();
            String idNumber = idNumberField.getText().trim();

            String courseName = courseComboBox.getValue() != null ?
                                courseComboBox.getValue().trim() : "";
            String gender = genderComboBox.getValue() != null ?
                           genderComboBox.getValue() : "";

            if (studentIDValue.isEmpty()) {
                errorView.setText("Please enter student number!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (name.isEmpty() || surname.isEmpty()) {
                errorView.setText("Please fill in name and surname!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (address.isEmpty()) {
                errorView.setText("Please enter address!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (city.isEmpty()) {
                errorView.setText("Please enter city!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (postalCode.isEmpty()) {
                errorView.setText("Please enter postal code!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (postalCode.length() < 4) {
                errorView.setText("Postal code must be at least 4 digits!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (phoneNumberValue.isEmpty()) {
                errorView.setText("Please enter phone number!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            // Count only digits, excluding the "+" prefix if present
            String digitsOnly = phoneNumberValue.replaceAll("[^0-9]", "");
            if (digitsOnly.length() < 10) {
                errorView.setText("Phone number must have at least 10 digits!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (email.isEmpty()) {
                errorView.setText("Please enter email address!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (!isValidEmail(email)) {
                errorView.setText("Invalid email address!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (dateOfBirth.isEmpty()) {
                errorView.setText("Please enter date of birth!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (!isValidDateOfBirth(dateOfBirth)) {
                errorView.setText("Invalid date of birth! Use format YYYY-MM-DD and ensure date is in the past!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (gender.isEmpty()) {
                errorView.setText("Please select gender!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (idNumber.isEmpty()) {
                errorView.setText("Please enter ID number!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            if (courseName.isEmpty()) {
                errorView.setText("Please select a course!");
                errorView.setStyle("-fx-text-fill: red;");
                return;
            }

            boolean saved = saveToNotepad(studentIDValue, phoneNumberValue, courseName,
                                         name, surname, email, address, city, postalCode,
                                         dateOfBirth, gender, idNumber);

            if (saved) {

                studentNumber.clear();
                phoneNumber.clear();
                courseComboBox.setValue(null);
                nameField.clear();
                surnameField.clear();
                emailField.clear();
                addressField.clear();
                cityField.clear();
                postalCodeField.clear();
                dateOfBirthField.clear();
                genderComboBox.setValue(null);
                idNumberField.clear();

                System.out.println("Data saved successfully!");
                errorView.setText("Data saved successfully!");
                errorView.setStyle("-fx-text-fill: green;");
            } else {
                errorView.setText("Error saving data!");
                errorView.setStyle("-fx-text-fill: red;");
            }
        });

        Scene scene = new Scene(root);
        primaryStage.setTitle("Student Registration Form");
        primaryStage.setScene(scene);
        primaryStage.show();
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyLetterOnlyFilter(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[a-zA-Z ]*")) {
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

    private void applyNumberOnlyFilter(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
    }

    private void applyAlphanumericFilter(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[a-zA-Z0-9]*")) {
                return change;
            }
            return null;
        }));
    }

    private void applyPhoneNumberFilter(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[+\\d]*")) {
                return change;
            }
            return null;
        }));
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isValidDateOfBirth(String dateOfBirth) {
        try {
            java.time.LocalDate dob = java.time.LocalDate.parse(dateOfBirth);
            java.time.LocalDate today = java.time.LocalDate.now();
            
            // Ensure date is not in the future and at least 13 years old (common age requirement)
            if (dob.isAfter(today)) {
                return false;
            }
            
            // Check if person is at least 13 years old
            if (dob.plusYears(13).isAfter(today)) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean saveToNotepad(String studentID, String phoneNumber, String courseName,
                                   String name, String surname, String email, String address,
                                   String city, String postalCode, String dateOfBirth,
                                   String gender, String idNumber) {
        String filePath = "applicationFormDatabase.txt";

        String timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("Student Number: " + studentID);
            writer.newLine();
            writer.write("Name: " + name);
            writer.newLine();
            writer.write("Surname: " + surname);
            writer.newLine();
            writer.write("Email: " + email);
            writer.newLine();
            writer.write("Phone Number: " + phoneNumber);
            writer.newLine();
            writer.write("Address: " + address);
            writer.newLine();
            writer.write("City: " + city);
            writer.newLine();
            writer.write("Postal Code: " + postalCode);
            writer.newLine();
            writer.write("Date of Birth: " + dateOfBirth);
            writer.newLine();
            writer.write("Gender: " + gender);
            writer.newLine();
            writer.write("ID Number: " + idNumber);
            writer.newLine();
            writer.write("Course Name: " + courseName);
            writer.newLine();
            writer.write("Timestamp: " + timestamp);
            writer.newLine();
            writer.write("----------------------------");
            writer.newLine();

            System.out.println("Successfully wrote to file: " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("An error occurred while trying to save information to file.");
            e.printStackTrace();
            return false;
        }
    }

    static class User {
        String studentID;
        String phoneNumber;
        String courseName;
        String name;
        String surname;
        String email;
        String address;
        String city;
        String postalCode;
        String dateOfBirth;
        String gender;
        String idNumber;
        String timestamp;

        User(String studentID, String phoneNumber, String courseName, String name,
             String surname, String email, String address, String city, String postalCode,
             String dateOfBirth, String gender, String idNumber, String timestamp) {
            this.studentID = studentID;
            this.phoneNumber = phoneNumber;
            this.courseName = courseName;
            this.name = name;
            this.surname = surname;
            this.email = email;
            this.address = address;
            this.city = city;
            this.postalCode = postalCode;
            this.dateOfBirth = dateOfBirth;
            this.gender = gender;
            this.idNumber = idNumber;
            this.timestamp = timestamp;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
