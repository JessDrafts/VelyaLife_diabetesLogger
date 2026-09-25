package components;

import application.VelyaLifeApplication;
import controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViewNavigator {

    private static VelyaLifeController mainController;

    private static String authenticatedUser = null;
    private static String authenticatedRole = null;
    private static final Logger LOGGER = Logger.getLogger(ViewNavigator.class.getName());

    public static void setMainController(VelyaLifeController controller) {
        mainController = controller;
    }

    private static void loadViewWithController(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/" + fxml));
            Node view = loader.load();

            if (mainController != null) {
                mainController.setContent(view);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading view: " + fxml, e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while loading view: " + fxml, e);
        }
    }

    public static void navigateToHome() {
        loadViewWithController("HomeView.fxml");
    }
    public static void navigateToLogin() {
        loadViewWithController("LoginView.fxml");
    }
    public static void navigateToSignup() {
        loadViewWithController("SignupView.fxml");
    }

    // === AUTH MANAGEMENT & NAVBAR SYNC ===
    public static void setAuthenticatedUser(String username, String role) {
        authenticatedUser = username;
        authenticatedRole = role;
        if (mainController != null) {
            mainController.updateNavBar(isAuthenticated());
        }
    }

    public static String getAuthenticatedUser() {
        return authenticatedUser;
    }
    public static boolean isAuthenticated() {
        return authenticatedUser != null;
    }

    public static void logout() {
        authenticatedUser = null;
        authenticatedRole = null;
        if (mainController != null) {
            mainController.updateNavBar(false);
        }
        navigateToHome();
    }

    // === PROFILE ===
    public static void navigateToProfile() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        switch (authenticatedRole.toUpperCase()) {
            case "ADMIN":
                loadViewWithController("AdminProfileView.fxml");
                break;
            case "DOCTOR":
                loadViewWithController("DoctorProfileView.fxml");
                break;
            case "PATIENT":
                loadViewWithController("PatientProfileView.fxml");
                break;
            default:
                System.err.println("User role does not exist: " + authenticatedRole);
                navigateToHome();
        }
    }

    // === NOTIFICATIONS ===
    public static void navigateToNotifications() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/NotificationsView.fxml"));
            Node view = loader.load();

            loader.getController();

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Notifications view", e);
        }
    }

    // === ADMIN SET UP ===
    public static void navigateToAdminDashboard() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/AdminDashboardView.fxml"));
            Node view = loader.load();
            AdminDashboardController controller = loader.getController();
            controller.setUser(authenticatedUser);

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Admin Dashboard view", e);
        }
    }

    public static void navigateToPatientList() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/PatientListView.fxml"));
            Node view = loader.load();

            PatientListController controller = loader.getController();
            controller.setUser(authenticatedUser);

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Patient List view", e);
        }
    }

    public static void navigateToDoctorList() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/DoctorListView.fxml"));
            Node view = loader.load();

            DoctorListController controller = loader.getController();
            controller.setUser(authenticatedUser);

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Doctor List view", e);
        }
    }

    public static void navigateToMedLogHistory() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/MedLogHistoryView.fxml"));
            Node view = loader.load();

            MedLogHistoryController controller = loader.getController();
            controller.setUser(authenticatedUser);

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Doctor activity List view", e);
        }
    }

    // === PATIENT SET UP ===
    public static void navigateToPatientDashboard() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/PatientDashboardView.fxml"));
            Node view = loader.load();
            PatientDashboardController controller = loader.getController();
            controller.setUser(authenticatedUser);

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Patient Dashboard view", e);
        }
    }

    public static void navigateToPatientTherapy() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/PatientTherapyView.fxml"));
            Node view = loader.load();

            PatientTherapyController controller = loader.getController();
            controller.setUser(authenticatedUser);

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Patient's therapy view", e);
        }
    }

    public static void navigateToPatientReport() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/DailyLogView.fxml"));
            Node view = loader.load();

            DailyLogController controller = loader.getController();
            controller.setUser(authenticatedUser);

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Patient's report view", e);
        }
    }

    public static void navigateToInfoPage() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/InfoPageView.fxml"));
            Node view = loader.load();

            InfoPageController controller = loader.getController();
            controller.setUser(authenticatedUser);
            controller.initData(authenticatedUser);

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Contacts view", e);
        }
    }

    public static void navigateToPatientChart() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/PatientChartView.fxml"));
            Node view = loader.load();

            PatientChartController controller = loader.getController();
            controller.setUser(authenticatedUser);
            controller.loadPatientInfo(authenticatedUser);

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Patient's chart view", e);
        }
    }

    // === DOCTOR DASHBOARD SET UP ===
    public static void navigateToDoctorDashboard() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/DoctorDashboardView.fxml"));
            Node view = loader.load();
            DoctorDashboardController controller = loader.getController();
            controller.setUser(authenticatedUser);

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Doctor Dashboard view", e);
        }
    }


    public static void navigateToMyPatient() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/MyPatientListView.fxml"));
            Node view = loader.load();

            MyPatientListController controller = loader.getController();
            controller.setUser(authenticatedUser);

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading My Patient list view", e);
        }
    }

    public static void navigateToOtherPatient() {
        if (!isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/OtherPatientView.fxml"));
            Node view = loader.load();

            OtherPatientController controller = loader.getController();
            controller.setUser(authenticatedUser);

            mainController.setContent(view);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading all Patient list view", e);
        }
    }
}
