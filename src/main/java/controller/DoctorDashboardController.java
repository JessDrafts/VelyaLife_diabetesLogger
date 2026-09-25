package controller;

import application.VelyaLifeApplication;
import components.ViewNavigator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import model.DailyLog;
import model.Notification;
import model.Patient;
import model.Therapy;
import repository.DailyLogRepository;
import repository.PatientRepository;
import repository.TherapyRepository;
import utility.NotificationService;
import utility.Observer;

import java.util.List;
import java.util.Optional;

public class DoctorDashboardController implements Observer {

    public AnchorPane sidebar;
    @FXML
    private DoctorSidebarController sidebarController;

    private String doctorUsername;
    private PatientRepository patientRepository;
    private TherapyRepository therapyRepository;
    private DailyLogRepository dailyLogRepository;

    @FXML
    public void initialize() {
        NotificationService.getInstance();
        patientRepository = VelyaLifeApplication.getPatientRepository();
        therapyRepository = VelyaLifeApplication.getTherapyRepository();
        dailyLogRepository = VelyaLifeApplication.getDailyLogRepository();
    }

    public void setUser(String username) {
        this.doctorUsername = username;
        if (sidebarController != null) {
            sidebarController.setUser(username);
        }

        checkTherapyComplianceForAssignedPatients();
        NotificationService.getInstance().addObserver(username, this);
        checkAndShowAlerts();
    }

    @Override
    public void update(Notification notification) {
        Platform.runLater(this::checkAndShowAlerts);
    }

    private void checkTherapyComplianceForAssignedPatients() {
        if (doctorUsername == null || patientRepository == null) return;

        List<Patient> allPatients = patientRepository.getAll();
        if (allPatients == null) return;

        List<Patient> assignedPatients = allPatients.stream()
                .filter(p -> p != null && p.getRefDoctor() != null &&
                        (doctorUsername.equalsIgnoreCase(p.getRefDoctor().getCodiceFiscale()) ||
                                doctorUsername.equalsIgnoreCase(p.getRefDoctor().getUsername())))
                .toList();
        List<Notification> existingNotifs = NotificationService.getInstance().getNotificationsForUser(doctorUsername);

        for (Patient patient : assignedPatients) {
            if (patient == null) continue;

            boolean alreadyNotified = existingNotifs != null && existingNotifs.stream().anyMatch(n ->
                    (!n.getSeen()) &&
                            n.getMessage() != null &&
                            n.getMessage().contains(patient.getName()) &&
                            n.getMessage().contains(patient.getLastName()) &&
                            n.getMessage().toLowerCase().contains("has not followed therapy")
            );

            if (!alreadyNotified) {
                List<Therapy> therapies = therapyRepository.getByPatient(patient.getUsername());
                List<DailyLog> allLogs = dailyLogRepository.getByPatient(patient.getUsername());

                NotificationService.getInstance().verifyAndNotifyTherapyCompliance(patient, therapies, allLogs);
            }
        }
    }
    private void checkAndShowAlerts() {
        NotificationService service = NotificationService.getInstance();

        List<Notification> unhandledGlucose = service.getUnhandledGlucoseNotifications();
        if (unhandledGlucose != null) {
            for (Notification notif : unhandledGlucose) {
                showAlertPopup(
                        "⚠ ALERT GLYCEMIC RISK",
                        notif.getMessage(),
                        () -> service.claimGlucoseNotification(notif.getId(), doctorUsername)
                );
            }
        }

        List<Notification> userNotifications = service.getNotificationsForUser(doctorUsername);
        if (userNotifications != null) {
            for (Notification notif : userNotifications) {
                boolean isUnseen = (!notif.getSeen());
                String msg = notif.getMessage();

                if (isUnseen && msg != null && msg.toLowerCase().contains("has not followed therapy for 3 consecutive days")) {
                    showAlertPopup(
                            "⚠ ALERT THERAPY NOT FOLLOWED",
                            msg,
                            () -> service.markAsSeen(notif)
                    );
                }
            }
        }
    }

    private void showAlertPopup(String title, String content, Runnable onClaimAction) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText("Attention: medical attention needed!");
        alert.setContentText(content + "\n\nWould you like to deal with this medical situation?");

        ButtonType btnClaim = new ButtonType("Yes");
        ButtonType btnIgnore = new ButtonType("No");
        alert.getButtonTypes().setAll(btnClaim, btnIgnore);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == btnClaim) {
            onClaimAction.run();
        }
    }

    /* =========================================================================
     * MANAGE NAVIGATION
     * ========================================================================= */

    @FXML
    private void handleMyPatient() {
        NotificationService.getInstance().removeObserver(doctorUsername);
        ViewNavigator.navigateToMyPatient();
    }

    @FXML
    private void handleOtherPatient() {
        NotificationService.getInstance().removeObserver(doctorUsername);
        ViewNavigator.navigateToOtherPatient();
    }

    @FXML
    private void handleNotification() {
        NotificationService.getInstance().removeObserver(doctorUsername);
        ViewNavigator.navigateToNotifications();
    }
}