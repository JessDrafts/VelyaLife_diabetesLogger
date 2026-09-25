package test;

import utility.AdminCreator;
import repository.UserRepository;
import utility.DBInit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.AbstractUser;

import static org.junit.jupiter.api.Assertions.*;

class AdminCreatorTest {

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
    void testToString() {
        AdminCreator admin = new AdminCreator("admin", "hashedpassword123", "infovelyalife@gmail.com");

        String result = admin.toString();

        assertNotNull(result);
        assertTrue(result.contains("admin"));
        assertTrue(result.contains("infovelyalife@gmail.com"));
        assertTrue(result.contains("ADMIN"));
    }

    @Test
    void launch() {
        assertFalse(userRepository.usernameExists("admin"));

        AdminCreator.launch();

        assertTrue(userRepository.usernameExists("admin"));
        AbstractUser fetchedAdmin = userRepository.getById("admin");
        assertNotNull(fetchedAdmin);
        assertEquals("ADMIN", fetchedAdmin.getType());
        assertEquals("infovelyalife@gmail.com", fetchedAdmin.getEmail());
    }
}