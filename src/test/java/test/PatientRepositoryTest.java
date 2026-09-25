package test;

import model.Doctor;
import model.Patient;
import repository.DoctorRepository;
import repository.PatientRepository;
import repository.UserRepository;
import utility.DBInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PatientRepositoryTest {

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

        Patient fetched = patientRepository.getById("MSSSNT15E62Z700R");
        assertNotNull(fetched);
        assertEquals("Samantha", fetched.getName());
        assertEquals("Moss", fetched.getLastName());
        assertEquals(35.0, fetched.getWeight());
        assertNotNull(fetched.getRefDoctor());
        assertEquals("DXNCRL85P10L378O", fetched.getRefDoctor().getCodiceFiscale());
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
                "Christopher",
                "Wyatt",
                LocalDate.of(1962, 3, 24),
                "Melbourne",
                "Australia",
                "M",
                80.0,
                true,
                false,
                "older age",
                doctor,
                "Notes here",
                "(02) 4966 8626",
                "WYTCRS62C24Z700I",
                "kNs@A069",
                "ChristopherWyatt@dayrep.com",
                "Patient"
        );
        userRepository.save(patient);
        patientRepository.save(patient);

        List<Patient> patients = patientRepository.getAll();
        assertNotNull(patients);
        assertFalse(patients.isEmpty());
        assertEquals(1, patients.size());
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

        patient.setWeight(38.0);
        patientRepository.update(patient);

        Patient fetched = patientRepository.getById("MSSSNT15E62Z700R");
        assertEquals(38.0, fetched.getWeight());
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
        assertNotNull(patientRepository.getById("MSSSNT15E62Z700R"));

        patientRepository.delete("MSSSNT15E62Z700R");
        assertNull(patientRepository.getById("MSSSNT15E62Z700R"));
    }

    @Test
    void updateDoctorFields() {
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

        patient.setDoctorNotes("Updated medical observations by doctor");
        patient.setRiskFactor("Updated risk assessment");
        patientRepository.updateDoctorFields(patient, true);

        Patient fetched = patientRepository.getById("MSSSNT15E62Z700R");
        assertEquals("Updated medical observations by doctor", fetched.getDoctorNotes());
        assertEquals("Updated risk assessment", fetched.getRiskFactor());
    }
}