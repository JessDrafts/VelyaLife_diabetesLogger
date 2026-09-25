package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.VelyaLifeApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.AbstractUser;
import model.DailyLog;
import model.MedicalCondition;
import model.Patient;
import model.Therapy;
import repository.DailyLogRepository;
import repository.MedicalConditionRepository;
import repository.TherapyRepository;
import repository.UserRepository;
import utility.Help_DailyLog;
import utility.NotificationService;
import components.ViewNavigator;

public class DailyLogController {

    private static final Logger LOGGER = Logger.getLogger(DailyLogController.class.getName());
    public AnchorPane sidebar;

    @FXML
    private VBox reportManagementSection;

    @FXML
    private VBox dailyLogListContainer;

    @FXML
    private VBox medicalConditionListContainer;

    @FXML
    private PatientSidebarController sidebarController;

    private String currentUsername;
    private DailyLogRepository dailyLogRepository;
    private MedicalConditionRepository medicalConditionRepository;

    @FXML
    public void initialize() {
        UserRepository userRepository = VelyaLifeApplication.getUserRepository();
        currentUsername = ViewNavigator.getAuthenticatedUser();
        AbstractUser user = userRepository.getById(currentUsername);
        dailyLogRepository = VelyaLifeApplication.getDailyLogRepository();
        medicalConditionRepository = VelyaLifeApplication.getMedicalConditionRepository();
        TherapyRepository therapyRepository = VelyaLifeApplication.getTherapyRepository();

        if (user != null && user.getType().equalsIgnoreCase("Patient")) {
            if (reportManagementSection != null) {
                reportManagementSection.setVisible(true);
                reportManagementSection.setManaged(true);
            }

            populateDailyLogList();
            populateMedicalConditionList();

            if (user instanceof Patient patient) {
                List<Therapy> therapies = therapyRepository.getByPatient(currentUsername);
                List<DailyLog> allLogs = dailyLogRepository.getByPatient(currentUsername);
                List<DailyLog> todayLogs = Help_DailyLog.filterByDate(allLogs, LocalDate.now());

                // 1. Remainder for the day
                NotificationService.getInstance().checkAndSendPatientTherapyReminder(patient, therapies, todayLogs);
            }
        }
    }

    public void setUser(String username) {
        if (sidebarController != null) {
            sidebarController.setUser(username);
        }
    }

    private void populateDailyLogList() {
        if (dailyLogListContainer == null) return;
        dailyLogListContainer.getChildren().clear();

        List<DailyLog> logs = dailyLogRepository.getByPatient(currentUsername);

        if (logs.isEmpty()) {
            dailyLogListContainer.getChildren().add(new Label("No log present."));
            return;
        }

        for (DailyLog log : logs) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("card");

            Label infoLabel = new Label("ID: " + log.getId() + " | Date: " + log.getCreatedAt());
            infoLabel.setPrefWidth(350);

            Button btnView = new Button("View");
            btnView.getStyleClass().add("buttons");
            btnView.setOnAction(_ -> handleShowDailyLog(String.valueOf(log.getId())));

            Button btnDelete = new Button("Delete");
            btnDelete.getStyleClass().add("buttons");
            btnDelete.setOnAction(_ -> {
                try {
                    dailyLogRepository.delete(String.valueOf(log.getId()));
                    populateDailyLogList();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error deleting daily log ID: " + log.getId(), ex);
                }
            });

            row.getChildren().addAll(infoLabel, btnView, btnDelete);
            dailyLogListContainer.getChildren().add(row);
        }
    }

    private void populateMedicalConditionList() {
        if (medicalConditionListContainer == null) return;
        medicalConditionListContainer.getChildren().clear();

        List<MedicalCondition> conditions = medicalConditionRepository.getByPatient(currentUsername);

        if (conditions.isEmpty()) {
            medicalConditionListContainer.getChildren().add(new Label("No medical condition registered."));
            return;
        }

        for (MedicalCondition cond : conditions) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("card");

            Label infoLabel = new Label("ID: " + cond.getId() + " | Type: " + cond.getType() + " | Start: " + cond.getStart());
            infoLabel.setPrefWidth(350);

            Button btnView = new Button("View");
            btnView.getStyleClass().add("buttons");
            btnView.setOnAction(_ -> handleShowMedicalCondition(String.valueOf(cond.getId())));

            Button btnDelete = new Button("Delete");
            btnDelete.getStyleClass().add("buttons");
            btnDelete.setOnAction(_ -> {
                try {
                    medicalConditionRepository.delete(String.valueOf(cond.getId()));
                    populateMedicalConditionList();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error deleting medical condition ID: " + cond.getId(), ex);
                }
            });

            row.getChildren().addAll(infoLabel, btnView, btnDelete);
            medicalConditionListContainer.getChildren().add(row);
        }
    }

    private void handleShowDailyLog(String id) {
        try {
            DailyLog log = dailyLogRepository.getById(id);
            if (log != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Glycemic Log Detail");
                alert.setHeaderText("ID: " + log.getId());
                alert.setContentText("Date: " + log.getCreatedAt() +
                        "\nGlycemia: " + log.getBloodSugarLevel() + " mg/dL" +
                        "\nBefore meal: " + (log.getBeforeMeal() ? "Yes" : "No") +
                        "\nMedicine: " + log.getDrugs() + " (Quantity: " + log.getAmountIntaken() + ")");
                alert.showAndWait();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing daily log ID: " + id, e);
        }
    }

    private void handleShowMedicalCondition(String id) {
        try {
            MedicalCondition cond = medicalConditionRepository.getById(id);
            if (cond != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Medical Condition Detail");
                alert.setHeaderText("ID: " + cond.getId() + " (" + cond.getType() + ")");
                alert.setContentText("Name: " + cond.getName() +
                        "\n Description: " + cond.getDescription() +
                        "\nStart: " + cond.getStart() +
                        (cond.getEnd() != null ? ("\nEnd: " + cond.getEnd()) : null));
                alert.showAndWait();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing medical condition ID: " + id, e);
        }
    }

    @FXML
    private void handleLog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DailyLogFormView.fxml"));
            Parent root = loader.load();

            DailyLogFormController controller = loader.getController();
            controller.setUser(currentUsername);

            Stage stage = new Stage();
            stage.setTitle("New Report");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            populateDailyLogList();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading DailyLogFormView.fxml", e);
        }
    }

    @FXML
    private void handleNewCondition() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MedicalConditionFormView.fxml"));
            Parent root = loader.load();

            MedicalConditionFormController controller = loader.getController();
            controller.setUser(currentUsername);

            Stage stage = new Stage();
            stage.setTitle("New Medical Condition");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            populateMedicalConditionList();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading MedicalConditionFormView.fxml", e);
        }
    }
}