package test;

import org.junit.jupiter.api.Test;
import utility.EmailService;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    @Test
    void sendEmail() {
        String recipient = "info.velyalifeteam@gmail.com";
        String subject = "Test VelyaLife Notification";
        String body = "<h1>Test Email</h1><p>Tis is a test email sent by VelyaLife system.</p>";

        assertDoesNotThrow(() -> EmailService.sendEmail(recipient, subject, body), "L'invio dell'email ha generato un'eccezione inaspettata.");
    }
}