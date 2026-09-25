package repository;

import model.Doctor;
import model.Patient;
import model.Therapy;
import utility.DBConnection;
import utility.UserFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TherapyRepository implements DAOServices<Therapy> {

    private static final Logger LOGGER = Logger.getLogger(TherapyRepository.class.getName());

    @Override
    public void save(Therapy obj) {
        String sql = """
            INSERT OR REPLACE INTO Therapy\s
            (prescription, daily_dose, amount_intaken, instructions, start_date, end_date, patient, doctor)\s
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, obj.getPrescription());
            stmt.setDouble(2, obj.getDailyDose());
            stmt.setDouble(3, obj.getAmountIntaken());
            stmt.setString(4, obj.getInstructions());
            stmt.setString(5, obj.getStartDate());
            stmt.setString(6, obj.getEndDate());
            stmt.setString(7, obj.getPatient().getCodiceFiscale());
            stmt.setString(8, obj.getDoctor().getCodiceFiscale());
            stmt.executeUpdate();
            System.out.println("Therapy inserted correctly");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving Therapy", e);
        }
    }

    @Override
    public Therapy getById(String id) {
        String sql = """
            SELECT t.ID, t.prescription, t.daily_dose, t.amount_intaken, t.instructions,\s
                   t.start_date, t.end_date,
                   p.codiceFiscale AS p_cf, p.weight, p.smoker, p.drinker, p.medical_notes, p.risk_factor,
                   pu.name AS p_name, pu.last_name AS p_last_name, pu.dob AS p_dob, pu.place_birth AS p_pb,\s
                   pu.nationality AS p_nat, pu.sex AS p_sex, pu.tel_number AS p_tel, pu.email AS p_email, pu.password AS p_pwd, pu.type AS p_type,
                   d.codiceFiscale AS d_cf,
                   du.name AS d_name, du.last_name AS d_last_name, du.dob AS d_dob, du.place_birth AS d_pb,\s
                   du.nationality AS d_nat, du.sex AS d_sex, du.tel_number AS d_tel, du.email AS d_email, du.password AS d_pwd, du.type AS d_type
            FROM Therapy t
            JOIN Patients p ON p.codiceFiscale = t.patient
            JOIN Users pu ON pu.username = p.codiceFiscale
            JOIN Doctors d ON d.codiceFiscale = t.doctor
            JOIN Users du ON du.username = d.codiceFiscale
            WHERE t.ID = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(id));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Doctor doctor = (Doctor) UserFactory.fromDatabase(
                            rs.getString("d_type"),
                            rs.getString("d_name"),
                            rs.getString("d_last_name"),
                            LocalDate.parse(rs.getString("d_dob")),
                            rs.getString("d_pb"),
                            rs.getString("d_nat"),
                            rs.getString("d_sex"),
                            rs.getString("d_tel"),
                            rs.getString("d_cf"),
                            rs.getString("d_pwd"),
                            rs.getString("d_email"),
                            rs.getString("d_type"),
                            0.0, false, false,
                            null, null, null
                    );

                    Patient patient = (Patient) UserFactory.fromDatabase(
                            rs.getString("p_type"),
                            rs.getString("p_name"),
                            rs.getString("p_last_name"),
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

                    return new Therapy(
                            rs.getInt("ID"),
                            rs.getString("prescription"),
                            rs.getDouble("daily_dose"),
                            rs.getDouble("amount_intaken"),
                            rs.getString("instructions"),
                            patient,
                            doctor,
                            rs.getString("start_date"),
                            rs.getString("end_date")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving Therapy by ID", e);
        }
        return null;
    }

    @Override
    public List<Therapy> getAll() {
        String sql = """
            SELECT t.ID, t.prescription, t.daily_dose, t.amount_intaken, t.instructions,\s
                   t.start_date, t.end_date,
                   p.codiceFiscale AS p_cf, p.weight, p.smoker, p.drinker, p.medical_notes, p.risk_factor,
                   pu.name AS p_name, pu.last_name AS p_last_name, pu.dob AS p_dob, pu.place_birth AS p_pb,\s
                   pu.nationality AS p_nat, pu.sex AS p_sex, pu.tel_number AS p_tel, pu.email AS p_email, pu.password AS p_pwd, pu.type AS p_type,
                   d.codiceFiscale AS d_cf,
                   du.name AS d_name, du.last_name AS d_last_name, du.dob AS d_dob, du.place_birth AS d_pb,\s
                   du.nationality AS d_nat, du.sex AS d_sex, du.tel_number AS d_tel, du.email AS d_email, du.password AS d_pwd, du.type AS d_type
            FROM Therapy t
            JOIN Patients p ON p.codiceFiscale = t.patient
            JOIN Users pu ON pu.username = p.codiceFiscale
            JOIN Doctors d ON d.codiceFiscale = t.doctor
            JOIN Users du ON du.username = d.codiceFiscale
       \s""";

        List<Therapy> therapies = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Doctor doctor = (Doctor) UserFactory.fromDatabase(
                        rs.getString("d_type"),
                        rs.getString("d_name"),
                        rs.getString("d_last_name"),
                        LocalDate.parse(rs.getString("d_dob")),
                        rs.getString("d_pb"),
                        rs.getString("d_nat"),
                        rs.getString("d_sex"),
                        rs.getString("d_tel"),
                        rs.getString("d_cf"),
                        rs.getString("d_pwd"),
                        rs.getString("d_email"),
                        rs.getString("d_type"),
                        0.0, false, false,
                        null, null, null
                );

                Patient patient = (Patient) UserFactory.fromDatabase(
                        rs.getString("p_type"),
                        rs.getString("p_name"),
                        rs.getString("p_last_name"),
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

                Therapy therapy = new Therapy(
                        rs.getInt("ID"),
                        rs.getString("prescription"),
                        rs.getDouble("daily_dose"),
                        rs.getDouble("amount_intaken"),
                        rs.getString("instructions"),
                        patient,
                        doctor,
                        rs.getString("start_date"),
                        rs.getString("end_date")
                );
                therapies.add(therapy);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all Therapies", e);
        }
        return therapies;
    }

    @Override
    public void update(Therapy therapy) {
        if (therapy == null) {
            System.err.println("Can't update. Therapy is null");
            return;
        }

        String sql = """
            UPDATE Therapy\s
            SET prescription = ?, daily_dose = ?, amount_intaken = ?,\s
                instructions = ?, start_date = ?, end_date = ?\s
            WHERE ID = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, therapy.getPrescription());
            stmt.setDouble(2, therapy.getDailyDose());
            stmt.setDouble(3, therapy.getAmountIntaken());
            stmt.setString(4, therapy.getInstructions());
            stmt.setString(5, therapy.getStartDate());
            stmt.setString(6, therapy.getEndDate());
            stmt.setInt(7, therapy.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Therapy updated: ID =" + therapy.getId());
            } else {
                System.out.println("No therapy found with ID =" + therapy.getId());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during therapy update", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = """
            DELETE FROM Therapy\s
            WHERE ID = ?
       \s""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            stmt.executeUpdate();
            System.out.println("Therapy deleted: " + id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting therapy", e);
        }
    }

    public List<Therapy> getByPatient(String codiceFiscale) {
        String sql = """
            SELECT t.ID, t.prescription, t.daily_dose, t.amount_intaken, t.instructions,\s
                   t.start_date, t.end_date,
                   p.codiceFiscale AS p_cf, p.weight, p.smoker, p.drinker, p.medical_notes, p.risk_factor,
                   pu.name AS p_name, pu.last_name AS p_last_name, pu.dob AS p_dob, pu.place_birth AS p_pb,\s
                   pu.nationality AS p_nat, pu.sex AS p_sex, pu.tel_number AS p_tel, pu.email AS p_email, pu.password AS p_pwd, pu.type AS p_type,
                   d.codiceFiscale AS d_cf,
                   du.name AS d_name, du.last_name AS d_last_name, du.dob AS d_dob, du.place_birth AS d_pb,\s
                   du.nationality AS d_nat, du.sex AS d_sex, du.tel_number AS d_tel, du.email AS d_email, du.password AS d_pwd, du.type AS d_type
            FROM Therapy t
            JOIN Patients p ON p.codiceFiscale = t.patient
            JOIN Users pu ON pu.username = p.codiceFiscale
            JOIN Doctors d ON d.codiceFiscale = t.doctor
            JOIN Users du ON du.username = d.codiceFiscale
            WHERE t.patient = ?
       \s""";

        List<Therapy> therapies = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codiceFiscale);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = (Doctor) UserFactory.fromDatabase(
                            rs.getString("d_type"),
                            rs.getString("d_name"),
                            rs.getString("d_last_name"),
                            LocalDate.parse(rs.getString("d_dob")),
                            rs.getString("d_pb"),
                            rs.getString("d_nat"),
                            rs.getString("d_sex"),
                            rs.getString("d_tel"),
                            rs.getString("d_cf"),
                            rs.getString("d_pwd"),
                            rs.getString("d_email"),
                            rs.getString("d_type"),
                            0.0, false, false,
                            null, null, null
                    );

                    Patient patient = (Patient) UserFactory.fromDatabase(
                            rs.getString("p_type"),
                            rs.getString("p_name"),
                            rs.getString("p_last_name"),
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

                    Therapy therapy = new Therapy(
                            rs.getInt("ID"),
                            rs.getString("prescription"),
                            rs.getDouble("daily_dose"),
                            rs.getDouble("amount_intaken"),
                            rs.getString("instructions"),
                            patient,
                            doctor,
                            rs.getString("start_date"),
                            rs.getString("end_date")
                    );
                    therapies.add(therapy);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving Therapies by patient", e);
        }
        return therapies;
    }
}