package repository;

import utility.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Doctor;
import model.Patient;

public class PatientRepository implements DAOServices<Patient> {

    private static final Logger LOGGER = Logger.getLogger(PatientRepository.class.getName());

    @Override
    public void save(Patient obj) {
        String sqlPatient = """
            INSERT OR REPLACE INTO Patients\s
            (codiceFiscale, weight, smoker, drinker, ref_doctor, medical_notes, risk_factor)\s
            VALUES (?, ?, ?, ?, ?, ?, ?)
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmtPatient = conn.prepareStatement(sqlPatient)) {
            stmtPatient.setString(1, obj.getCodiceFiscale());
            stmtPatient.setDouble(2, obj.getWeight());
            stmtPatient.setInt(3, obj.getIsSmoker() ? 1 : 0);
            stmtPatient.setInt(4, obj.getIsDrinker() ? 1 : 0);
            if (obj.getRefDoctor() != null) {
                stmtPatient.setString(5, obj.getRefDoctor().getCodiceFiscale());
            } else {
                stmtPatient.setNull(5, java.sql.Types.VARCHAR);
            }
            stmtPatient.setString(6, obj.getDoctorNotes());
            stmtPatient.setString(7, obj.getRiskFactor());
            stmtPatient.executeUpdate();
            System.out.println("Patient inserted correctly");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving Patient", e);
        }
    }

    @Override
    public Patient getById(String codiceFiscale) {
        String sql = """
            SELECT u.name, u.last_name, u.dob, u.place_birth, u.nationality,\s
                   u.sex, u.tel_number, u.username, u.email, u.password, u.type,
                   p.weight, p.smoker, p.drinker, p.medical_notes, p.risk_factor,
                   d.codiceFiscale AS doctorCF
            FROM Patients p
            JOIN Users u ON u.username = p.codiceFiscale
            LEFT JOIN Doctors d ON d.codiceFiscale = p.ref_doctor
            WHERE p.codiceFiscale = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codiceFiscale);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String lastName = rs.getString("last_name");
                    String dobStr = rs.getString("dob");
                    LocalDate dob = (dobStr != null && !dobStr.isEmpty()) ? LocalDate.parse(dobStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
                    String placeBirth = rs.getString("place_birth");
                    String nationality = rs.getString("nationality");
                    String sex = rs.getString("sex");
                    String telNumber = rs.getString("tel_number");
                    String user = rs.getString("username");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String type = rs.getString("type");

                    double weight = rs.getDouble("weight");
                    boolean smoker = rs.getInt("smoker") == 1;
                    boolean drinker = rs.getInt("drinker") == 1;
                    String riskFactor = rs.getString("risk_factor");
                    String medicalNotes = rs.getString("medical_notes");

                    DoctorRepository doctorRepository = new DoctorRepository();
                    Doctor doctor = null;
                    String doctorCF = rs.getString("doctorCF");
                    if (doctorCF != null) {
                        doctor = doctorRepository.getById(doctorCF);
                    }

                    return new Patient(
                            name, lastName, dob, placeBirth, nationality, sex,
                            weight, smoker, drinker, riskFactor, doctor, medicalNotes,
                            telNumber, user, password, email, type
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving Patient by ID", e);
        }
        return null;
    }

    @Override
    public List<Patient> getAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = """
            SELECT p.codiceFiscale\s
            FROM Patients p
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String cf = rs.getString("codiceFiscale");
                Patient pat = getById(cf);
                if (pat != null) {
                    patients.add(pat);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all Patients", e);
        }
        return patients;
    }

    @Override
    public void update(Patient p) {
        String sql = """
            UPDATE Patients\s
            SET weight = ?, smoker = ?, drinker = ?, ref_doctor = ?, medical_notes = ?, risk_factor = ?\s
            WHERE codiceFiscale = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, p.getWeight());
            stmt.setInt(2, p.getIsSmoker() ? 1 : 0);
            stmt.setInt(3, p.getIsDrinker() ? 1 : 0);
            if (p.getRefDoctor() != null) {
                stmt.setString(4, p.getRefDoctor().getCodiceFiscale());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR);
            }
            stmt.setString(5, p.getDoctorNotes());
            stmt.setString(6, p.getRiskFactor());
            stmt.setString(7, p.getCodiceFiscale());

            stmt.executeUpdate();
            System.out.println("Patient specific fields updated: " + p.getCodiceFiscale());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating Patient fields", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = """
            DELETE FROM Patients\s
            WHERE codiceFiscale = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
            System.out.println("Patient deleted from Patients table: " + id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during patient deletion", e);
        }
    }

    public void updateDoctorFields(Patient patient, boolean updateRiskFactor) {
        String sql;
        if (updateRiskFactor) {
            sql = """
                UPDATE Patients\s
                SET risk_factor = ?, medical_notes = ?\s
                WHERE codiceFiscale = ?
           \s""";
        } else {
            sql = """
                UPDATE Patients\s
                SET medical_notes = ?\s
                WHERE codiceFiscale = ?
           \s""";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (updateRiskFactor) {
                stmt.setString(1, patient.getRiskFactor());
                stmt.setString(2, patient.getDoctorNotes());
                stmt.setString(3, patient.getCodiceFiscale());
            } else {
                stmt.setString(1, patient.getDoctorNotes());
                stmt.setString(2, patient.getCodiceFiscale());
            }

            stmt.executeUpdate();
            System.out.println("Patient doctor fields updated: " + patient.getCodiceFiscale());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating Patient doctor fields", e);
        }
    }
}