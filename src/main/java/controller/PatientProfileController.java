package controller;

import java.time.LocalDate;

import application.VelyaLifeApplication;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Patient;
import repository.PatientRepository;
import repository.UserRepository;
import utility.EmailService;
import components.ViewNavigator;

public class PatientProfileController {

    public TextField NameField;
    public TextField LastNameField;
    public DatePicker BirthDateField;
    public ChoiceBox<String> SexField;
    public TextField WeightField;
    public TextField CodiceFiscaleField;
    public TextField EmailField;
    public TextField TelNumberField;
    public VBox passwordBox;
    public Button saveButton;
    public Button editButton;
    public TextField PlaceBirthField;
    public TextField NationalityField;

    @FXML
    private Label usernameLabel;

    private final String[] sexTypes = {"Select", "Female", "Male", "prefer not to say"};

    @FXML
    private CheckBox smokerField;

    @FXML
    private CheckBox drinkerField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label statusLabel;

    private UserRepository userRepository;
    private PatientRepository patientRepository;
    private String currentUsername;
    private boolean isEditable = false;

    @FXML
    public void initialize() {
        SexField.getItems().addAll(sexTypes);
        SexField.setValue("Select");

        userRepository = VelyaLifeApplication.getUserRepository();
        patientRepository = VelyaLifeApplication.getPatientRepository();
        currentUsername = ViewNavigator.getAuthenticatedUser();
        usernameLabel.setText(currentUsername);
        statusLabel.setVisible(false);

        loadPatientData();
    }

    private void loadPatientData() {
        Patient currentUser = (Patient) userRepository.getById(currentUsername);
        if (currentUser != null) {
            if (currentUser.getName() != null) NameField.setText(currentUser.getName());
            if (currentUser.getLastName() != null) LastNameField.setText(currentUser.getLastName());
            if (currentUser.getDateBirth() != null) BirthDateField.setValue(currentUser.getDateBirth());
            if (currentUser.getSex() != null) SexField.setValue(currentUser.getSex());
            if (currentUser.getWeight() > 0) WeightField.setText(String.valueOf(currentUser.getWeight()));
            if (currentUser.getCodiceFiscale() != null) CodiceFiscaleField.setText(currentUser.getCodiceFiscale());
            if (currentUser.getEmail() != null) EmailField.setText(currentUser.getEmail());
            if (currentUser.getTelNumber() != null) TelNumberField.setText(currentUser.getTelNumber());
            if (currentUser.getPlaceBirth() != null) PlaceBirthField.setText(currentUser.getPlaceBirth());
            if (currentUser.getNationality() != null) NationalityField.setText(currentUser.getNationality());
            smokerField.setSelected(currentUser.getIsSmoker());
            drinkerField.setSelected(currentUser.getIsDrinker());
        }
    }

    @FXML
    private void handleEnableEdit() {
        isEditable = !isEditable;

        NameField.setEditable(isEditable);
        LastNameField.setEditable(isEditable);
        BirthDateField.setDisable(!isEditable);
        SexField.setDisable(!isEditable);
        WeightField.setEditable(isEditable);
        PlaceBirthField.setEditable(isEditable);
        NationalityField.setEditable(isEditable);
        CodiceFiscaleField.setEditable(isEditable);
        smokerField.setDisable(!isEditable);
        drinkerField.setDisable(!isEditable);
        EmailField.setEditable(isEditable);
        TelNumberField.setEditable(isEditable);

        passwordBox.setVisible(isEditable);
        passwordBox.setManaged(isEditable);
        saveButton.setDisable(!isEditable);

        editButton.setText(isEditable ? "Cancel" : "Edit Profile");
        statusLabel.setVisible(false);
    }

    @FXML
    private void handleUpdateProfile() {
        String receiver;
        String object = "Profile Update";
        String body = "<p>Dear User,</p><p>Your profile has been updated.</p>";

        String Name = NameField.getText();
        String LastName = LastNameField.getText();
        LocalDate BirthDate = BirthDateField.getValue();
        String CodiceFiscale = CodiceFiscaleField.getText();
        String Sex = SexField.getValue();
        String Weight = WeightField.getText();
        String PlaceBirth = PlaceBirthField.getText();
        String Nationality = NationalityField.getText();
        boolean smoker = smokerField.isSelected();
        boolean drinker = drinkerField.isSelected();
        String TelNumber = TelNumberField.getText();
        String Email = EmailField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        Patient currentUser = (Patient) userRepository.getById(currentUsername);

        if(!Name.isEmpty()) currentUser.setName(Name);
        if(!LastName.isEmpty()) currentUser.setLastName(LastName);
        if(BirthDate != null) currentUser.setDateBirth(BirthDate);
        if(PlaceBirth != null) currentUser.setPlaceBirth(PlaceBirth);
        if(Nationality != null) currentUser.setNationality(Nationality);
        if(!Sex.isEmpty() && !Sex.equals("Select")) currentUser.setSex(Sex);
        if(!Weight.isEmpty()) currentUser.setWeight(Double.parseDouble(Weight));

        currentUser.setSmoker(smoker);
        currentUser.setDrinker(drinker);

        if(!CodiceFiscale.isEmpty()) currentUser.setCodiceFiscale(CodiceFiscale);
        if(!TelNumber.isEmpty()) currentUser.setTelNumber(TelNumber);
        if(!Email.isEmpty()) currentUser.setEmail(Email);

        if (!newPassword.isEmpty()) {
            if (confirmPassword.isEmpty()) {
                showError("Fill all the fields.");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                showError("Passwords doesn't match.");
                return;
            }
            currentUser.setPassword(newPassword);
        }
        userRepository.update(currentUser);
        patientRepository.update(currentUser);
        showSuccess();

        receiver = currentUser.getEmail();
        body += "<ul>"
                + "<li><b>Name:</b> " + currentUser.getName() + "</li>"
                + "<li><b>Last name:</b> " + currentUser.getLastName() + "</li>"
                + "<li><b>Date of birth:</b> " + currentUser.getDateBirth() + "</li>"
                + "<li><b>Place birth:</b> " + currentUser.getPlaceBirth() + "</li>"
                + "<li><b>Nationality:</b> " + currentUser.getNationality() + "</li>"
                + "<li><b>Sex:</b> " + currentUser.getSex() + "</li>"
                + "<li><b>Weight:</b> " + currentUser.getWeight() + "</li>"
                + "<li><b>CodiceFiscale:</b> " + currentUser.getCodiceFiscale() + "</li>"
                + "<li><b>Smoker:</b> " + (currentUser.getIsSmoker() ? "Yes" : "No") + "</li>"
                + "<li><b>Drinker:</b> " + (currentUser.getIsDrinker() ? "Yes" : "No") + "</li>"
                + "<li><b>Phone number:</b> " + currentUser.getTelNumber() + "</li>"
                + "<li><b>Email:</b> " + currentUser.getEmail() + "</li>"
                + "<li><b>Password:</b> " + newPassword + "</li>"
                + "</ul>"
                + "From,<br>VelyaLife staff</p>";

        EmailService.sendEmail(receiver, object, body);

        newPasswordField.clear();
        confirmPasswordField.clear();
        handleEnableEdit();
    }

    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToPatientDashboard();
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
    }

    private void showSuccess() {
        statusLabel.setText("Profile updated successfully");
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setVisible(true);
    }
}