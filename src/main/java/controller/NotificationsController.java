package controller;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import model.AbstractUser;
import model.Doctor;
import model.Notification;
import model.Patient;
import repository.NotificationRepository;
import repository.UserRepository;

public class NotificationsController {

    private static final Logger LOGGER = Logger.getLogger(NotificationsController.class.getName());

    @FXML
    private StackPane sidebarContainer;

    @FXML
    private TableView<Notification> notificationTable;

    @FXML
    private TableColumn<Notification, String> dateColumn;

    @FXML
    private TableColumn<Notification, String> senderColumn;

    @FXML
    private TableColumn<Notification, String> messageColumn;

    @FXML
    private TableColumn<Notification, Void> actionColumn;

    private NotificationRepository notificationRepository;
    private String currentUsername;
    private AbstractUser user;

    @FXML
    public void initialize() {
        UserRepository userRepository = VelyaLifeApplication.getUserRepository();
        notificationRepository = VelyaLifeApplication.getNotificationRepository();
        currentUsername = ViewNavigator.getAuthenticatedUser();
        user = userRepository.getById(currentUsername);

        if (user != null) {
            loadSidebarBasedOnUserType();
            setupTableColumns();
            loadNotifications();
        }
    }

    private void loadSidebarBasedOnUserType() {
        try {
            if (user == null) return;

            String fxmlPath;
            if (user instanceof Patient) {
                fxmlPath = "/fxml/PatientSidebarView.fxml";
            } else if (user instanceof Doctor) {
                fxmlPath = "/fxml/DoctorSidebarView.fxml";
            } else {
                return;
            }

            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("File FXML not found: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Node sidebar = loader.load();

            if (user instanceof Patient) {
                PatientSidebarController sidebarController = loader.getController();
                sidebarController.setUser(user.getUsername());
            } else if (user instanceof Doctor) {
                DoctorSidebarController sidebarController = loader.getController();
                sidebarController.setUser(user.getUsername());
            }

            sidebarContainer.getChildren().setAll(sidebar);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading sidebar for user: " + currentUsername, e);
        }
    }

    private void setupTableColumns() {

        dateColumn.setCellValueFactory(cellData -> {
            Notification n = cellData.getValue();
            String formattedDate = (n.getCreatedAt() != null) ? n.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
            return new SimpleStringProperty(formattedDate);
        });

        senderColumn.setCellValueFactory(cellData -> {
            Notification notification = cellData.getValue();
            if (notification != null && notification.getSender() != null) {
                return new SimpleStringProperty(notification.getSender().getUsername());
            }
            return new SimpleStringProperty("System");
        });

        messageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessage()));

        actionColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button btnView = new Button("View");
            private final Button btnRead = new Button("Read");
            private final HBox pane = new HBox(10, btnView, btnRead);

            {
                btnView.getStyleClass().add("buttons");
                btnRead.getStyleClass().add("buttons");

                btnView.setOnAction(_ -> {
                    Notification notification = getTableView().getItems().get(getIndex());
                    handleShowNotification(notification);
                });

                btnRead.setOnAction(_ -> {
                    Notification notification = getTableView().getItems().get(getIndex());
                    handleMarkAsRead(notification);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Notification notification = getTableView().getItems().get(getIndex());
                    if (notification.getSeen()) {
                        pane.getChildren().setAll(btnView);
                    } else {
                        pane.getChildren().setAll(btnView, btnRead);
                    }
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadNotifications() {
        try {
            List<Notification> notifications = notificationRepository.getNotificationsForUser(currentUsername);
            ObservableList<Notification> notificationList = FXCollections.observableArrayList(notifications);
            notificationTable.setItems(notificationList);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading notifications for user: " + currentUsername, e);
        }
    }

    private void handleShowNotification(Notification notification) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message Detail");
            alert.setHeaderText("ID: " + notification.getId());
            alert.setContentText("Date: " + notification.getCreatedAt() +
                    "\nFrom: " + (notification.getSender().getUsername().equalsIgnoreCase("Admin") ? notification.getSender().getUsername() : "System") +
                    "\n\nMessage:\n" + notification.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing notification detail ID: " + notification.getId(), e);
        }
    }

    private void handleMarkAsRead(Notification notification) {
        try {
            notification.setSeen(true);
            notificationRepository.update(notification);
            loadNotifications();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error marking notification as read ID: " + notification.getId(), e);
        }
    }
}