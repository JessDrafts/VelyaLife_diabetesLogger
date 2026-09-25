package test;

import model.Doctor;
import model.MedicalCondition;
import model.Patient;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.MedicalConditionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MedicalConditionRepositoryTest {

    private MedicalConditionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MedicalConditionRepository();
    }

    @Test
    void save() {
        Patient patient = getPatient();

        MedicalCondition condition = new MedicalCondition(
                0,
                "Symptom",
                "Headache and fever",
                "Symptom",
                "2026-06-01",
                "2026-06-03",
                LocalDateTime.now(),
                patient
        );

        assertDoesNotThrow(() -> repository.save(condition));
    }

    @NotNull
    private static Patient getPatient() {
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
        return new Patient("Luigi", "Verdi", LocalDate.of(1990, 2, 20), "Milano", "Italian", "M", "VRDLGU90B20F205X", 75.0, false, false, "None", doctor, "3339876543", "luigi.verdi@test.com");
    }

    @Test
    void getById() {
        String testId = "1";
        MedicalCondition condition = repository.getById(testId);

        if (condition != null) {
            assertEquals(Integer.parseInt(testId), condition.getId());
            assertNotNull(condition.getPatient());
        } else {
            assertNull(null);
        }
    }

    @Test
    void getAll() {
        List<MedicalCondition> conditions = repository.getAll();
        assertNotNull(conditions, "Medical conditions list must not be null");
    }

    @Test
    void update() {
        List<MedicalCondition> conditions = repository.getAll();
        if (!conditions.isEmpty()) {
            MedicalCondition conditionToUpdate = conditions.getFirst();
            conditionToUpdate.setDescription("Updated description");
            conditionToUpdate.setType("Pathologies");

            repository.update(conditionToUpdate);

            MedicalCondition updatedCondition = repository.getById(String.valueOf(conditionToUpdate.getId()));
            assertEquals("Updated description", updatedCondition.getDescription());
            assertEquals("Pathologies", updatedCondition.getType());
        }
    }

    @Test
    void delete() {
        String testId = "999";
        repository.delete(testId);

        MedicalCondition deleted = repository.getById(testId);
        assertNull(deleted, "Medical condition must return null");
    }

    @Test
    void getByPatient() {
        String codiceFiscale = "VRDLGU90B20F205X";
        List<MedicalCondition> conditions = repository.getByPatient(codiceFiscale);

        assertNotNull(conditions, "patient list must not be null");
        for (MedicalCondition mc : conditions) {
            assertEquals(codiceFiscale, mc.getPatient().getCodiceFiscale());
        }
    }
}