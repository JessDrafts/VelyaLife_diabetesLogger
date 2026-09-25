package controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.VelyaLifeApplication;
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
import javafx.stage.Stage;
import model.AbstractUser;
import model.MedLogHistory;
import repository.MedLogHistoryRepository;
import repository.UserRepository;
import components.ViewNavigator;

public class MedLogHistoryController {

    private static final Logger LOGGER = Logger.getLogger(MedLogHistoryController.class.getName());

    public AnchorPane sidebar;

    @FXML
    private TableView<MedLogHistory> medLogTable;

    @FXML
    private TableColumn<MedLogHistory, Integer> idColumn;

    @FXML
    private TableColumn<MedLogHistory, String> doctorNameColumn;

    @FXML
    private TableColumn<MedLogHistory, String> doctorLastNameColumn;

    @FXML
    private TableColumn<MedLogHistory, String> doctorCfColumn;

    @FXML
    private TableColumn<MedLogHistory, Void> actionColumn;

    @FXML
    private AdminSidebarController sidebarController;

    private MedLogHistoryRepository medLogRepository;

    @FXML
    public void initialize() {
        UserRepository userRepository = VelyaLifeApplication.getUserRepository();
        medLogRepository = VelyaLifeApplication.getMedLogHistoryRepository();
        String currentUsername = ViewNavigator.getAuthenticatedUser();
        AbstractUser user = userRepository.getById(currentUsername);

        if (user != null && user.getType().equalsIgnoreCase("Admin")) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            doctorNameColumn.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDoctor() != null ? cellData.getValue().getDoctor().getName() : ""));
            doctorLastNameColumn.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDoctor() != null ? cellData.getValue().getDoctor().getLastName() : ""));
            doctorCfColumn.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDoctor() != null ? cellData.getValue().getDoctor().getCodiceFiscale() : ""));

            setupActionColumn();
            loadMedLogs();
        }
    }

    public void setUser(String username) {
        if (sidebarController != null) {
            sidebarController.setUser(username);
        }
    }

    private void loadMedLogs() {
        List<MedLogHistory> medLogs = medLogRepository.getAll();
        ObservableList<MedLogHistory> observableLogs = FXCollections.observableArrayList(medLogs);
        medLogTable.setItems(observableLogs);
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button viewButton = new Button("View");

            {
                viewButton.getStyleClass().add("buttons");
                viewButton.setOnAction(_ -> {
                    MedLogHistory medLog = getTableView().getItems().get(getIndex());
                    handleView(medLog.getId());
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

    private void handleView(int id) {
        MedLogHistory medLog = medLogRepository.getById(String.valueOf(id));
        if (medLog != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MedLogDetailView.fxml"));
                Parent root = loader.load();

                MedLogDetailController controller = loader.getController();
                controller.setMedLog(medLog);

                Stage stage = new Stage();
                stage.setTitle("Report");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error loading MedLogDetailView.fxml", e);
            }
        } else {
            System.out.println("MedLog with ID " + id + " not found.");
        }
    }
}