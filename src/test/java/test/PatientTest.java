package test;

import model.Doctor;
import model.Patient;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

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

    @Test
    void getCodiceFiscale() {
        assertEquals("MSSSMN15M62Z100V", patient.getCodiceFiscale());
    }

    @Test
    void getWeight() {
        assertEquals(35.0, patient.getWeight());
    }

    @Test
    void getIsSmoker() {
        assertFalse(patient.getIsSmoker());
    }

    @Test
    void getIsDrinker() {
        assertFalse(patient.getIsDrinker());
    }

    @Test
    void getRiskFactor() {
        assertEquals("Pediatric onset type 2 diabetes risk", patient.getRiskFactor());
    }

    @Test
    void getRefDoctor() {
        assertNotNull(patient.getRefDoctor());
        assertEquals("Charles", patient.getRefDoctor().getName());
    }

    @Test
    void getDoctorNotes() {
        assertNull(patient.getDoctorNotes());
    }

    @Test
    void setCodiceFiscale() {
        patient.setCodiceFiscale("NEWCF15M62Z100V");
        assertEquals("NEWCF15M62Z100V", patient.getCodiceFiscale());
    }

    @Test
    void setWeight() {
        patient.setWeight(38.5);
        assertEquals(38.5, patient.getWeight());
    }

    @Test
    void setSmoker() {
        patient.setSmoker(true);
        assertTrue(patient.getIsSmoker());
    }

    @Test
    void setDrinker() {
        patient.setDrinker(true);
        assertTrue(patient.getIsDrinker());
    }

    @Test
    void setRiskFactor() {
        patient.setRiskFactor("Updated risk factor details");
        assertEquals("Updated risk factor details", patient.getRiskFactor());
    }

    @Test
    void setRefDoctor() {
        Doctor newDctor = new Doctor(
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
        patient.setRefDoctor(newDctor);
        assertEquals("Charles", patient.getRefDoctor().getName());
    }

    @Test
    void setDoctorNotes() {
        patient.setDoctorNotes("Patient is responding well to dietary changes.");
        assertEquals("Patient is responding well to dietary changes.", patient.getDoctorNotes());
    }

    @Test
    void testPatientToString() {
        assertNotNull(patient.toString());
        assertTrue(patient.toString().contains("Samantha"));
        assertTrue(patient.toString().contains("MSSSMN15M62Z100V"));
        assertTrue(patient.toString().contains("Charles Dixon"));
    }

    @Test
    void compareTo() {
        Patient otherPatient = new Patient(
                "Christopher",
                "Wyatt",
                LocalDate.of(1962, 3, 24),
                "Melbourne, Australia",
                "Australia",
                "M",
                "WYTCRS62C24Z700I",
                80.0,
                true,
                false,
                "Older age and tobacco smoking risk",
                doctor,
                "(02) 4966 8626",
                "ChristopherWyatt@dayrep.com"
        );
        assertTrue(patient.compareTo(otherPatient) < 0);
    }
}