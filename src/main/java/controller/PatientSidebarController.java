package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import components.ViewNavigator;

public class PatientSidebarController {

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
    private void handleViewProfile() {
        ViewNavigator.navigateToProfile();
    }

    @FXML
    private void handleDailyLog() {
        ViewNavigator.navigateToPatientReport();
    }

    @FXML
    private void handleContacts() {
        ViewNavigator.navigateToInfoPage();
    }
    
    @FXML
    private void handleNotification() {
        ViewNavigator.navigateToNotifications();
    }

    @FXML
    private void handleDashboard() {
        ViewNavigator.navigateToPatientDashboard();
    }
    
    @FXML
    private void handleLogout() {
        ViewNavigator.logout();
    }
    
    @FXML
    private void handleTherapy() {
        ViewNavigator.navigateToPatientTherapy();
    }

    @FXML
    private void handleChart() {
        ViewNavigator.navigateToPatientChart();
    }
}
