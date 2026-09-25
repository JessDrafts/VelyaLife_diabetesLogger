package controller;

import components.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class HomeController {
    public VBox HomepageVBox;
    public ImageView logoImageView;
    public Label textLabel;
    @FXML
    private Button loginButton;
    
    @FXML
    public void initialize() {
        // If we're already authenticated, hide login button
        if (ViewNavigator.isAuthenticated()) {
            loginButton.setVisible(false);
        }
    }
    
    @FXML
    private void handleLogin() {
        ViewNavigator.navigateToLogin();
    }
}