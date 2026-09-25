package test;

import model.Doctor;
import model.Patient;
import model.Therapy;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TherapyTest {

    Doctor doctor  = new Doctor(
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

    Therapy therapy = new Therapy(
            1,
            "Insulin Glargine",
            10.0,
            10.0,
            "Inject subcutaneously once daily before bedtime",
            patient,
            doctor,
            "2026-06-01",
            null
    );

    @Test
    void getId() {
        assertEquals(1, therapy.getId());
    }

    @Test
    void getPrescription() {
        assertEquals("Insulin Glargine", therapy.getPrescription());
    }

    @Test
    void getDailyDose() {
        assertEquals(10.0, therapy.getDailyDose());
    }

    @Test
    void getAmountIntaken() {
        assertEquals(10.0, therapy.getAmountIntaken());
    }

    @Test
    void getInstructions() {
        assertEquals("Inject subcutaneously once daily before bedtime", therapy.getInstructions());
    }

    @Test
    void getPatient() {
        assertNotNull(therapy.getPatient());
        assertEquals("Samantha", therapy.getPatient().getName());
    }

    @Test
    void getDoctor() {
        assertNotNull(therapy.getDoctor());
        assertEquals("Charles", therapy.getDoctor().getName());
    }

    @Test
    void getStartDate() {
        assertEquals("2026-06-01", therapy.getStartDate());
    }

    @Test
    void getEndDate() {
        assertNull(therapy.getEndDate());
    }

    @Test
    void setPrescription() {
        therapy.setPrescription("Metformin");
        assertEquals("Metformin", therapy.getPrescription());
    }

    @Test
    void setDailyDose() {
        therapy.setDailyDose(15.0);
        assertEquals(15.0, therapy.getDailyDose());
    }

    @Test
    void setAmountIntaken() {
        therapy.setAmountIntaken(15.0);
        assertEquals(15.0, therapy.getAmountIntaken());
    }

    @Test
    void setInstructions() {
        therapy.setInstructions("Take with meals");
        assertEquals("Take with meals", therapy.getInstructions());
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
        therapy.setPatient(newPatient);
        assertEquals("Enrico", therapy.getPatient().getName());
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
        therapy.setDoctor(newDoctor);
        assertEquals("Charles", therapy.getDoctor().getName());
    }

    @Test
    void setStartDate() {
        therapy.setStartDate("2026-06-10");
        assertEquals("2026-06-10", therapy.getStartDate());
    }

    @Test
    void setEndDate() {
        therapy.setEndDate("2026-12-31");
        assertEquals("2026-12-31", therapy.getEndDate());
    }

    @Test
    void testTherapyToString() {
        assertNotNull(therapy.toString());
        assertTrue(therapy.toString().contains("Insulin Glargine"));
        assertTrue(therapy.toString().contains("MSSSMN15M62Z100V"));
        assertTrue(therapy.toString().contains("2026-06-01"));
    }
}