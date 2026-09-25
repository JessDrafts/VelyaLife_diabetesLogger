package controller;

import application.VelyaLifeApplication;
import components.ViewNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import model.Doctor;
import repository.DoctorRepository;
import utility.EmailService;
import utility.UserService;

import java.util.List;

public class DoctorListController {

    public AnchorPane sidebar;
    public Button registerButton;
    @FXML
    private TableView<Doctor> doctorTable;

    @FXML
    private TableColumn<Doctor, String> nameColumn;

    @FXML
    private TableColumn<Doctor, String> lastNameColumn;

    @FXML
    private TableColumn<Doctor, String> usernameColumn;

    @FXML
    private TableColumn<Doctor, String> emailColumn;

    @FXML
    private TableColumn<Doctor, Void> actionColumn;

    private DoctorRepository doctorRepository;
    private final UserService userService = new UserService();

    @FXML
    private AdminSidebarController sidebarController;

    @FXML
    public void initialize() {
        doctorRepository = VelyaLifeApplication.getDoctorRepository();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        setupActionColumn();
        loadDoctors();
    }

    public void setUser(String username) {
        sidebarController.setUser(username);
    }

    private void loadDoctors() {
        List<Doctor> doctorsList = doctorRepository.getAll();
        ObservableList<Doctor> observableDoctors = FXCollections.observableArrayList(doctorsList);
        doctorTable.setItems(observableDoctors);
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.getStyleClass().add("buttons");
                deleteButton.setOnAction(_ -> {
                    Doctor doctor = getTableView().getItems().get(getIndex());
                    handleDeleteDoctor(doctor);
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

    private void handleDeleteDoctor(Doctor doctor) {
        if (doctor != null) {
            String receiver = doctor.getEmail();
            String subject = "Account Deleted";
            String body = "<p>Dear " + doctor.getName() + ",</p>"
                    + "<p>We are writing to inform you that your profile has been deleted from the VelyaLife system.</p>"
                    + "<p>If you believe this is an error, please contact the administrator.</p>"
                    + "<br><p>From,<br>VelyaLife staff</p>";

            userService.deleteUserFromDb(doctor);

            if (receiver != null && !receiver.isBlank()) {
                EmailService.sendEmail(receiver, subject, body);
            }

            loadDoctors();
        }
    }

    @FXML
    private void handleSignup() {
        ViewNavigator.navigateToSignup();
    }
}