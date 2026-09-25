package controller;

import java.util.List;

import application.VelyaLifeApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import model.Patient;
import repository.PatientRepository;
import utility.EmailService;
import utility.UserService;
import components.ViewNavigator;

public class PatientListController {

    public AnchorPane sidebar;
    public Button registerButton;
    @FXML
    private TableView<Patient> patientTable;

    @FXML
    private TableColumn<Patient, String> nameColumn;

    @FXML
    private TableColumn<Patient, String> lastNameColumn;

    @FXML
    private TableColumn<Patient, String> usernameColumn;

    @FXML
    private TableColumn<Patient, String> emailColumn;

    @FXML
    private TableColumn<Patient, Void> actionColumn;

    private PatientRepository patientRepository;
    private final UserService userService = new UserService();

    @FXML
    private AdminSidebarController sidebarController;

    @FXML
    public void initialize() {
        patientRepository = VelyaLifeApplication.getPatientRepository();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        setupActionColumn();
        loadPatients();
    }

    public void setUser(String username) {
        sidebarController.setUser(username);
    }

    private void loadPatients() {
        List<Patient> patientsList = patientRepository.getAll();
        ObservableList<Patient> observablePatients = FXCollections.observableArrayList(patientsList);
        patientTable.setItems(observablePatients);
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.getStyleClass().add("buttons");
                deleteButton.setOnAction(_ -> {
                    Patient patient = getTableView().getItems().get(getIndex());
                    handleDeletePatient(patient);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void handleDeletePatient(Patient patient) {
        if (patient != null) {
            String receiver = patient.getEmail();
            String subject = "Account Deleted";
            String body = "<p>Dear " + patient.getName() + ",</p>"
                    + "<p>We are writing to inform you that your profile has been deleted from the VelyaLife system.</p>"
                    + "<p>If you believe this is an error, please contact the administrator.</p>"
                    + "<br><p>From,<br>VelyaLife staff</p>";

            userService.deleteUserFromDb(patient);

            if (receiver != null && !receiver.isBlank()) {
                EmailService.sendEmail(receiver, subject, body);
            }

            loadPatients();
        }
    }

    @FXML
    private void handleSignup() { ViewNavigator.navigateToSignup(); }
}