package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import components.ViewNavigator;

public class DoctorSidebarController {

    @FXML
    private Label welcomeLabel;

    public void setUser(String username) {
        if (welcomeLabel != null) {
            welcomeLabel.setText(username);
        } else {
            System.err.println("welcomeLabel is null!");
        }
    }

    @FXML
    private void handleViewProfile() { ViewNavigator.navigateToProfile(); }

    @FXML
    private void handleMyPatient() { ViewNavigator.navigateToMyPatient(); }

    @FXML
    private void handleOtherPatient() { ViewNavigator.navigateToOtherPatient(); }
    
    @FXML
    private void handleNotification() { ViewNavigator.navigateToNotifications(); }

    @FXML
    private void handleDashboard() { ViewNavigator.navigateToDoctorDashboard(); }
    
    @FXML
    private void handleLogout() { ViewNavigator.logout(); }
}
