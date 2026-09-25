package repository;

import model.Doctor;
import model.MedicalCondition;
import model.Patient;
import utility.DBConnection;
import utility.UserFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MedicalConditionRepository implements DAOServices<MedicalCondition> {

    private static final Logger LOGGER = Logger.getLogger(MedicalConditionRepository.class.getName());

    @Override
    public void save(MedicalCondition obj) {
        String sql = """
            INSERT OR REPLACE INTO Patient_Health\s
            (type, name, description, start_date, end_date, patient)\s
            VALUES (?, ?, ?, ?, ?, ?)
       \s""";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getType());
            stmt.setString(2, obj.getName());
            stmt.setString(3, obj.getDescription());
            stmt.setString(4, obj.getStart());
            stmt.setString(5, obj.getEnd());
            stmt.setString(6, obj.getPatient().getCodiceFiscale());
            stmt.executeUpdate();
            System.out.println("Medical condition inserted correctly");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving Medical condition", e);
        }
    }

    @Override
    public MedicalCondition getById(String id) {
        String sql = """
            SELECT ph.ID, ph.type, ph.name, ph.description, ph.start_date, ph.end_date,\s
                   p.codiceFiscale AS p_cf, p.weight, p.smoker, p.drinker, p.medical_notes, p.risk_factor,
                   pu.name AS p_name, pu.last_name AS p_lastName, pu.dob AS p_dob, pu.place_birth AS p_pb,\s
                   pu.nationality AS p_nat, pu.sex AS p_sex, pu.tel_number AS p_tel, pu.email AS p_email, pu.password AS p_pwd, pu.type AS p_type,
                   d.codiceFiscale AS d_cf,
                   du.name AS d_name, du.last_name AS d_lastName, du.dob AS d_dob, du.place_birth AS d_pb,\s
                   du.nationality AS d_nat, du.sex AS d_sex, du.tel_number AS d_tel, du.email AS d_email, du.password AS d_pwd, du.type AS d_type
            FROM Patient_Health ph
            JOIN Patients p ON p.codiceFiscale = ph.patient
            JOIN Users pu ON pu.username = p.codiceFiscale
            LEFT JOIN Doctors d ON d.codiceFiscale = p.ref_doctor
            LEFT JOIN Users du ON du.username = d.codiceFiscale
            WHERE ph.ID = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(id));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Doctor doctor = null;
                    String dCf = rs.getString("d_cf");
                    if (dCf != null) {
                        doctor = (Doctor) UserFactory.fromDatabase(
                                rs.getString("d_type"),
                                rs.getString("d_name"),
                                rs.getString("d_lastName"),
                                LocalDate.parse(rs.getString("d_dob")),
                                rs.getString("d_pb"),
                                rs.getString("d_nat"),
                                rs.getString("d_sex"),
                                rs.getString("d_tel"),
                                dCf,
                                rs.getString("d_pwd"),
                                rs.getString("d_email"),
                                rs.getString("d_type"),
                                0.0, false, false,
                                null, null, null
                        );
                    }

                    Patient patient = (Patient) UserFactory.fromDatabase(
                            rs.getString("p_type"),
                            rs.getString("p_name"),
                            rs.getString("p_lastName"),
                            LocalDate.parse(rs.getString("p_dob")),
                            rs.getString("p_pb"),
                            rs.getString("p_nat"),
                            rs.getString("p_sex"),
                            rs.getString("p_tel"),
                            rs.getString("p_cf"),
                            rs.getString("p_pwd"),
                            rs.getString("p_email"),
                            rs.getString("p_type"),
                            rs.getDouble("weight"),
                            rs.getInt("smoker") == 1,
                            rs.getInt("drinker") == 1,
                            rs.getString("risk_factor"),
                            doctor,
                            rs.getString("medical_notes")
                    );

                    return new MedicalCondition(
                            rs.getInt("ID"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("type"),
                            rs.getString("start_date"),
                            rs.getString("end_date"),
                            LocalDateTime.now(),
                            patient
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving Medical condition", e);
        }
        return null;
    }

    @Override
    public List<MedicalCondition> getAll() {
        String sql = """
            SELECT ph.ID, ph.type, ph.name, ph.description, ph.start_date, ph.end_date,\s
                   p.codiceFiscale AS p_cf, p.weight, p.smoker, p.drinker, p.medical_notes, p.risk_factor,
                   pu.name AS p_name, pu.last_name AS p_lastName, pu.dob AS p_dob, pu.place_birth AS p_pb,\s
                   pu.nationality AS p_nat, pu.sex AS p_sex, pu.tel_number AS p_tel, pu.email AS p_email, pu.password AS p_pwd, pu.type AS p_type,
                   d.codiceFiscale AS d_cf,
                   du.name AS d_name, du.last_name AS d_lastName, du.dob AS d_dob, du.place_birth AS d_pb,\s
                   du.nationality AS d_nat, du.sex AS d_sex, du.tel_number AS d_tel, du.email AS d_email, du.password AS d_pwd, du.type AS d_type
            FROM Patient_Health ph
            JOIN Patients p ON p.codiceFiscale = ph.patient
            JOIN Users pu ON pu.username = p.codiceFiscale
            LEFT JOIN Doctors d ON d.codiceFiscale = p.ref_doctor
            LEFT JOIN Users du ON du.username = d.codiceFiscale
       \s""";

        List<MedicalCondition> conditions = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Doctor doctor = null;
                String dCf = rs.getString("d_cf");
                if (dCf != null) {
                    doctor = (Doctor) UserFactory.fromDatabase(
                            rs.getString("d_type"),
                            rs.getString("d_name"),
                            rs.getString("d_lastName"),
                            LocalDate.parse(rs.getString("d_dob")),
                            rs.getString("d_pb"),
                            rs.getString("d_nat"),
                            rs.getString("d_sex"),
                            rs.getString("d_tel"),
                            dCf,
                            rs.getString("d_pwd"),
                            rs.getString("d_email"),
                            rs.getString("d_type"),
                            0.0, false, false,
                            null, null, null
                    );
                }

                Patient patient = (Patient) UserFactory.fromDatabase(
                        rs.getString("p_type"),
                        rs.getString("p_name"),
                        rs.getString("p_lastName"),
                        LocalDate.parse(rs.getString("p_dob")),
                        rs.getString("p_pb"),
                        rs.getString("p_nat"),
                        rs.getString("p_sex"),
                        rs.getString("p_tel"),
                        rs.getString("p_cf"),
                        rs.getString("p_pwd"),
                        rs.getString("p_email"),
                        rs.getString("p_type"),
                        rs.getDouble("weight"),
                        rs.getInt("smoker") == 1,
                        rs.getInt("drinker") == 1,
                        rs.getString("risk_factor"),
                        doctor,
                        rs.getString("medical_notes")
                );

                MedicalCondition condition = new MedicalCondition(
                        rs.getInt("ID"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        LocalDateTime.now(),
                        patient
                );
                conditions.add(condition);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all medical conditions", e);
        }
        return conditions;
    }

    @Override
    public void update(MedicalCondition obj) {
        if (obj == null) {
            System.err.println("Can't update. MedicalCondition is null");
            return;
        }

        String sql = """
            UPDATE Patient_Health\s
            SET type = ?, name = ?, description = ?, start_date = ?, end_date = ?\s
            WHERE ID = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getType());
            stmt.setString(2, obj.getName());
            stmt.setString(3, obj.getDescription());
            stmt.setString(4, obj.getStart());
            stmt.setString(5, obj.getEnd());
            stmt.setInt(6, obj.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("MedicalCondition updated: ID =" + obj.getId());
            } else {
                System.out.println("No medical condition found with ID =" + obj.getId());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating medical condition", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Patient_Health WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            stmt.executeUpdate();
            System.out.println("MedicalCondition deleted: " + id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting medical condition", e);
        }
    }

    public List<MedicalCondition> getByPatient(String codiceFiscale) {
        String sql = """
            SELECT ph.ID, ph.type, ph.name, ph.description, ph.start_date, ph.end_date,\s
                   p.codiceFiscale AS p_cf, p.weight, p.smoker, p.drinker, p.medical_notes, p.risk_factor,
                   pu.name AS p_name, pu.last_name AS p_lastName, pu.dob AS p_dob, pu.place_birth AS p_pb,\s
                   pu.nationality AS p_nat, pu.sex AS p_sex, pu.tel_number AS p_tel, pu.email AS p_email, pu.password AS p_pwd, pu.type AS p_type,
                   d.codiceFiscale AS d_cf,
                   du.name AS d_name, du.last_name AS d_lastName, du.dob AS d_dob, du.place_birth AS d_pb,\s
                   du.nationality AS d_nat, du.sex AS d_sex, du.tel_number AS d_tel, du.email AS d_email, du.password AS d_pwd, du.type AS d_type
            FROM Patient_Health ph
            JOIN Patients p ON p.codiceFiscale = ph.patient
            JOIN Users pu ON pu.username = p.codiceFiscale
            LEFT JOIN Doctors d ON d.codiceFiscale = p.ref_doctor
            LEFT JOIN Users du ON du.username = d.codiceFiscale
            WHERE ph.patient = ?
       \s""";

        List<MedicalCondition> conditions = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codiceFiscale);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = null;
                    String dCf = rs.getString("d_cf");
                    if (dCf != null) {
                        doctor = (Doctor) UserFactory.fromDatabase(
                                rs.getString("d_type"),
                                rs.getString("d_name"),
                                rs.getString("d_lastName"),
                                LocalDate.parse(rs.getString("d_dob")),
                                rs.getString("d_pb"),
                                rs.getString("d_nat"),
                                rs.getString("d_sex"),
                                rs.getString("d_tel"),
                                dCf,
                                rs.getString("d_pwd"),
                                rs.getString("d_email"),
                                rs.getString("d_type"),
                                0.0, false, false,
                                null, null, null
                        );
                    }

                    Patient patient = (Patient) UserFactory.fromDatabase(
                            rs.getString("p_type"),
                            rs.getString("p_name"),
                            rs.getString("p_lastName"),
                            LocalDate.parse(rs.getString("p_dob")),
                            rs.getString("p_pb"),
                            rs.getString("p_nat"),
                            rs.getString("p_sex"),
                            rs.getString("p_tel"),
                            rs.getString("p_cf"),
                            rs.getString("p_pwd"),
                            rs.getString("p_email"),
                            rs.getString("p_type"),
                            rs.getDouble("weight"),
                            rs.getInt("smoker") == 1,
                            rs.getInt("drinker") == 1,
                            rs.getString("risk_factor"),
                            doctor,
                            rs.getString("medical_notes")
                    );

                    MedicalCondition condition = new MedicalCondition(
                            rs.getInt("ID"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("type"),
                            rs.getString("start_date"),
                            rs.getString("end_date"),
                            LocalDateTime.now(),
                            patient
                    );
                    conditions.add(condition);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving patient's medical condition", e);
        }
        return conditions;
    }
}