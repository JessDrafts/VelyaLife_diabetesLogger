package test;

import utility.Help_functions;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class Help_functionsTest {

    @Test
    void isValideCodicefiscale() {
        // Test with a valid Italian Codice Fiscale (e.g., Samantha Moss's valid code structure or similar mock profile)
        boolean valid = Help_functions.isValideCodicefiscale(
                "MSSSNT15E62Z700R",
                "Moss",
                "Samantha",
                LocalDate.of(2015, 5, 22),
                'F',
                null,
                "Australia"
        );
        assertTrue(valid, "Codice fiscale should be validated as correct");

        // Test invalid length
        assertFalse(Help_functions.isValideCodicefiscale("INVALID", "Moss", "Samantha", LocalDate.of(2015, 5, 22), 'F', null, "Australia"));
    }

    @Test
    void isValidEmail() {
        assertTrue(Help_functions.isValidEmail("SamanthaMoss@teleworm.us"));
        assertFalse(Help_functions.isValidEmail("invalid-email-format"));
        assertFalse(Help_functions.isValidEmail(null));
    }

    @Test
    void isValidPassword() {
        // Requires length >= 6, special char (@#&_-), digit, lowercase, uppercase
        assertTrue(Help_functions.isValidPassword("P@ss12"));
        assertFalse(Help_functions.isValidPassword("weak")); // Too short, missing upper/special/digit
        assertFalse(Help_functions.isValidPassword("PASSWORD123!")); // Missing lowercase
        assertFalse(Help_functions.isValidPassword(null));
    }

    @Test
    void formattedDate() {
        LocalDate date = LocalDate.of(2026, 6, 15);
        assertEquals("15/06/2026", Help_functions.formattedDate(date));
        assertEquals("dd/MM/yyyy", Help_functions.formattedDate(null));
    }

    @Test
    void formattedDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 6, 15, 8, 30);
        assertEquals("15/06/2026 08:30", Help_functions.formattedDateTime(dateTime));
        assertEquals("dd/MM/yyyy", Help_functions.formattedDateTime(null));
    }
}