package repository;

import utility.DBConnection;
import utility.UserFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.AbstractUser;
import model.Notification;

public class NotificationRepository implements DAOServices<Notification> {

    private static final Logger LOGGER = Logger.getLogger(NotificationRepository.class.getName());

    @Override
    public void save(Notification obj) {
        try (Connection conn = DBConnection.getConnection()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String sql = """
                INSERT INTO Notifications\s
                (message, sender, "user", createdAt, seen)\s
                VALUES (?, ?, ?, ?, ?)
           \s""";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, obj.getMessage());

                if (obj.getSender() != null) {
                    stmt.setString(2, obj.getSender().getUsername());
                } else {
                    stmt.setNull(2, Types.VARCHAR);
                }

                if (obj.getReceiver() != null) {
                    stmt.setString(3, obj.getReceiver().getUsername());
                } else {
                    stmt.setNull(3, Types.VARCHAR);
                }

                stmt.setString(4, obj.getCreatedAt().format(formatter));
                stmt.setInt(5, obj.getSeen() ? 1 : 0);
                stmt.executeUpdate();
                System.out.println("Notification inserted correctly");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving Notification", e);
        }
    }

    @Override
    public Notification getById(String id) {
        String sql = """
            SELECT n.ID, n.message, n.sender AS senderCF, n.user AS receiverCF, n.createdAt, n.seen,
                   su.email AS senderEmail, su.password AS senderPwd, su.type AS senderRole,
                   su.name AS senderName, su.last_name AS senderLastName,
                   ru.email AS receiverEmail, ru.password AS receiverPwd, ru.type AS receiverRole,
                   ru.name AS receiverName, ru.last_name AS receiverLastName
            FROM Notifications n
            LEFT JOIN Users su ON su.username = n.sender
            LEFT JOIN Users ru ON ru.username = n.user
            WHERE n.ID = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int logId = rs.getInt("ID");
                    String message = rs.getString("message");
                    boolean seen = rs.getInt("seen") == 1;

                    LocalDateTime createdAt = null;
                    String createdAtStr = rs.getString("createdAt");
                    if (createdAtStr != null && !createdAtStr.isEmpty()) {
                        createdAt = LocalDateTime.parse(createdAtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }

                    AbstractUser sender = null;
                    String senderRole = rs.getString("senderRole");
                    if (senderRole != null) {
                        sender = UserFactory.fromDatabase(
                                senderRole,
                                rs.getString("senderName"),
                                rs.getString("senderLastName"),
                                null, null, null, null, null,
                                rs.getString("senderCF"),
                                rs.getString("senderPwd"),
                                rs.getString("senderEmail"),
                                senderRole,
                                0.0, false, false,
                                null, null, null
                        );
                    }

                    AbstractUser receiver = null;
                    String receiverRole = rs.getString("receiverRole");
                    if (receiverRole != null) {
                        receiver = UserFactory.fromDatabase(
                                receiverRole,
                                rs.getString("receiverName"),
                                rs.getString("receiverLastName"),
                                null, null, null, null, null,
                                rs.getString("receiverCF"),
                                rs.getString("receiverPwd"),
                                rs.getString("receiverEmail"),
                                receiverRole,
                                0.0, false, false,
                                null, null, null
                        );
                    }

                    return new Notification(logId, message, sender, receiver, createdAt, seen);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving Notification by ID", e);
        }
        return null;
    }

    @Override
    public List<Notification> getAll() {
        List<Notification> notifications = new ArrayList<>();
        String sql = """
            SELECT ID\s
            FROM Notifications
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String idStr = String.valueOf(rs.getInt("ID"));
                Notification notif = getById(idStr);
                if (notif != null) {
                    notifications.add(notif);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all Notifications", e);
        }
        return notifications;
    }

    @Override
    public void update(Notification notification) {
        if (notification == null) {
            System.err.println("Notification null, can't update.");
            return;
        }

        String sql = """
            UPDATE Notifications\s
            SET seen = ?\s
            WHERE ID = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notification.getSeen() ? 1 : 0);
            stmt.setInt(2, notification.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Notification updated: ID=" + notification.getId());
            } else {
                System.out.println("notification with ID=" + notification.getId() + "not founded");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during Notification update", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = """
            DELETE FROM Notifications\s
            WHERE ID = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
            System.out.println("Notification deleted: " + id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during notification elimination", e);
        }
    }

    public List<Notification> getNotificationsForUser(String currentUsername) {
        List<Notification> notifications = new ArrayList<>();
        String sql = """
            SELECT ID\s
            FROM Notifications\s
            WHERE user = ?\s
            ORDER BY createdAt DESC
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, currentUsername);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String idStr = String.valueOf(rs.getInt("ID"));
                    Notification notif = getById(idStr);
                    if (notif != null) {
                        notifications.add(notif);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving notifications for user", e);
        }
        return notifications;
    }

    public List<Notification> getUnhandledNotifications() {
        List<Notification> notifications = new ArrayList<>();
        String sql = """
        SELECT ID\s
        FROM Notifications\s
        WHERE "user" IS NULL AND seen = 0
        ORDER BY createdAt DESC
   \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String idStr = String.valueOf(rs.getInt("ID"));
                Notification notif = getById(idStr);
                if (notif != null) {
                    notifications.add(notif);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving unhandled notifications", e);
        }
        return notifications;
    }

    public boolean claimNotification(int notificationId, String doctorUsername) {
        String sql = """
        UPDATE Notifications\s
        SET "user" = ?, seen = 1\s
        WHERE ID = ? AND "user" IS NULL
   \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctorUsername);
            stmt.setInt(2, notificationId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error claiming notification", e);
            return false;
        }
    }
}