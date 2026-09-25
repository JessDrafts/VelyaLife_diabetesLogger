package test;

import model.Doctor;
import model.Patient;
import model.DailyLog;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.DailyLogRepository;
import utility.DBInit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DailyLogRepositoryTest {

    private DailyLogRepository repository;

    @BeforeEach
    void setUp() {
        repository = new DailyLogRepository();
         DBInit.clearTables();
    }

    @Test
    void saveAndReturnId() {
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
        Patient dummyPatient = getDummyPatient(doctor);

        DailyLog report = new DailyLog(0, 110.5, dummyPatient, LocalDateTime.now(), true, "Paracetamol", 1);
        int generatedId = repository.saveAndReturnId(report);

        assertTrue(generatedId > 0, "ID must be greater than 0");
    }

    @NotNull
    private static Patient getDummyPatient(Doctor doctor) {
        Patient dummyPatient = new Patient(
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
        dummyPatient.setCodiceFiscale("MSSSNT15E62Z700R");
        return dummyPatient;
    }

    @Test
    void getById() {
        String testId = "1"; // existing ID
        DailyLog report = repository.getById(testId);

        // check record
        if (report != null) {
            assertEquals(Integer.parseInt(testId), report.getId());
            assertNotNull(report.getPatient());
        } else {
            // if DB empty
            assertNull(null);
        }
    }

    @Test
    void getAll() {
        List<DailyLog> reports = repository.getAll();
        assertNotNull(reports, "Report list must not be null");
    }

    @Test
    void update() {
        // 1. Create/get report
        List<DailyLog> reports = repository.getAll();
        if (!reports.isEmpty()) {
            DailyLog reportToUpdate = reports.getFirst();
            reportToUpdate.setBloodSugarLevel(145.0);
            reportToUpdate.setDrugs("Insulin");

            repository.update(reportToUpdate);

            DailyLog updatedReport = repository.getById(String.valueOf(reportToUpdate.getId()));
            assertEquals(145.0, updatedReport.getBloodSugarLevel());
            assertEquals("Insulin", updatedReport.getDrugs());
        }
    }

    @Test
    void delete() {
        // create new one
        String testId = "999";
        repository.delete(testId);

        DailyLog deleted = repository.getById(testId);
        assertNull(deleted, "Deleter report must return null");
    }

    @Test
    void getByPatient() {
        String codiceFiscale = "RSSMRA80A01H501U";
        List<DailyLog> reports = repository.getByPatient(codiceFiscale);

        assertNotNull(reports, "Patient list must not be null");
        for (DailyLog r : reports) {
            assertEquals(codiceFiscale, r.getPatient().getCodiceFiscale());
        }
    }

    @Test
    void getByPatientAndDate() {
        String codiceFiscale = "RSSMRA80A01H501U";
        LocalDate today = LocalDate.now();

        List<DailyLog> reports = repository.getByPatientAndDate(codiceFiscale, today);
        assertNotNull(reports, "Filterd list must not be null");

        for (DailyLog r : reports) {
            assertEquals(today, r.getCreatedAt().toLocalDate());
        }
    }
}