package test;

import model.Doctor;
import model.MedicalCondition;
import model.Patient;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class MedicalConditionTest {

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

    Patient patient = new Patient(
            "Samantha",
            "Moss",
            LocalDate.of(2015, 5, 22),
            "Brisbane, Australia",
            "Australia",
            "F",
            "MSSSMN15M62Z100V",
            35.0,
            false,
            false,
            "Pediatric onset type 2 diabetes risk",
            doctor,
            "(02) 4049 2010",
            "SamanthaMoss@teleworm.us"
    );

    LocalDateTime now = LocalDateTime.now();

    MedicalCondition condition = new MedicalCondition(
            1,
            "Hyperglycemia",
            "High blood sugar episode",
            "Symptom",
            "2026-06-15 08:00",
            null,
            now,
            patient
    );

    @Test
    void getId() {
        assertEquals(1, condition.getId());
    }

    @Test
    void getName() {
        assertEquals("Hyperglycemia", condition.getName());
    }

    @Test
    void getDescription() {
        assertEquals("High blood sugar episode", condition.getDescription());
    }

    @Test
    void getType() {
        assertEquals("Symptom", condition.getType());
    }

    @Test
    void getStart() {
        assertEquals("2026-06-15 08:00", condition.getStart());
    }

    @Test
    void getEnd() {
        assertNull(condition.getEnd());
    }

    @Test
    void getPatient() {
        assertNotNull(condition.getPatient());
        assertEquals("Samantha", condition.getPatient().getName());
    }

    @Test
    void getCreatedAt() {
        assertEquals(now, condition.getCreatedAt());
    }

    @Test
    void setId() {
        condition.setId(2);
        assertEquals(2, condition.getId());
    }

    @Test
    void setName() {
        condition.setName("Hypoglycemia");
        assertEquals("Hypoglycemia", condition.getName());
    }

    @Test
    void setDescription() {
        condition.setDescription("Low blood sugar episode");
        assertEquals("Low blood sugar episode", condition.getDescription());
    }

    @Test
    void setType() {
        condition.setType("pathologies");
        assertEquals("pathologies", condition.getType());
    }

    @Test
    void setStart() {
        condition.setStart("2026-06-16 10:00");
        assertEquals("2026-06-16 10:00", condition.getStart());
    }

    @Test
    void setEnd() {
        condition.setEnd("2026-06-16 11:00");
        assertEquals("2026-06-16 11:00", condition.getEnd());
    }

    @Test
    void setPatient() {
        Patient newPatient = new Patient(
                "Enrico",
                "Arcuri",
                LocalDate.of(2004, 3, 16),
                "Torino, Italy",
                "Italy",
                "M",
                "RCRNRC04C16L219Y",
                79.0,
                false,
                true,
                "Alcohol consumption risk factor",
                doctor,
                "0333 7753120",
                "EnricoArcuri@teleworm.us"
        );
        condition.setPatient(newPatient);
        assertEquals("Enrico", condition.getPatient().getName());
    }

    @Test
    void setCreatedAt() {
        LocalDateTime newTime = now.plusDays(1);
        condition.setCreatedAt(newTime);
        assertEquals(newTime, condition.getCreatedAt());
    }

    @Test
    void testMedicalConditionToString() {
        assertNotNull(condition.toString());
        assertTrue(condition.toString().contains("Symptoms:"));
        assertTrue(condition.toString().contains("High blood sugar episode"));
    }
}