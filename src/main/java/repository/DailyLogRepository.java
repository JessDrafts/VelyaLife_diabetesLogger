package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import model.Patient;
import model.DailyLog;
import utility.DBConnection;
import utility.UserFactory;

public class DailyLogRepository implements DAOServices<DailyLog> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Logger LOGGER = Logger.getLogger(DailyLogRepository.class.getName());

    @Override
    public void save(DailyLog obj) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                INSERT OR REPLACE INTO DailyReport\s
                (blood_sugar_level, patient, date_time, before_meal, drugs, amount)
                VALUES (?, ?, ?, ?, ?, ?)
           \s""";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, obj.getBloodSugarLevel());
                stmt.setString(2, obj.getPatient().getCodiceFiscale());
                stmt.setString(3, obj.getCreatedAt().format(FORMATTER));
                stmt.setInt(4, obj.getBeforeMeal() ? 1 : 0);
                stmt.setString(5, obj.getDrugs());
                stmt.setInt(6, obj.getAmountIntaken());
                stmt.executeUpdate();
                System.out.println("✅ Report inserted correctly");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving DailyLog", e);
        }
    }

    @Override
    public DailyLog getById(String id) {
        String sql = """
                     SELECT r.ID, r.blood_sugar_level, r.patient, r.date_time, r.before_meal, r.drugs, \s
                            r.amount,\s
                            u.email, u.password, u.name, u.last_name
                     FROM DailyReport r
                     JOIN Users u ON u.username = r.patient
                     WHERE r.ID = ?
                \s""";

        DailyLog report = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int reportId = rs.getInt("ID");

                    Patient patient = (Patient) UserFactory.fromDatabase(
                            "Patient",
                            rs.getString("name"),
                            rs.getString("last_name"),
                            null, null, null, null, null,
                            rs.getString("patient"),
                            rs.getString("password"),
                            rs.getString("email"),
                            "Patient",
                            0.0, false, false,
                            null, null, null
                    );

                    LocalDateTime createdAt = null;
                    String createdAtStr = rs.getString("date_time");
                    if (createdAtStr != null && !createdAtStr.isEmpty()) {
                        createdAt = LocalDateTime.parse(createdAtStr, FORMATTER);
                    }

                    double bloodSugarLevel = rs.getDouble("blood_sugar_level");
                    boolean beforeMeal = rs.getInt("before_meal") == 1;
                    String drugs = rs.getString("drugs");
                    int amount = rs.getInt("amount");

                    report = new DailyLog(
                            reportId,
                            bloodSugarLevel,
                            patient,
                            createdAt,
                            beforeMeal,
                            drugs,
                            amount
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving DailyLog", e);
        }

        return report;
    }

    @Override
    public List<DailyLog> getAll() {
        List<DailyLog> reports = new ArrayList<>();
        String sql = "SELECT ID FROM DailyReport";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DailyLog r = getById(String.valueOf(rs.getInt("ID")));
                if (r != null) {
                    reports.add(r);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all DailyLog", e);
        }

        return reports;
    }

    @Override
    public void update(DailyLog obj) {
        DailyLog existing = getById(String.valueOf(obj.getId()));
        if (existing == null) {
            System.err.println("❌ Report with id not found: " + obj.getId());
            return;
        }

        double bloodSugarLevel = (obj.getBloodSugarLevel() != 0) ? obj.getBloodSugarLevel() : existing.getBloodSugarLevel();
        Patient patient = (obj.getPatient() != null) ? obj.getPatient() : existing.getPatient();
        LocalDateTime createdAt = (obj.getCreatedAt() != null) ? obj.getCreatedAt() : existing.getCreatedAt();
        boolean beforeMeal = obj.getBeforeMeal();
        String drugs = (obj.getDrugs() != null) ? obj.getDrugs() : existing.getDrugs();
        int amount = (obj.getAmountIntaken() != 0) ? obj.getAmountIntaken() : existing.getAmountIntaken();

        String sql = """
            UPDATE DailyReport
            SET blood_sugar_level = ?, patient = ?, date_time = ?, before_meal = ?,\s
                drugs = ?, amount = ?
            WHERE ID = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, bloodSugarLevel);
            stmt.setString(2, patient.getCodiceFiscale());
            stmt.setString(3, createdAt.format(FORMATTER));
            stmt.setInt(4, beforeMeal ? 1 : 0);
            stmt.setString(5, drugs);
            stmt.setInt(6, amount);
            stmt.setInt(7, obj.getId());

            stmt.executeUpdate();
            System.out.println("✅ Report updated (id=" + obj.getId() + ")");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating DailyLog", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM DailyReport WHERE ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
            System.out.println("🗑️ Report deleted correctly");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting DailyLog", e);
        }
    }

    public List<DailyLog> getByPatient(String codiceFiscale) {
        List<DailyLog> reports = new ArrayList<>();

        String sql = """
                     SELECT r.ID, r.blood_sugar_level, r.patient, r.date_time, r.before_meal, r.drugs,
                            r.amount,\s
                            u.email, u.password, u.name, u.last_name
                     FROM DailyReport r
                     JOIN Users u ON u.username = r.patient
                     WHERE r.patient = ?
                     ORDER BY r.date_time DESC
                \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codiceFiscale);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Patient patient = (Patient) UserFactory.fromDatabase(
                            "Patient",
                            rs.getString("name"),
                            rs.getString("last_name"),
                            null, null, null, null, null,
                            rs.getString("patient"),
                            rs.getString("password"),
                            rs.getString("email"),
                            "Patient",
                            0.0, false, false,
                            null, null, null
                    );

                    LocalDateTime createdAt = null;
                    String createdAtStr = rs.getString("date_time");
                    if (createdAtStr != null && !createdAtStr.isEmpty()) {
                        createdAt = LocalDateTime.parse(createdAtStr, FORMATTER);
                    }

                    DailyLog report = new DailyLog(
                            rs.getInt("ID"),
                            rs.getDouble("blood_sugar_level"),
                            patient,
                            createdAt,
                            rs.getInt("before_meal") == 1,
                            rs.getString("drugs"),
                            rs.getInt("amount")
                    );

                    reports.add(report);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieve patient's DailyLog", e);
        }
        return reports;
    }

    public int saveAndReturnId(DailyLog obj) {
        String sql = """
            INSERT INTO DailyReport (blood_sugar_level, patient, date_time, before_meal, drugs, amount)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDouble(1, obj.getBloodSugarLevel());
            stmt.setString(2, obj.getPatient().getCodiceFiscale());
            stmt.setString(3, obj.getCreatedAt().format(FORMATTER));
            stmt.setInt(4, obj.getBeforeMeal() ? 1 : 0);
            stmt.setString(5, obj.getDrugs());
            stmt.setInt(6, obj.getAmountIntaken());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    System.out.println("✅ Report with ID=" + generatedId + " saved");
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving DailyLog with id", e);
        }
        return -1;
    }

    public List<DailyLog> getByPatientAndDate(String codiceFiscale, LocalDate date) {
        return getByPatient(codiceFiscale).stream()
                .filter(r -> r.getCreatedAt().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }
}