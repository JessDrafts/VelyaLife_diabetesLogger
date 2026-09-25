package controller;

import application.VelyaLifeApplication;
import model.AbstractUser;
import repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import javafx.scene.control.TextField;
import components.ViewNavigator;

public class LoginController {
    
	@FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private TextField visiblePasswordField;
    
    @FXML
    private CheckBox showPasswordBtn;
    
    @FXML
    private ChoiceBox<String> UserTypeField;
    private final String[] userTypes = {"Select", "Admin", "Doctor", "Patient"};

    @FXML
    private Label statusLabel;

    private UserRepository userRepository;

    @FXML
    public void initialize() {
    	UserTypeField.getItems().addAll(userTypes);
        UserTypeField.setValue("Select");
        userRepository = VelyaLifeApplication.getUserRepository();
        statusLabel.setVisible(false);
        
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        // Listener for the checkbox
        showPasswordBtn.selectedProperty().addListener((_, _, isSelected) -> {
            if (isSelected) {
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
            } else {
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
            }
        });
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String selectedRole = UserTypeField.getValue();

        if (username.isEmpty() || password.isEmpty() || selectedRole == null || selectedRole.equals("Select")) {
            showError("Insert username, password and role");
            return;
        }

        AbstractUser user = userRepository.getById(username);
        if (user != null && user.checkPassword(password) && user.checkUsername(username)) {
            if (user.getType().equalsIgnoreCase(selectedRole)) {
                // save logged user
                ViewNavigator.setAuthenticatedUser(user.getUsername(), user.getType());

                // change context based on user type
                switch (user.getType().toUpperCase()) {
                    case "ADMIN":
                    	ViewNavigator.setAuthenticatedUser(user.getUsername(), user.getType());
                        ViewNavigator.navigateToAdminDashboard();
                        break;
                    case "DOCTOR":
                    	ViewNavigator.setAuthenticatedUser(user.getUsername(), user.getType());
                        ViewNavigator.navigateToDoctorDashboard();
                        break;
                    case "PATIENT":
                    	ViewNavigator.setAuthenticatedUser(user.getUsername(), user.getType());
                        ViewNavigator.navigateToPatientDashboard();
                        break;
                    default:
                        showError("User type not found");
                }
            } else {
                showError("Wrong user");
            }
        } else {
            showError("Username o password not valid");
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
    }
}
