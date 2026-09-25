package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import components.ViewNavigator;

public class AdminSidebarController {

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
    private void handlePatientList() {
        ViewNavigator.navigateToPatientList();
    }

    @FXML
    private void handleDoctorList() {
        ViewNavigator.navigateToDoctorList();
    }
    
    @FXML
    private void handleDoctorActivityLog() { ViewNavigator.navigateToMedLogHistory(); }
    
    @FXML
    private void handleDashboard() {
        ViewNavigator.navigateToAdminDashboard();
    }
    
    @FXML
    private void handleLogout() {
        ViewNavigator.logout();
    }
}
