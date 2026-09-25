package test;

import model.Doctor;
import model.MedLogHistory;
import model.Patient;
import repository.DoctorRepository;
import repository.MedLogHistoryRepository;
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

class MedLogHistoryRepositoryTest {

    private MedLogHistoryRepository medLogHistoryRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private UserRepository userRepository;

    @BeforeAll
    static void initDatabase() {
        DBInit.launch();
    }

    @BeforeEach
    void setUp() {
        DBInit.clearTables();
        medLogHistoryRepository = new MedLogHistoryRepository();
        patientRepository = new PatientRepository();
        doctorRepository = new DoctorRepository();
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

        LocalDateTime now = LocalDateTime.of(2026, 6, 1, 10, 30, 0);
        MedLogHistory log = new MedLogHistory(patient, doctor, now, "Routine checkup notes");
        medLogHistoryRepository.save(log);

        List<MedLogHistory> allLogs = medLogHistoryRepository.getAll();
        assertFalse(allLogs.isEmpty());
        int generatedId = allLogs.getFirst().getId();

        MedLogHistory fetched = medLogHistoryRepository.getById(String.valueOf(generatedId));
        assertNotNull(fetched);
        assertEquals("Routine checkup notes", fetched.getContent());
        assertEquals("MSSSNT15E62Z700R", fetched.getPatient().getCodiceFiscale());
        assertEquals("DXNCRL85P10L378O", fetched.getDoctor().getCodiceFiscale());
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

        MedLogHistory log = new MedLogHistory(patient, doctor, LocalDateTime.now(), "Log entry 1");
        medLogHistoryRepository.save(log);

        List<MedLogHistory> logs = medLogHistoryRepository.getAll();
        assertNotNull(logs);
        assertFalse(logs.isEmpty());
        assertEquals(1, logs.size());
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

        MedLogHistory log = new MedLogHistory(patient, doctor, LocalDateTime.now(), "Original content");
        medLogHistoryRepository.save(log);

        int id = medLogHistoryRepository.getAll().getFirst().getId();
        MedLogHistory fetched = medLogHistoryRepository.getById(String.valueOf(id));
        fetched.setContent("Updated content");
        medLogHistoryRepository.update(fetched);

        MedLogHistory updatedFetched = medLogHistoryRepository.getById(String.valueOf(id));
        assertEquals("Updated content", updatedFetched.getContent());
    }

    @Test
    void getByUserCodiceFiscale() {
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

        MedLogHistory log = new MedLogHistory(patient, doctor, LocalDateTime.now(), "Patient log entry");
        medLogHistoryRepository.save(log);

        List<MedLogHistory> logsByPatient = medLogHistoryRepository.getByUserCodiceFiscale("MSSSNT15E62Z700R");
        assertNotNull(logsByPatient);
        assertFalse(logsByPatient.isEmpty());
        assertEquals("Patient log entry", logsByPatient.getFirst().getContent());

        List<MedLogHistory> logsByDoctor = medLogHistoryRepository.getByUserCodiceFiscale("DXNCRL85P10L378O");
        assertNotNull(logsByDoctor);
        assertFalse(logsByDoctor.isEmpty());
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

        MedLogHistory log = new MedLogHistory(patient, doctor, LocalDateTime.now(), "To be deleted");
        medLogHistoryRepository.save(log);

        int id = medLogHistoryRepository.getAll().getFirst().getId();
        assertNotNull(medLogHistoryRepository.getById(String.valueOf(id)));

        medLogHistoryRepository.delete(String.valueOf(id));
        assertNull(medLogHistoryRepository.getById(String.valueOf(id)));
    }
}