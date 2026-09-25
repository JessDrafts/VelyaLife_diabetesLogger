package test;

import model.DailyLog;
import model.Doctor;
import model.Patient;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class DailyLogTest {

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

    DailyLog dailyLog = new DailyLog(
            1,
            120.5,
            patient,
            now,
            true,
            "Insulin",
            2
    );

    @Test
    void getId() {
        assertEquals(1, dailyLog.getId());
    }

    @Test
    void getBloodSugarLevel() {
        assertEquals(120.5, dailyLog.getBloodSugarLevel());
    }

    @Test
    void getPatient() {
        assertNotNull(dailyLog.getPatient());
        assertEquals("Samantha", dailyLog.getPatient().getName());
    }

    @Test
    void getCreatedAt() {
        assertEquals(now, dailyLog.getCreatedAt());
    }

    @Test
    void getBeforeMeal() {
        assertTrue(dailyLog.getBeforeMeal());
    }

    @Test
    void getDrugs() {
        assertEquals("Insulin", dailyLog.getDrugs());
    }

    @Test
    void getAmountIntaken() {
        assertEquals(2, dailyLog.getAmountIntaken());
    }

    @Test
    void setId() {
        dailyLog.setId(2);
        assertEquals(2, dailyLog.getId());
    }

    @Test
    void setBloodSugarLevel() {
        dailyLog.setBloodSugarLevel(140.0);
        assertEquals(140.0, dailyLog.getBloodSugarLevel());
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
        dailyLog.setPatient(newPatient);
        assertEquals("Enrico", dailyLog.getPatient().getName());
    }

    @Test
    void setCreatedAt() {
        LocalDateTime newTime = now.plusDays(1);
        dailyLog.setCreatedAt(newTime);
        assertEquals(newTime, dailyLog.getCreatedAt());
    }

    @Test
    void setBeforeMeal() {
        dailyLog.setBeforeMeal(false);
        assertFalse(dailyLog.getBeforeMeal());
    }

    @Test
    void setDrugs() {
        dailyLog.setDrugs("Metformin");
        assertEquals("Metformin", dailyLog.getDrugs());
    }

    @Test
    void setAmountIntaken() {
        dailyLog.setAmountIntaken(5);
        assertEquals(5, dailyLog.getAmountIntaken());
    }

    @Test
    void testDailyLogToString() {
        assertNotNull(dailyLog.toString());
        assertTrue(dailyLog.toString().contains("Samantha"));
    }
}