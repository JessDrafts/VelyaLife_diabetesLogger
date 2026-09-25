package test;

import model.Doctor;
import org.junit.jupiter.api.BeforeAll;
import repository.UserRepository;
import utility.DBInit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import model.AbstractUser;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeAll
    static void initDatabase() {
        DBInit.launch();
    }

    @BeforeEach
    void setUp() {
        DBInit.clearTables();
        userRepository = new UserRepository();
    }

    @Test
    void saveAndGetById() {
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
        userRepository.save(doctor);

        AbstractUser fetched = userRepository.getById("DXNCRL85P10L378O");
        assertNotNull(fetched);
        assertEquals("Charles", fetched.getName());
        assertEquals("Dixon", fetched.getLastName());
        assertEquals("Doctor", fetched.getType());
    }

    @Test
    void getAll() {
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
        userRepository.save(doctor);

        List<AbstractUser> users = userRepository.getAll();
        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    @Test
    void update() {
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
        userRepository.save(doctor);

        doctor.setEmail("new.email@example.com");
        userRepository.update(doctor);

        AbstractUser updated = userRepository.getById("BLLMNL91B08L781F");
        assertEquals("new.email@example.com", updated.getEmail());
    }

    @Test
    void delete() {
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
        userRepository.save(doctor);
        assertNotNull(userRepository.getById("CRMNMO90M46H294R"));

        userRepository.delete("CRMNMO90M46H294R");
        assertNull(userRepository.getById("CRMNMO90M46H294R"));
    }

    @Test
    void usernameExists() {
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
        userRepository.save(doctor);

        assertTrue(userRepository.usernameExists("SHFCSHFCS0"));
        assertFalse(userRepository.usernameExists("NONEXISTENT"));
    }
}