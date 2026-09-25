package controller;

import javafx.fxml.FXML;
import components.ViewNavigator;
import javafx.scene.layout.AnchorPane;

public class AdminDashboardController {

    public AnchorPane sidebar;
    @FXML
    private AdminSidebarController sidebarController;

    public void setUser(String username) {
        sidebarController.setUser(username);
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
    private void handleDoctorActivityLog() {
        ViewNavigator.navigateToMedLogHistory();
    }
 
}
