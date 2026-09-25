package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.VelyaLifeApplication;
import components.ViewNavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.AbstractUser;
import model.Therapy;
import repository.TherapyRepository;
import repository.UserRepository;

public class PatientTherapyController {

    private static final Logger LOGGER = Logger.getLogger(PatientTherapyController.class.getName());
    public AnchorPane sidebar;

    @FXML
    private VBox therapyManagementSection;

    @FXML
    private TableView<Therapy> therapyTable;

    @FXML
    private TableColumn<Therapy, Integer> idColumn;

    @FXML
    private TableColumn<Therapy, String> nameColumn;

    @FXML
    private TableColumn<Therapy, String> statusColumn;

    @FXML
    private TableColumn<Therapy, Void> actionColumn;

    @FXML
    private PatientSidebarController sidebarController;

    private TherapyRepository therapyRepo;
    private String currentUsername;

    @FXML
    public void initialize() {
        try {
            UserRepository userRepository = VelyaLifeApplication.getUserRepository();
            therapyRepo = VelyaLifeApplication.getTherapyRepository();
            currentUsername = ViewNavigator.getAuthenticatedUser();
            AbstractUser user = userRepository.getById(currentUsername);

            if (user != null && "Patient".equalsIgnoreCase(user.getType())) {
                therapyManagementSection.setVisible(true);
                therapyManagementSection.setManaged(true);

                setupTableColumns();
                loadTherapies();
            } else {
                LOGGER.warning("User not found or not authorized: " + currentUsername);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during PatientTherapy controller initialize", e);
        }
        setupTableColumns();
        loadTherapies();
    }

    public void setUser(String username) {
        if (sidebarController != null) {
            sidebarController.setUser(username);
        } else {
            LOGGER.warning("sidebarController is null.");
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("prescription"));

        // calculate "Active" state
        statusColumn.setCellValueFactory(cellData -> {
            Therapy therapy = cellData.getValue();
            String endDateStr = therapy.getEndDate();

            if (endDateStr == null || endDateStr.isBlank()) {
                return new SimpleStringProperty("Active");
            }
            try {
                LocalDate endDate = LocalDate.parse(endDateStr);
                if (endDate.isBefore(LocalDate.now())) {
                    return new SimpleStringProperty("Ended");
                }
            } catch (DateTimeParseException e) {
                LOGGER.fine("Could not parse end date: " + endDateStr);
            }
            return new SimpleStringProperty("Active");
        });

        setupActionColumn();
    }

    private void loadTherapies() {
        List<Therapy> therapies = therapyRepo.getByPatient(currentUsername);
        if (therapies != null) {
            ObservableList<Therapy> observableTherapies = FXCollections.observableArrayList(therapies);
            therapyTable.setItems(observableTherapies);
        }
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button detailsButton = new Button("View");

            {
                detailsButton.getStyleClass().add("buttons");
                detailsButton.setOnAction(_ -> {
                    Therapy therapy = getTableView().getItems().get(getIndex());
                    if (therapy != null) {
                        handleShowTherapy(String.valueOf(therapy.getId()));
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        });
    }

    private void handleShowTherapy(String id) {
        Therapy therapy = therapyRepo.getById(id);
        if (therapy != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TherapyDetailView.fxml"));
                Parent root = loader.load();

                TherapyDetailController controller = loader.getController();
                controller.setTherapy(therapy);

                Stage stage = new Stage();
                stage.setTitle("Therapy Detail");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error loading TherapyDetailView.fxml for ID: " + id, e);
            }
        } else {
            LOGGER.warning("Therapy with ID: " + id + " not found.");
        }
    }
}