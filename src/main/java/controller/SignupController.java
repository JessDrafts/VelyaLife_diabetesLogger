package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import application.VelyaLifeApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.AbstractUser;
import model.Doctor;
import repository.DoctorRepository;
import repository.UserRepository;
import utility.UserFactory;
import utility.UserService;
import components.ViewNavigator;
import utility.EmailService;

import static utility.Help_functions.isValidEmail;
import static utility.Help_functions.isValideCodicefiscale;

public class SignupController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField lastNameField;
    @FXML private TextField codiceFiscaleField;
    @FXML private TextField dateBirthField;
    @FXML private TextField placeBirthField;
    @FXML private TextField nationalityField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField sexField;

    @FXML private ChoiceBox<String> userTypeField;
    private final String[] userTypes = {"Doctor", "Patient"};

    @FXML private VBox patientFieldsContainer;
    @FXML private TextField weightField;
    @FXML private CheckBox smokerCheckBox;
    @FXML private CheckBox drinkerCheckBox;
    @FXML private TextArea riskFactorsArea;
    @FXML private ComboBox<Doctor> doctorComboBox;

    @FXML private Label statusLabel;

    private UserRepository userRepository;
    private final UserService userService = new UserService();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        userTypeField.getItems().addAll(userTypes);
        userRepository = VelyaLifeApplication.getUserRepository();
        DoctorRepository doctorRepository = VelyaLifeApplication.getDoctorRepository();
        statusLabel.setVisible(false);

        if (doctorComboBox != null) {
            doctorComboBox.getItems().addAll(doctorRepository.getAll());
            doctorComboBox.setCellFactory(_ -> new ListCell<>() {
                @Override
                protected void updateItem(Doctor doctor, boolean empty) {
                    super.updateItem(doctor, empty);
                    if (empty || doctor == null) {
                        setText(null);
                    } else {
                        setText(doctor.getName() + " " + doctor.getLastName() + " (" + doctor.getUsername() + ")");
                    }
                }
            });
            doctorComboBox.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Doctor doctor) {
                    return doctor == null ? "" : doctor.getName() + " " + doctor.getLastName();
                }

                @Override
                public Doctor fromString(String string) {
                    return null;
                }
            });
        }

        if (patientFieldsContainer != null) {
            patientFieldsContainer.visibleProperty().bind(
                    userTypeField.valueProperty().isEqualTo("Patient")
            );
            patientFieldsContainer.managedProperty().bind(
                    patientFieldsContainer.visibleProperty()
            );
        }

        userTypeField.setValue("Doctor");

        Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            SkinBase<ChoiceBox<String>> skin = (SkinBase<ChoiceBox<String>>) userTypeField.getSkin();
            if (skin != null) {
                for (Node child : skin.getChildren()) {
                    if (child instanceof Label label) {
                        if (label.getText().isEmpty()) {
                            label.setText("Doctor");
                        }
                        return;
                    }
                }
            }
        });
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText();
        String lastName = lastNameField.getText();
        String codiceFiscale = codiceFiscaleField.getText();
        String dateBirthStr = dateBirthField != null ? dateBirthField.getText() : "";
        String placeBirth = placeBirthField != null ? placeBirthField.getText() : "";
        String nationality = nationalityField != null ? nationalityField.getText() : "";
        String email = emailField.getText();
        String phone = phoneField != null ? phoneField.getText() : "";
        String userType = userTypeField.getValue();
        String sex = sexField.getText().toUpperCase();

        AbstractUser newUser = null;
        String receiver;
        String object = "VelyaLife Sign up";
        String body = "<h1>Access information</h1><p>Dear user,</p>" +
                "<p>Below you will find the username and password to access the VelyaLife app. The password provided here is temporary, it must be changed after the first access.</p>";

        if (name.isEmpty() || lastName.isEmpty() || codiceFiscale.isEmpty() || dateBirthStr.isEmpty() ||
                nationality.isEmpty() || email.isEmpty() || phone.isEmpty() || userType == null || sex.isEmpty()) {
            showError("Fill all spaces 😑");
            return;
        }

        if (userRepository.usernameExists(codiceFiscale)) {
            showError("User exists 😢");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(dateBirthStr, formatter);
        } catch (Exception e) {
            showError("Incorrect date format. Use DD/MM/YYYY 😢");
            return;
        }

        boolean cf = isValideCodicefiscale(codiceFiscale, lastName, name, birthDate, sex.charAt(0), placeBirth, nationality);
        if (!cf) {
            showError("Wrong Codice fiscale 😢");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Incorrect email format 😢");
            return;
        }

        if ("Patient".equalsIgnoreCase(userType)) {
            String weightStr = weightField != null ? weightField.getText() : "";
            boolean isSmoker = smokerCheckBox != null && smokerCheckBox.isSelected();
            boolean isDrinker = drinkerCheckBox != null && drinkerCheckBox.isSelected();
            String riskFactors = riskFactorsArea != null ? riskFactorsArea.getText() : "";
            Doctor selectedDoctor = doctorComboBox != null ? doctorComboBox.getValue(): null;

            if (weightStr.isEmpty() || riskFactors.isEmpty() || selectedDoctor == null) {
                showError("Fill all spaces including doctor 😑");
                return;
            }

            double weight = Double.parseDouble(weightStr);

            newUser = UserFactory.createPatient(
                    name, lastName, birthDate, placeBirth, nationality, sex,
                    codiceFiscale, weight, isSmoker, isDrinker, riskFactors, selectedDoctor, phone, email
            );

        } else if ("Doctor".equalsIgnoreCase(userType)) {
            newUser = UserFactory.createDoctor(
                    name, lastName, birthDate, placeBirth, nationality, sex,
                    codiceFiscale, phone, email
            );
        }

        if (newUser != null) {
            newUser.setPasswordGen();
            userService.registerUserToDb(newUser);

            receiver = newUser.getEmail();
            body += """
                    <p>Username:\s"""  + newUser.getUsername() + """
                        <br><br>Password:\s""" + newUser.getPlainPassword() + """
                        <p>The new password must be at least 6 characters long. It must include at least one of the following:</p>
                        <ul>
                            <li>Upper case letter</li>
                            <li>Lower case letter</li>
                            <li>Number (0-9)</li>
                            <li>Special character between the following ones: @#&amp;_-</li>
                        </ul>
                       \s
                        <p>From, Admin.</p>""";
            EmailService.sendEmail(receiver, object, body);

            clearFields();
            showSuccess();
        }
    }

    private void clearFields() {
        nameField.clear();
        lastNameField.clear();
        codiceFiscaleField.clear();
        emailField.clear();

        if (dateBirthField != null) dateBirthField.clear();
        if (placeBirthField != null) placeBirthField.clear();
        if (nationalityField != null) nationalityField.clear();
        if (phoneField != null) phoneField.clear();
        if (sexField != null) sexField.clear();

        if (weightField != null) weightField.clear();
        if (smokerCheckBox != null) smokerCheckBox.setSelected(false);
        if (drinkerCheckBox != null) drinkerCheckBox.setSelected(false);
        if (riskFactorsArea != null) riskFactorsArea.clear();
        if (doctorComboBox != null) doctorComboBox.setValue(null);
    }

    private PauseTransition errorTimeline;

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);

        if (errorTimeline != null) {
            errorTimeline.stop();
        }

        errorTimeline = new PauseTransition(Duration.seconds(4));
        errorTimeline.setOnFinished(_ -> {
            statusLabel.setVisible(false);
            statusLabel.setText("");
        });
        errorTimeline.play();
    }

    private void showSuccess() {
        if (errorTimeline != null) {
            errorTimeline.stop();
        }

        statusLabel.setText("User signed up 😁");
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setVisible(true);

        if (errorTimeline != null) {
            errorTimeline.stop();
        }

        errorTimeline = new PauseTransition(Duration.seconds(4));
        errorTimeline.setOnFinished(_ -> {
            statusLabel.setVisible(false);
            statusLabel.setText("");
        });
        errorTimeline.play();
    }

    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToAdminDashboard();
    }
}