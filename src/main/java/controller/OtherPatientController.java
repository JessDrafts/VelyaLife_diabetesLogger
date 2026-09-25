package controller;

import application.VelyaLifeApplication;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Doctor;
import model.Patient;
import repository.DoctorRepository;
import repository.PatientRepository;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OtherPatientController {
    private static final Logger LOGGER = Logger.getLogger(OtherPatientController.class.getName());

    @FXML
    public AnchorPane sidebar;
    @FXML
    private TableView<Patient> patientTable;
    @FXML
    private TableColumn<Patient, String> nameColumn;
    @FXML
    private TableColumn<Patient, String> lastNameColumn;
    @FXML
    private TableColumn<Patient, String> codiceFiscaleColumn;
    @FXML
    private TableColumn<Patient, Void> actionColumn;

    @FXML
    private DoctorSidebarController sidebarController;

    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private String doctorUsername;

    @FXML
    public void initialize() {
        patientRepository = VelyaLifeApplication.getPatientRepository();
        doctorRepository = VelyaLifeApplication.getDoctorRepository();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        codiceFiscaleColumn.setCellValueFactory(new PropertyValueFactory<>("codiceFiscale"));

        setupActionColumn();
    }

    public void setUser(String username) {
        this.doctorUsername = username;
        if (sidebarController != null) {
            sidebarController.setUser(username);
        }
        loadPatients();
    }

    private void loadPatients() {
        if (doctorUsername == null) return;

        Doctor loggedDoctor = doctorRepository.getById(doctorUsername);
        List<Patient> allPatients = patientRepository.getAll();

        if (allPatients == null) return;

        if (loggedDoctor != null) {
            String loggedDoctorCf = loggedDoctor.getCodiceFiscale();

            List<Patient> otherPatients = allPatients.stream()
                    .filter(patient -> {
                        Doctor refDoc = patient.getRefDoctor();

                        if (refDoc == null || refDoc.getCodiceFiscale() == null) {
                            return true;
                        }
                        return !refDoc.getCodiceFiscale().equalsIgnoreCase(loggedDoctorCf);
                    })
                    .collect(Collectors.toList());

            patientTable.setItems(FXCollections.observableArrayList(otherPatients));
        } else {
            patientTable.setItems(FXCollections.observableArrayList(allPatients));
        }
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button viewButton = new Button("View");

            {
                viewButton.getStyleClass().add("buttons");
                viewButton.setOnAction(_ -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    handleViewPatient(patient);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });
    }

    private void handleViewPatient(Patient patient) {
        if (patient == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DetailPatientView.fxml"));
            Parent root = loader.load();

            DetailPatientController controller = loader.getController();

            controller.setLoggedDoctorByUsername(doctorUsername);

            controller.setPatient(patient);

            Stage stage = new Stage();
            stage.setTitle("Patient Detail - " + patient.getName() + " " + patient.getLastName());
            stage.setScene(new Scene(root, 1000, 600));
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading DetailPatientView.fxml", e);
        }
    }
}