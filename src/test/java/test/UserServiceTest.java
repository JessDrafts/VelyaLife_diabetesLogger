package test;

import model.Doctor;
import repository.DoctorRepository;
import repository.UserRepository;
import utility.UserService;
import utility.DBInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private DoctorRepository doctorRepository;
    private UserRepository userRepository;

    @BeforeAll
    static void initDatabase() {
        DBInit.launch();
    }

    @BeforeEach
    void setUp() {
        DBInit.clearTables();
        userService = new UserService();
        doctorRepository = new DoctorRepository();
        userRepository = new UserRepository();
    }

    @Test
    void registerUserToDb() {
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

        userService.registerUserToDb(doctor);

        assertNotNull(doctorRepository.getById("DXNCRL85P10L378O"));
        assertNotNull(userRepository.getById("DXNCRL85P10L378O"));
    }

    @Test
    void deleteUserFromDb() {
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

        userService.registerUserToDb(doctor);
        assertNotNull(doctorRepository.getById("DXNCRL85P10L378O"));

        userService.deleteUserFromDb(doctor);
        assertNull(doctorRepository.getById("DXNCRL85P10L378O"));
    }
}