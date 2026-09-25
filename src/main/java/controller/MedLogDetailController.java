package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.MedLogHistory;
import java.time.format.DateTimeFormatter;

public class MedLogDetailController {

    public Button closeButton;
    @FXML
    private TextField idField;

    @FXML
    private TextField patientField;

    @FXML
    private TextField doctorField;

    @FXML
    private TextField patientDoctorField;

    @FXML
    private TextField dateField;

    @FXML
    private TextArea contentArea;

    public void setMedLog(MedLogHistory medLog) {
        if (medLog != null) {
            idField.setText(String.valueOf(medLog.getId()));

            if (medLog.getPatient() != null) {
                patientField.setText(medLog.getPatient().getName() + " " + medLog.getPatient().getLastName() + " (" + medLog.getPatient().getCodiceFiscale() + ")");

                if (medLog.getPatient().getRefDoctor() != null) {
                    patientDoctorField.setText(medLog.getPatient().getRefDoctor().getCodiceFiscale());
                } else {
                    patientDoctorField.setText("N/A");
                }
            } else {
                patientField.setText("N/A");
                patientDoctorField.setText("N/A");
            }

            if (medLog.getDoctor() != null) {
                doctorField.setText(medLog.getDoctor().getName() + " " + medLog.getDoctor().getLastName() + " (" + medLog.getDoctor().getCodiceFiscale() + ")");
            } else {
                doctorField.setText("N/A");
            }

            if (medLog.getModifiedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                dateField.setText(medLog.getModifiedAt().format(formatter));
            } else {
                dateField.setText("N/A");
            }

            contentArea.setText(medLog.getContent() != null ? medLog.getContent() : "N/A");
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }
}