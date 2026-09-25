package controller;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Doctor;
import model.Patient;
import model.Therapy;

public class TherapyDetailController {

    private static final Logger LOGGER = Logger.getLogger(TherapyDetailController.class.getName());

    @FXML
    private TextField idField;

    @FXML
    private TextField patientField;

    @FXML
    private TextField patientDoctorField;

    @FXML
    private TextField drugField;

    @FXML
    private TextField dailyDoseField;

    @FXML
    private TextField amountField;

    @FXML
    private TextField startdateField;

    @FXML
    private TextField enddateField;

    @FXML
    private TextField prescDocField;

    @FXML
    private TextField instructionField;

    @FXML
    private Button closeButton;

    public void setTherapy(Therapy therapy) {
        if (therapy == null) {
            LOGGER.warning("Therapy is null.");
            return;
        }

        idField.setText(String.valueOf(therapy.getId()));

        Patient patient = therapy.getPatient();
        if (patient != null) {
            patientField.setText(patient.getName() + " " + patient.getLastName() + " (" + patient.getCodiceFiscale() + ")");

            Doctor refDoctor = patient.getRefDoctor();
            if (refDoctor != null) {
                patientDoctorField.setText("Dr. " + refDoctor.getName() + " " + refDoctor.getLastName() + " (" + refDoctor.getCodiceFiscale() + ")");
            } else {
                patientDoctorField.setText("N/A");
            }
        } else {
            patientField.setText("N/A");
            patientDoctorField.setText("N/A");
        }

        drugField.setText(therapy.getPrescription() != null ? therapy.getPrescription() : "");
        dailyDoseField.setText(String.valueOf(therapy.getDailyDose()));
        amountField.setText(String.valueOf(therapy.getAmountIntaken()));
        startdateField.setText(therapy.getStartDate() != null ? therapy.getStartDate() : "");
        enddateField.setText(therapy.getEndDate() != null ? therapy.getEndDate() : "");

        Doctor prescDoctor = therapy.getDoctor();
        if (prescDoctor != null) {
            prescDocField.setText("Dr. " + prescDoctor.getName() + " " + prescDoctor.getLastName() + " (" + prescDoctor.getCodiceFiscale() + ")");
        } else {
            prescDocField.setText("N/A");
        }

        instructionField.setText(therapy.getInstructions() != null ? therapy.getInstructions() : "");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}