package test;

import model.Doctor;
import model.Notification;
import model.Patient;
import repository.DoctorRepository;
import repository.NotificationRepository;
import repository.PatientRepository;
import repository.UserRepository;
import utility.DBInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationRepositoryTest {

    private NotificationRepository notificationRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private UserRepository userRepository;

    @BeforeAll
    static void initDatabase() {
        DBInit.launch();
    }

    @BeforeEach
    void setUp() {
        DBInit.clearTables();
        notificationRepository = new NotificationRepository();
        doctorRepository = new DoctorRepository();
        patientRepository = new PatientRepository();
        userRepository = new UserRepository();
    }

    @Test
    void saveAndGetById() {
        Doctor doctor = new Doctor(
                "Charles",
                "Dixon",
                LocalDate.of(1985, 9, 10),
                "Trento",
                "Italia",
                "M",
                "913-301-0149",
                "DXNCRL85P10L378O",
                "4Hz_25zE",
                "CharlesDixon@jourrapide.com",
                "Doctor"
        );
        userRepository.save(doctor);
        doctorRepository.save(doctor);

        Patient patient = new Patient(
                "Samantha",
                "Moss",
                LocalDate.of(2015, 5, 22),
                "Brisbane",
                "Australia",
                "F",
                35.0,
                false,
                false,
                "pediatric onset",
                doctor,
                "Initial notes",
                "(02) 4049 2010",
                "MSSSNT15E62Z700R",
                "M2#E8_v8",
                "SamanthaMoss@teleworm.us",
                "Patient"
        );
        userRepository.save(patient);
        patientRepository.save(patient);

        LocalDateTime now = LocalDateTime.of(2026, 6, 1, 12, 0, 0);
        Notification notification = new Notification("Test message notification", doctor, patient, now, false);
        notificationRepository.save(notification);

        List<Notification> allNotifications = notificationRepository.getAll();
        assertFalse(allNotifications.isEmpty());
        int generatedId = allNotifications.getFirst().getId();

        Notification fetched = notificationRepository.getById(String.valueOf(generatedId));
        assertNotNull(fetched);
        assertEquals("Test message notification", fetched.getMessage());
        assertFalse(fetched.getSeen());
        assertNotNull(fetched.getSender());
        assertNotNull(fetched.getReceiver());
    }

    @Test
    void getAll() {
        Doctor doctor = new Doctor(
                "Charles",
                "Dixon",
                LocalDate.of(1985, 9, 10),
                "Trento",
                "Italia",
                "M",
                "913-301-0149",
                "DXNCRL85P10L378O",
                "4Hz_25zE",
                "CharlesDixon@jourrapide.com",
                "Doctor"
        );
        userRepository.save(doctor);
        doctorRepository.save(doctor);

        Patient patient = new Patient(
                "Samantha",
                "Moss",
                LocalDate.of(2015, 5, 22),
                "Brisbane",
                "Australia",
                "F",
                35.0,
                false,
                false,
                "pediatric onset",
                doctor,
                "Initial notes",
                "(02) 4049 2010",
                "MSSSNT15E62Z700R",
                "M2#E8_v8",
                "SamanthaMoss@teleworm.us",
                "Patient"
        );
        userRepository.save(patient);
        patientRepository.save(patient);

        Notification notification = new Notification("Another notification", doctor, patient, LocalDateTime.now(), false);
        notificationRepository.save(notification);

        List<Notification> notifications = notificationRepository.getAll();
        assertNotNull(notifications);
        assertFalse(notifications.isEmpty());
        assertEquals(1, notifications.size());
    }

    @Test
    void update() {
        Doctor doctor = new Doctor(
                "Charles",
                "Dixon",
                LocalDate.of(1985, 9, 10),
                "Trento",
                "Italia",
                "M",
                "913-301-0149",
                "DXNCRL85P10L378O",
                "4Hz_25zE",
                "CharlesDixon@jourrapide.com",
                "Doctor"
        );
        userRepository.save(doctor);
        doctorRepository.save(doctor);

        Patient patient = new Patient(
                "Samantha",
                "Moss",
                LocalDate.of(2015, 5, 22),
                "Brisbane",
                "Australia",
                "F",
                35.0,
                false,
                false,
                "pediatric onset",
                doctor,
                "Initial notes",
                "(02) 4049 2010",
                "MSSSNT15E62Z700R",
                "M2#E8_v8",
                "SamanthaMoss@teleworm.us",
                "Patient"
        );
        userRepository.save(patient);
        patientRepository.save(patient);

        Notification notification = new Notification("Unseen notification", doctor, patient, LocalDateTime.now(), false);
        notificationRepository.save(notification);

        int id = notificationRepository.getAll().getFirst().getId();
        Notification fetched = notificationRepository.getById(String.valueOf(id));
        assertFalse(fetched.getSeen());

        fetched.setSeen(true);
        notificationRepository.update(fetched);

        Notification updatedFetched = notificationRepository.getById(String.valueOf(id));
        assertTrue(updatedFetched.getSeen());
    }

    @Test
    void delete() {
        Doctor doctor = new Doctor(
                "Charles",
                "Dixon",
                LocalDate.of(1985, 9, 10),
                "Trento",
                "Italia",
                "M",
                "913-301-0149",
                "DXNCRL85P10L378O",
                "4Hz_25zE",
                "CharlesDixon@jourrapide.com",
                "Doctor"
        );
        userRepository.save(doctor);
        doctorRepository.save(doctor);

        Patient patient = new Patient(
                "Samantha",
                "Moss",
                LocalDate.of(2015, 5, 22),
                "Brisbane",
                "Australia",
                "F",
                35.0,
                false,
                false,
                "pediatric onset",
                doctor,
                "Initial notes",
                "(02) 4049 2010",
                "MSSSNT15E62Z700R",
                "M2#E8_v8",
                "SamanthaMoss@teleworm.us",
                "Patient"
        );
        userRepository.save(patient);
        patientRepository.save(patient);

        Notification notification = new Notification("Notification to delete", doctor, patient, LocalDateTime.now(), false);
        notificationRepository.save(notification);

        int id = notificationRepository.getAll().getFirst().getId();
        assertNotNull(notificationRepository.getById(String.valueOf(id)));

        notificationRepository.delete(String.valueOf(id));
        assertNull(notificationRepository.getById(String.valueOf(id)));
    }

    @Test
    void getNotificationsForUser() {
        Doctor doctor = new Doctor(
                "Charles",
                "Dixon",
                LocalDate.of(1985, 9, 10),
                "Trento",
                "Italia",
                "M",
                "913-301-0149",
                "DXNCRL85P10L378O",
                "4Hz_25zE",
                "CharlesDixon@jourrapide.com",
                "Doctor"
        );
        userRepository.save(doctor);
        doctorRepository.save(doctor);

        Patient patient = new Patient(
                "Samantha",
                "Moss",
                LocalDate.of(2015, 5, 22),
                "Brisbane",
                "Australia",
                "F",
                35.0,
                false,
                false,
                "pediatric onset",
                doctor,
                "Initial notes",
                "(02) 4049 2010",
                "MSSSNT15E62Z700R",
                "M2#E8_v8",
                "SamanthaMoss@teleworm.us",
                "Patient"
        );
        userRepository.save(patient);
        patientRepository.save(patient);

        Notification notification = new Notification("Hello patient", doctor, patient, LocalDateTime.now(), false);
        notificationRepository.save(notification);

        List<Notification> userNotifications = notificationRepository.getNotificationsForUser("MSSSNT15E62Z700R");
        assertNotNull(userNotifications);
        assertFalse(userNotifications.isEmpty());
        assertEquals("Hello patient", userNotifications.getFirst().getMessage());
    }
}