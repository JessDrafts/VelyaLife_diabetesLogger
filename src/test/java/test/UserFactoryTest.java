package test;

import model.AbstractUser;
import model.Doctor;
import model.Patient;
import utility.UserFactory;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    @Test
    void createPatient() {
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

        Patient patient = UserFactory.createPatient(
                "Samantha",
                "Moss",
                LocalDate.of(2015, 5, 22),
                "Brisbane",
                "Australia",
                "F",
                "MSSSNT15E62Z700R",
                35.0,
                false,
                false,
                "pediatric onset",
                doctor,
                "(02) 4049 2010",
                "SamanthaMoss@teleworm.us"
        );

        assertNotNull(patient);
        assertEquals("Samantha", patient.getName());
        assertEquals("Moss", patient.getLastName());
        assertEquals("MSSSNT15E62Z700R", patient.getCodiceFiscale());
        assertEquals(35.0, patient.getWeight());
        assertNotNull(patient.getRefDoctor());
    }

    @Test
    void createDoctor() {
        Doctor doctor = UserFactory.createDoctor(
                "Charles",
                "Dixon",
                LocalDate.of(1985, 9, 10),
                "Trento",
                "Italia",
                "M",
                "DXNCRL85P10L378O",
                "913-301-0149",
                "CharlesDixon@jourrapide.com"
        );

        assertNotNull(doctor);
        assertEquals("Charles", doctor.getName());
        assertEquals("Dixon", doctor.getLastName());
        assertEquals("DXNCRL85P10L378O", doctor.getCodiceFiscale());
    }

    @Test
    void fromDatabase() {
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

        AbstractUser userDoctor = UserFactory.fromDatabase(
                "Doctor",
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
                "Doctor",
                0.0,
                false,
                false,
                null,
                null,
                null
        );

        assertNotNull(userDoctor);
        assertInstanceOf(Doctor.class, userDoctor);
        assertEquals("DXNCRL85P10L378O", userDoctor.getUsername());

        AbstractUser userPatient = UserFactory.fromDatabase(
                "Patient",
                "Samantha",
                "Moss",
                LocalDate.of(2015, 5, 22),
                "Brisbane",
                "Australia",
                "F",
                "(02) 4049 2010",
                "MSSSNT15E62Z700R",
                "M2#E8_v8",
                "SamanthaMoss@teleworm.us",
                "Patient",
                35.0,
                false,
                false,
                "pediatric onset",
                doctor,
                "Initial notes"
        );

        assertNotNull(userPatient);
        assertInstanceOf(Patient.class, userPatient);
        assertEquals("MSSSNT15E62Z700R", userPatient.getUsername());
    }
}