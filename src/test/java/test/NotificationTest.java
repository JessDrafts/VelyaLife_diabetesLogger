package test;

import model.Doctor;
import model.Notification;
import model.Patient;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    Doctor senderDoctor = new Doctor(
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

    Patient receiverPatient = new Patient(
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
            senderDoctor,
            "(02) 4049 2010",
            "SamanthaMoss@teleworm.us"
    );

    LocalDateTime now = LocalDateTime.now();

    Notification notification = new Notification(
            1,
            "Please check your blood sugar levels regularly.",
            senderDoctor,
            receiverPatient,
            now,
            false
    );

    @Test
    void getId() {
        assertEquals(1, notification.getId());
    }

    @Test
    void getMessage() {
        assertEquals("Please check your blood sugar levels regularly.", notification.getMessage());
    }

    @Test
    void getSeen() {
        assertFalse(notification.getSeen());
    }

    @Test
    void getCreatedAt() {
        assertEquals(now, notification.getCreatedAt());
    }

    @Test
    void getSender() {
        assertNotNull(notification.getSender());
        assertEquals("Charles", notification.getSender().getName());
    }

    @Test
    void getReceiver() {
        assertNotNull(notification.getReceiver());
        assertEquals("Samantha", notification.getReceiver().getName());
    }

    @Test
    void setSender() {
        Doctor newSender = new Doctor(
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
        notification.setSender(newSender);
        assertEquals("Charles", notification.getSender().getName());
    }

    @Test
    void setReceiver() {
        Patient newReceiver = new Patient(
                "Enrico",
                "Arcuri",
                LocalDate.of(2004, 3, 16),
                "Torino, Italy",
                "Italy",
                "M",
                "RCRNRC04C16L219Y",
                79.0,
                false,
                true,
                "Alcohol consumption risk factor",
                senderDoctor,
                "0333 7753120",
                "EnricoArcuri@teleworm.us"
        );
        notification.setReceiver(newReceiver);
        assertEquals("Enrico", notification.getReceiver().getName());
    }

    @Test
    void setMessage() {
        notification.setMessage("Updated therapy plan instructions.");
        assertEquals("Updated therapy plan instructions.", notification.getMessage());
    }

    @Test
    void setSeen() {
        notification.setSeen(true);
        assertTrue(notification.getSeen());
    }

    @Test
    void setCreatedAt() {
        LocalDateTime newTime = now.plusDays(1);
        notification.setCreatedAt(newTime);
        assertEquals(newTime, notification.getCreatedAt());
    }

    @Test
    void testNotificationToString() {
        assertNotNull(notification.toString());
        assertTrue(notification.toString().contains("Please check your blood sugar levels regularly."));
        assertTrue(notification.toString().contains("Charles"));
        assertTrue(notification.toString().contains("Samantha"));
    }
}