package application;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import repository.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class VelyaLifeApplication extends Application {
    private static final UserRepository userRepository = new UserRepository();
    private static final DoctorRepository doctorRepository = new DoctorRepository();
    private static final PatientRepository patientRepository = new PatientRepository();
    private static final DailyLogRepository dailyLogRepository = new DailyLogRepository();
    private static final TherapyRepository therapyRepository = new TherapyRepository();
    private static final NotificationRepository notificationRepository = new NotificationRepository();
    private static final MedLogHistoryRepository medLogHistoryRepository = new MedLogHistoryRepository();
    private static final MedicalConditionRepository medicalConditionRepository = new MedicalConditionRepository();

    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/icon_noBg.png")));
        stage.getIcons().add(icon);
        FXMLLoader fxmlLoader = new FXMLLoader(VelyaLifeApplication.class.getResource("/fxml/VelyaLife_view.fxml"));

        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 800, 600);
        URL cssUrl = getClass().getResource("/style/styles.css");
        assert cssUrl != null;
        scene.getStylesheets().add(cssUrl.toExternalForm());

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

   public static UserRepository getUserRepository() {
        return userRepository;
    }
   public static DoctorRepository getDoctorRepository() {
        return doctorRepository;
    }
   public static PatientRepository getPatientRepository() { return patientRepository; }
   public static DailyLogRepository getDailyLogRepository() { return dailyLogRepository; }
   public static TherapyRepository getTherapyRepository() { return therapyRepository; }
   public static NotificationRepository getNotificationRepository() { return notificationRepository; }
   public static MedLogHistoryRepository getMedLogHistoryRepository() { return medLogHistoryRepository; }
   public static MedicalConditionRepository getMedicalConditionRepository() { return medicalConditionRepository; }
}
