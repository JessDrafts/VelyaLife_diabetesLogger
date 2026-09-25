package utility;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailService {

    private static final String FROM = System.getenv("VCL_EMAIL_USER");
    private static final String PASSWORD = System.getenv("VCL_EMAIL_PASS");
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    static {
        Logger.getLogger("jakarta.mail").setLevel(Level.SEVERE);
    }

    public static void sendEmail(String to, String subject, String body) {
        if (FROM == null || PASSWORD == null || FROM.isBlank() || PASSWORD.isBlank()) {
            System.err.println("❌ Error: env variable VCL_EMAIL_USER o VCL_EMAIL_PASS not configurated.");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PASSWORD);
            }
        });

        session.setDebug(false);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("✅ Email sent to: " + to);

        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Error sending email", e);
        }
    }
}