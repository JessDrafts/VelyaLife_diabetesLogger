package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Doctor;
import model.MedLogHistory;
import model.Patient;
import utility.DBConnection;

public class MedLogHistoryRepository implements DAOServices<MedLogHistory> {

    private final PatientRepository patientRepository = new PatientRepository();
    private final DoctorRepository doctorRepository = new DoctorRepository();
    private static final Logger LOGGER = Logger.getLogger(MedLogHistoryRepository.class.getName());

    @Override
    public void save(MedLogHistory obj) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sql = """
            INSERT INTO MedLogHistory\s
            (patient, doctor, createdAt, content)\s
            VALUES (?, ?, ?, ?)
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getPatient().getCodiceFiscale());
            stmt.setString(2, obj.getDoctor().getCodiceFiscale());
            stmt.setString(3, obj.getModifiedAt().format(formatter));
            stmt.setString(4, obj.getContent());
            stmt.executeUpdate();
            System.out.println("Log inserted correctly");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving MedLogHistory", e);
        }
    }

    @Override
    public MedLogHistory getById(String id) {
        String sql = """
            SELECT ID, patient, doctor, createdAt, content\s
            FROM MedLogHistory\s
            WHERE ID = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int logId = rs.getInt("ID");
                    String patientCF = rs.getString("patient");
                    String doctorCF = rs.getString("doctor");

                    Patient patient = patientRepository.getById(patientCF);
                    Doctor doctor = doctorRepository.getById(doctorCF);

                    LocalDateTime createdAt = null;
                    String createdAtStr = rs.getString("createdAt");
                    if (createdAtStr != null && !createdAtStr.isEmpty()) {
                        createdAt = LocalDateTime.parse(createdAtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }

                    String content = rs.getString("content");

                    return new MedLogHistory(logId, patient, doctor, createdAt, content);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving MedLogHistory by ID", e);
        }
        return null;
    }

    @Override
    public List<MedLogHistory> getAll() {
        List<MedLogHistory> medLogHistoryList = new ArrayList<>();
        String sql = """
            SELECT ID, patient, doctor, createdAt, content\s
            FROM MedLogHistory
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int logId = rs.getInt("ID");
                String patientCF = rs.getString("patient");
                String doctorCF = rs.getString("doctor");

                Patient patient = patientRepository.getById(patientCF);
                Doctor doctor = doctorRepository.getById(doctorCF);

                LocalDateTime createdAt = null;
                String createdAtStr = rs.getString("createdAt");
                if (createdAtStr != null && !createdAtStr.isEmpty()) {
                    createdAt = LocalDateTime.parse(createdAtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }

                String content = rs.getString("content");

                medLogHistoryList.add(new MedLogHistory(logId, patient, doctor, createdAt, content));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all MedLogHistory records", e);
        }
        return medLogHistoryList;
    }

    @Override
    public void update(MedLogHistory obj) {
        if (obj == null || obj.getId() <= 0) {
            System.err.println("Not valid object");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sql = """
            UPDATE MedLogHistory\s
            SET patient = ?, doctor = ?, createdAt = ?, content = ?\s
            WHERE ID = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, obj.getPatient().getCodiceFiscale());
            stmt.setString(2, obj.getDoctor().getCodiceFiscale());
            stmt.setString(3, obj.getModifiedAt().format(formatter));
            stmt.setString(4, obj.getContent());
            stmt.setInt(5, obj.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Record updated: " + obj.getId());
            } else {
                System.out.println("No record found with ID: " + obj.getId());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during MedLogHistory update", e);
        }
    }

    public List<MedLogHistory> getByUserCodiceFiscale(String codiceFiscale) {
        List<MedLogHistory> historyList = new ArrayList<>();
        String sql = """
            SELECT ID, patient, doctor, createdAt, content\s
            FROM MedLogHistory\s
            WHERE patient = ? OR doctor = ?\s
            ORDER BY createdAt DESC
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codiceFiscale);
            stmt.setString(2, codiceFiscale);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int logId = rs.getInt("ID");
                    String patientCF = rs.getString("patient");
                    String doctorCF = rs.getString("doctor");

                    Patient patient = patientRepository.getById(patientCF);
                    Doctor doctor = doctorRepository.getById(doctorCF);

                    LocalDateTime createdAt = null;
                    String createdAtStr = rs.getString("createdAt");
                    if (createdAtStr != null && !createdAtStr.isEmpty()) {
                        createdAt = LocalDateTime.parse(createdAtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }

                    String content = rs.getString("content");

                    historyList.add(new MedLogHistory(logId, patient, doctor, createdAt, content));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving history by user Codice Fiscale", e);
        }
        return historyList;
    }

    @Override
    public void delete(String id) {
        String sql = """
            DELETE FROM MedLogHistory\s
            WHERE ID = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Record deleted: " + id);
            } else {
                System.out.println("No record found with ID: " + id);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during record elimination", e);
        }
    }
}