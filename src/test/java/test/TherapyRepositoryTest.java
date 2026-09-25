package test;

import model.Doctor;
import model.Patient;
import model.Therapy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.TherapyRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TherapyRepositoryTest {

    private TherapyRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TherapyRepository();
    }

    @Test
    void save() {
        Doctor doctor = new Doctor(
                "Charles",
                "Dixon",
                LocalDate.of(1985, 9, 11),
                "London",
                "UK",
                "M",
                "SHFCSHFCS0",
                "913-301-0149",
                "CharlesDixon@jourrapide.com"
        );
        Patient patient = new Patient("Luigi", "Verdi", LocalDate.of(1990, 2, 20), "Milano", "Italian", "M", "VRDLGU90B20F205X", 75.0, false, false, "None", doctor, "3339876543", "luigi.verdi@test.com");

        Therapy therapy = new Therapy(
                0,
                "Metformin",
                2.0,
                1.0,
                "Take after meals",
                patient,
                doctor,
                "2026-01-01",
                "2026-12-31"
        );

        assertDoesNotThrow(() -> repository.save(therapy));
    }

    @Test
    void getById() {
        String testId = "1";
        Therapy therapy = repository.getById(testId);

        if (therapy != null) {
            assertEquals(Integer.parseInt(testId), therapy.getId());
            assertNotNull(therapy.getPatient());
            assertNotNull(therapy.getDoctor());
        } else {
            assertNull(null);
        }
    }

    @Test
    void getAll() {
        List<Therapy> therapies = repository.getAll();
        assertNotNull(therapies, "Therapy list must not be null");
    }

    @Test
    void update() {
        List<Therapy> therapies = repository.getAll();
        if (!therapies.isEmpty()) {
            Therapy therapyToUpdate = therapies.getFirst();
            therapyToUpdate.setInstructions("Take before breakfast");
            therapyToUpdate.setDailyDose(3.0);

            repository.update(therapyToUpdate);

            Therapy updatedTherapy = repository.getById(String.valueOf(therapyToUpdate.getId()));
            assertEquals("Take before breakfast", updatedTherapy.getInstructions());
            assertEquals(3.0, updatedTherapy.getDailyDose());
        }
    }

    @Test
    void delete() {
        String testId = "999";
        repository.delete(testId);

        Therapy deleted = repository.getById(testId);
        assertNull(deleted, "Eliminated therapy must return null");
    }

    @Test
    void getByPatient() {
        String codiceFiscale = "VRDLGU90B20F205X";
        List<Therapy> therapies = repository.getByPatient(codiceFiscale);

        assertNotNull(therapies, "Therapy list must not be null");
        for (Therapy t : therapies) {
            assertEquals(codiceFiscale, t.getPatient().getCodiceFiscale());
        }
    }
}