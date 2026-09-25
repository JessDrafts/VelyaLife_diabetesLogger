package test;

import model.Doctor;
import model.MedLogHistory;
import model.Patient;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class MedLogHistoryTest {

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

    MedLogHistory medLogHistory = new MedLogHistory(
            1,
            patient,
            doctor,
            now,
            "Updated dosage for insulin therapy"
    );

    @Test
    void getId() {
        assertEquals(1, medLogHistory.getId());
    }

    @Test
    void getPatient() {
        assertNotNull(medLogHistory.getPatient());
        assertEquals("Samantha", medLogHistory.getPatient().getName());
    }

    @Test
    void getDoctor() {
        assertNotNull(medLogHistory.getDoctor());
        assertEquals("Charles", medLogHistory.getDoctor().getName());
    }

    @Test
    void getModifiedAt() {
        assertEquals(now, medLogHistory.getModifiedAt());
    }

    @Test
    void getContent() {
        assertEquals("Updated dosage for insulin therapy", medLogHistory.getContent());
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
        medLogHistory.setPatient(newPatient);
        assertEquals("Enrico", medLogHistory.getPatient().getName());
    }

    @Test
    void setDoctor() {
        Doctor newDoctor = new Doctor(
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
        medLogHistory.setDoctor(newDoctor);
        assertEquals("Charles", medLogHistory.getDoctor().getName());
    }

    @Test
    void setModifiedAt() {
        LocalDateTime newTime = now.plusDays(1);
        medLogHistory.setModifiedAt(newTime);
        assertEquals(newTime, medLogHistory.getModifiedAt());
    }

    @Test
    void setContent() {
        medLogHistory.setContent("Reviewed patient glucose trends");
        assertEquals("Reviewed patient glucose trends", medLogHistory.getContent());
    }

    @Test
    void testMedLogHistoryToString() {
        assertNotNull(medLogHistory.toString());
        assertTrue(medLogHistory.toString().contains("Samantha"));
        assertTrue(medLogHistory.toString().contains("Charles"));
        assertTrue(medLogHistory.toString().contains("Updated dosage for insulin therapy"));
    }
}