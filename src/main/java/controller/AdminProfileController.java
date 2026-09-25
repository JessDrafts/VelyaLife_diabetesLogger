package controller;

import application.VelyaLifeApplication;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import model.AbstractUser;
import repository.UserRepository;
import utility.EmailService;
import components.ViewNavigator;

public class AdminProfileController {
    
	@FXML
    private Label usernameLabel;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Label statusLabel;
    
    private UserRepository userRepository;
    private String currentUsername;
    
    @FXML
    public void initialize() {
        userRepository = VelyaLifeApplication.getUserRepository();
        currentUsername = ViewNavigator.getAuthenticatedUser();

        usernameLabel.setText(currentUsername);
        statusLabel.setVisible(false);
    }
    
    @FXML
    private void handleUpdatePassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String receiver;
        String object = "Profile Update";
        String body = "<p>Dear Admin,</p><p>Your password has been changed.</p>";
        
        // Validation
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Fill all the fields.");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords doesn't match.");
            return;
        }
        
        // Update the user with the new password
        AbstractUser currentUser = userRepository.getById(currentUsername);
        currentUser.setPassword(newPassword);
        userRepository.update(currentUser);
        
        showSuccess();
        
        receiver = currentUser.getEmail();
        body += "<ul>"
     	       + "<li><b>Username:</b> " + currentUser.getUsername() + "</li>"
     	       + "<li><b>Password:</b> " + (currentUser.getPlainPassword() == null || currentUser.getPlainPassword().isEmpty() ? "no changes were made" : currentUser.getPlainPassword()) + "</li>"
     	       + "</ul>"
     	       + "From,<br>"
     	       + "VelyaLife staff"
     	       + "</p>";
        EmailService.sendEmail(receiver, object, body);
        
        // Clear fields
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
    
    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.navigateToAdminDashboard();
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setVisible(true);
    }
    
    private void showSuccess() {
        statusLabel.setText("Password changed successfully.");
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setVisible(true);
    }
}
