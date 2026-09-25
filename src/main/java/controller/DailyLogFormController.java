package controller;

import java.time.LocalDateTime;
import java.util.List;

import application.VelyaLifeApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DailyLog;
import model.Patient;
import repository.DailyLogRepository;
import repository.TherapyRepository;
import repository.UserRepository;
import model.Therapy;
import utility.Help_DailyLog;
import utility.NotificationService;

public class DailyLogFormController {

    @FXML
    private TextField bloodSugarField;

    @FXML
    private CheckBox beforeMealCheckBox;

    @FXML
    private ComboBox<String> drugComboBox;

    @FXML
    private TextField amountField;

    private String patientUsername;
    private DailyLogRepository dailyLogRepository;
    private TherapyRepository therapyRepository;
    private UserRepository userRepository;

    @FXML
    public void initialize() {
        dailyLogRepository = VelyaLifeApplication.getDailyLogRepository();
        therapyRepository = VelyaLifeApplication.getTherapyRepository();
        userRepository = VelyaLifeApplication.getUserRepository();
    }

    public void setUser(String username) {
        this.patientUsername = username;
        loadPrescribedDrugs();
    }

    private void loadPrescribedDrugs() {
        if (patientUsername == null) return;

        // get patient therapy
        List<Therapy> therapies = therapyRepository.getByPatient(patientUsername);
        ObservableList<String> drugs = FXCollections.observableArrayList();

        for (Therapy t : therapies) {
            if (t.getPrescription() != null && !drugs.contains(t.getPrescription())) {
                drugs.add(t.getPrescription());
            }
        }

        drugComboBox.setItems(drugs);
    }

    @FXML
    private void handleSave() {
        try {
            double bloodSugar = Double.parseDouble(bloodSugarField.getText());
            boolean beforeMeal = beforeMealCheckBox.isSelected();
            String selectedDrug = drugComboBox.getValue();
            int amount = amountField.getText().isEmpty() ? 0 : Integer.parseInt(amountField.getText());

            Patient patient = (Patient) userRepository.getById(patientUsername);

            DailyLog log = new DailyLog(
                    0,
                    bloodSugar,
                    patient,
                    LocalDateTime.now(),
                    beforeMeal,
                    selectedDrug != null ? selectedDrug : "None",
                    amount
            );

            dailyLogRepository.save(log);

            // glycemic level check
            String severity = Help_DailyLog.evaluateGlucoseSeverity(bloodSugar, beforeMeal);
            if (!"NORMAL".equalsIgnoreCase(severity)) {
                NotificationService.getInstance().notifyGlucoseOutOfRange(patient, bloodSugar, severity);
            }

            List<Therapy> therapies = therapyRepository.getByPatient(patientUsername);
            List<DailyLog> allLogs = dailyLogRepository.getByPatient(patientUsername);
            List<DailyLog> todayLogs = Help_DailyLog.filterByDate(allLogs, java.time.LocalDate.now());

            // A. check therapy compliance for three days - for doctor
            NotificationService.getInstance().verifyAndNotifyTherapyCompliance(patient, therapies, allLogs);

            // B. check medication - for patient
            NotificationService.getInstance().checkAndSendPatientTherapyReminder(patient, therapies, todayLogs);

            Stage stage = (Stage) bloodSugarField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            System.err.println("Error number format.");
        }
    }
}