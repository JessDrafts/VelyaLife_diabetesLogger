package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import model.Notification;
import utility.NotificationService;
import utility.Observer;
import components.ViewNavigator;

import java.util.List;

public class PatientDashboardController implements Observer {

    public AnchorPane sidebar;
    @FXML
    private PatientSidebarController sidebarController;

    private String patientUsername;

    @FXML
    public void initialize() {
        NotificationService.getInstance();
    }

    public void setUser(String patient) {
        this.patientUsername = patient;
        if (sidebarController != null) {
            sidebarController.setUser(patient);
        }
        NotificationService.getInstance().addObserver(patient, this);
        checkAndShowReminders();
    }

    @Override
    public void update(Notification notification) {
        Platform.runLater(this::checkAndShowReminders);
    }

    private void checkAndShowReminders() {
        if (patientUsername == null) return;

        NotificationService service = NotificationService.getInstance();
        List<Notification> userNotifications = service.getNotificationsForUser(patientUsername);

        if (userNotifications == null) return;

        for (Notification notif : userNotifications) {
            if (notif.getSeen()) {
                continue;
            }

            String msg = notif.getMessage();
            if (msg != null) {
                String lowerMsg = msg.toLowerCase();

                if (lowerMsg.contains("therapy") || lowerMsg.contains("reminder") || lowerMsg.contains("medication") || lowerMsg.contains("terapia")) {

                    service.markAsSeen(notif);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("⏰ Medication Reminder");
                    alert.setHeaderText("Don't forget your therapy!");
                    alert.setContentText(msg);
                    alert.show();
                }
            }
        }
    }

    /* =========================================================================
     * MANAGE NAVIGATION
     * ========================================================================= */

    @FXML
    private void handleDailyLog() {
        NotificationService.getInstance().removeObserver(patientUsername);
        ViewNavigator.navigateToPatientReport();
    }

    @FXML
    private void handleContacts() {
        NotificationService.getInstance().removeObserver(patientUsername);
        ViewNavigator.navigateToInfoPage();
    }

    @FXML
    private void handleNotification() {
        NotificationService.getInstance().removeObserver(patientUsername);
        ViewNavigator.navigateToNotifications();
    }

    @FXML
    private void handleTherapy() {
        NotificationService.getInstance().removeObserver(patientUsername);
        ViewNavigator.navigateToPatientTherapy();
    }

    @FXML
    private void handleChart() {
        NotificationService.getInstance().removeObserver(patientUsername);
        ViewNavigator.navigateToPatientChart();
    }
}