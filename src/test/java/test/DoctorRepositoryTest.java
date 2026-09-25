package test;

import model.Doctor;
import repository.DoctorRepository;
import repository.UserRepository;
import utility.DBInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DoctorRepositoryTest {

    private DoctorRepository doctorRepository;
    private UserRepository userRepository;

    @BeforeAll
    static void initDatabase() {
        DBInit.launch();
    }

    @BeforeEach
    void setUp() {
        DBInit.clearTables();
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
        // Save in Users first because of foreign key constraints
        userRepository.save(doctor);
        doctorRepository.save(doctor);

        Doctor fetched = doctorRepository.getById("DXNCRL85P10L378O");
        assertNotNull(fetched);
        assertEquals("Charles", fetched.getName());
        assertEquals("Dixon", fetched.getLastName());
        assertEquals("DXNCRL85P10L378O", fetched.getCodiceFiscale());
    }

    @Test
    void getAll() {
        Doctor doctor = new Doctor(
                "Violetta",
                "Castiglione",
                LocalDate.of(1996, 7, 18),
                "Milano",
                "Italia",
                "F",
                "0319 0223676",
                "CSTVTT96L58F205N",
                "#E-p41K3",
                "ViolettaCastiglione@armyspy.com",
                "Doctor"
        );
        userRepository.save(doctor);
        doctorRepository.save(doctor);

        List<Doctor> doctors = doctorRepository.getAll();
        assertNotNull(doctors);
        assertFalse(doctors.isEmpty());
        assertEquals(1, doctors.size());
    }

    @Test
    void update() {
        Doctor doctor = new Doctor(
                "Manuel",
                "Bellucci",
                LocalDate.of(1991, 2, 8),
                "Verona",
                "Italia",
                "M",
                "0384 3888064",
                "BLLMNL91B08L781F",
                "H@f0f6e5",
                "ManuelBellucci@armyspy.com",
                "Doctor"
        );
        userRepository.save(doctor);
        doctorRepository.save(doctor);

        doctor.setEmail("new.email@armyspy.com");
        userRepository.update(doctor);

        Doctor fetched = doctorRepository.getById("BLLMNL91B08L781F");
        assertEquals("new.email@armyspy.com", fetched.getEmail());
    }

    @Test
    void delete() {
        Doctor doctor = new Doctor(
                "Noemi",
                "Cremonesi",
                LocalDate.of(1990, 8, 6),
                "Rimini",
                "Italia",
                "F",
                "0326 7646948",
                "CRMNMO90M46H294R",
                "F-877p#4",
                "NoemiCremonesi@teleworm.us",
                "Doctor"
        );
        userRepository.save(doctor);
        doctorRepository.save(doctor);
        assertNotNull(doctorRepository.getById("CRMNMO90M46H294R"));

        doctorRepository.delete("CRMNMO90M46H294R");
        assertNull(doctorRepository.getById("CRMNMO90M46H294R"));
    }
}