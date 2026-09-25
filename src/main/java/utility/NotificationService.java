package utility;

import application.VelyaLifeApplication;
import model.*;
import repository.NotificationRepository;
import repository.PatientRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationService implements Subject {

    private static NotificationService instance;
    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());

    private final NotificationRepository notificationRepo;
    private final PatientRepository patientRepo;

    // Thread-Safe mao of active Observer (username -> Observer)
    private final Map<String, Observer> observers = new ConcurrentHashMap<>();

    private NotificationService() {
        this.notificationRepo = VelyaLifeApplication.getNotificationRepository();
        this.patientRepo = VelyaLifeApplication.getPatientRepository();
    }

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    /* =========================================================================
     * SUBJECT INTERFACE IMPLEMENTATION (REGISTER / UNREGISTER / NOTIFY)
     * ========================================================================= */

    @Override
    public void addObserver(String username, Observer observer) {
        if (username != null && observer != null) {
            observers.put(username, observer);
            LOGGER.info(() -> "Observer registered for user: " + username);
        }
    }

    @Override
    public void removeObserver(String username) {
        if (username != null) {
            observers.remove(username);
            LOGGER.info(() -> "Observer removed for user: " + username);
        }
    }

    // real time notification for registered users
    @Override
    public void notifyObservers(Notification notification) {
        if (notification == null) return;

        AbstractUser receiver = notification.getReceiver();
        String targetUsername = null;

        if (receiver != null) {
            targetUsername = receiver.getUsername();
        }

        if (targetUsername != null) {
            Observer obs = observers.get(targetUsername);
            if (obs != null) {
                deliverToObserver(obs, notification);
            }
        } else {
            for (Observer obs : observers.values()) {
                deliverToObserver(obs, notification);
            }
        }
    }

    private void deliverToObserver(Observer obs, Notification notification) {
        try {
            obs.update(notification);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error during notification sent via Observer", ex);
        }
    }

    private void pushAndDeliver(Notification n) {
        if (n == null) return;

        notificationRepo.save(n);
        notifyObservers(n);
    }

    /* =========================================================================
     * NOTIFICATION SEND LOGIC
     * ========================================================================= */

    public void notifyTherapyModified(Therapy therapy, Doctor modifiedBy) {
        if (therapy == null || therapy.getPatient() == null || modifiedBy == null) return;

        Patient patient = patientRepo.getById(therapy.getPatient().getCodiceFiscale());
        if (patient == null) return;

        String msgPatient = String.format("Your therapy \"%s\" was changed by doctor %s %s.",
                therapy.getPrescription(), modifiedBy.getName(), modifiedBy.getLastName());
        Notification notifPatient = new Notification(msgPatient, modifiedBy, patient, LocalDateTime.now(), false);
        pushAndDeliver(notifPatient);

        Doctor refDoc = patient.getRefDoctor();
        if (refDoc != null && !refDoc.getCodiceFiscale().equals(modifiedBy.getCodiceFiscale())) {
            String msgRef = String.format("Doctor %s %s has modified a therapy for patient %s %s.",
                    modifiedBy.getName(), modifiedBy.getLastName(), patient.getName(), patient.getLastName());
            Notification notifRef = new Notification(msgRef, modifiedBy, refDoc, LocalDateTime.now(), false);
            pushAndDeliver(notifRef);
        }
    }

    public void notifyTherapyDeleted(Therapy therapy, Doctor deletedBy) {
        if (therapy == null || therapy.getPatient() == null || deletedBy == null) return;

        Patient patient = patientRepo.getById(therapy.getPatient().getCodiceFiscale());
        if (patient == null) return;

        String msgPatient = String.format("Your therapy \"%s\" was removed by doctor %s %s.",
                therapy.getPrescription(), deletedBy.getName(), deletedBy.getLastName());
        Notification notifPatient = new Notification(msgPatient, deletedBy, patient, LocalDateTime.now(), false);
        pushAndDeliver(notifPatient);

        Doctor refDoc = patient.getRefDoctor();
        if (refDoc != null && !refDoc.getCodiceFiscale().equals(deletedBy.getCodiceFiscale())) {
            String msgRef = String.format("Doctor %s %s has eliminated therapy \"%s\" for patient %s %s.",
                    deletedBy.getName(), deletedBy.getLastName(), therapy.getPrescription(), patient.getName(), patient.getLastName());
            Notification notifRef = new Notification(msgRef, deletedBy, refDoc, LocalDateTime.now(), false);
            pushAndDeliver(notifRef);
        }
    }

    public void notifyNewTherapy(Patient patient, Therapy therapy, Doctor assignedBy) {
        if (patient == null || therapy == null || assignedBy == null) return;

        String msg = String.format("New Therapy activated \"%s\" by doctor %s %s.",
                therapy.getPrescription(), assignedBy.getName(), assignedBy.getLastName());

        Notification notif = new Notification(msg, assignedBy, patient, LocalDateTime.now(), false);
        pushAndDeliver(notif);
    }

    public void notifyPatientStoppedForDays(Patient patient) {
        if (patient == null) return;
        Doctor ref = patient.getRefDoctor();
        if (ref == null) return;

        String msg = String.format("Alert: Patient %s %s has not followed therapy for 3 consecutive days",
                patient.getName(), patient.getLastName());

        Notification notif = new Notification(msg, patient, ref, LocalDateTime.now(), false);
        pushAndDeliver(notif);
    }

    public void notifyGlucoseOutOfRange(Patient patient, double value, String severity) {
        if (patient == null || "NORMAL".equalsIgnoreCase(severity)) return;

        String levelText = switch ((severity == null) ? "" : severity.toUpperCase()) {
            case "CRITICAL_HIGH", "HIGH" -> "HIGH (Emergency)";
            case "CRITICAL_LOW", "LOW"   -> "LOW (Emergency)";
            default                     -> "Out of bounds";
        };

        String msg = String.format(
                "⚠ ALERT GLYCEMIA for patient %s %s (CF: %s): value %.1f mg/dL — Risk: %s.",
                patient.getName(), patient.getLastName(), patient.getCodiceFiscale(), value, levelText
        );
        Notification notif = new Notification(msg, patient, null, LocalDateTime.now(), false);
        pushAndDeliver(notif);
    }

    public void verifyAndNotifyTherapyCompliance(Patient patient, List<Therapy> therapies, List<DailyLog> logs) {
        if (patient == null || therapies == null || logs == null) return;

        boolean nonCompliantDays = Help_DailyLog.hasExactlyThreeConsecutiveNonCompliantDays(therapies, logs);

        if (nonCompliantDays) {
            notifyPatientStoppedForDays(patient);
        }
    }

    public void checkAndSendPatientTherapyReminder(Patient patient, List<Therapy> therapies, List<DailyLog> todayLogs) {
        if (patient == null || therapies == null) return;

        LocalDate today = LocalDate.now();

        // 1. filter therapy
        List<Therapy> activeToday = new ArrayList<>();
        for (Object obj : therapies) {
            if (obj instanceof Therapy t) {
                if (Help_DailyLog.isTherapyActiveOnDate(t, today)) {
                    activeToday.add(t);
                }
            }
        }

        if (activeToday.isEmpty()) return;

        // 2. check therapy
        boolean isCompliant = Help_DailyLog.checkTherapyAdherenceForDay(activeToday, todayLogs);
        if (!isCompliant) {
            String msg = "⏰ Therapy remainder: Take your medications for today!";
            Notification reminder = new Notification(msg, null, patient, LocalDateTime.now(), false);
            pushAndDeliver(reminder);
        }
    }

    /* =========================================================================
     * MANAGE NOTIFICATION STATUS
     * ========================================================================= */

    public List<Notification> getUnhandledGlucoseNotifications() {
        return notificationRepo.getUnhandledNotifications();
    }

    public void claimGlucoseNotification(int notificationId, String doctorUsername) {
        notificationRepo.claimNotification(notificationId, doctorUsername);
    }

    public List<Notification> getNotificationsForUser(String username) {
        return notificationRepo.getNotificationsForUser(username);
    }

    public void markAsSeen(Notification notification) {
        if (notification == null) return;
        notification.setSeen(true);
        notificationRepo.update(notification);
    }
}