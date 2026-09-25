package controller;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.VelyaLifeApplication;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.MedicalCondition;
import model.Patient;
import repository.MedicalConditionRepository;
import repository.UserRepository;

public class MedicalConditionFormController {

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField startDateField;

    @FXML
    private TextField endDateField;

    private String patientUsername;
    private MedicalConditionRepository medicalConditionRepository;
    private UserRepository userRepository;
    private static final Logger LOGGER = Logger.getLogger(MedicalConditionFormController.class.getName());


    @FXML
    public void initialize() {
        medicalConditionRepository = VelyaLifeApplication.getMedicalConditionRepository();
        userRepository = VelyaLifeApplication.getUserRepository();

        typeComboBox.setItems(FXCollections.observableArrayList("Symptom", "Pathologies", "Other_therapies"));
    }

    public void setUser(String username) {
        this.patientUsername = username;
    }

    @FXML
    private void handleSave() {
        try {
            String type = typeComboBox.getValue();
            String name = nameField.getText();
            String description = descriptionField.getText();
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();

            Patient patient = (Patient) userRepository.getById(patientUsername);

            MedicalCondition condition = new MedicalCondition(
                    name,
                    description,
                    type != null ? type : "Symptom",
                    startDate,
                    endDate.isEmpty() ? null : endDate,
                    LocalDateTime.now(),
                    patient
            );

            medicalConditionRepository.save(condition);

            // close
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while loading Medical condition form: ", e);
        }
    }
}