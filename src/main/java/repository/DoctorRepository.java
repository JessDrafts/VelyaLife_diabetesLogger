package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Doctor;
import utility.DBConnection;

public class DoctorRepository implements DAOServices<Doctor> {

	private static final Logger LOGGER = Logger.getLogger(DoctorRepository.class.getName());

	@Override
	public void save(Doctor obj) {
		try (Connection conn = DBConnection.getConnection()) {
			String sqlDoctor = "INSERT OR REPLACE INTO Doctors (codiceFiscale) VALUES (?)";
			try (PreparedStatement stmtDoctor = conn.prepareStatement(sqlDoctor)) {
				stmtDoctor.setString(1, obj.getCodiceFiscale());
				stmtDoctor.executeUpdate();
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error saving doctor", e);
		}
	}

	@Override
	public Doctor getById(String codiceFiscale) {
		String sql = "SELECT u.name, u.last_name, u.dob, u.place_birth, u.nationality, " +
				"u.sex, u.tel_number, u.username, u.email, u.password, u.type " +
				"FROM Doctors d " +
				"JOIN Users u ON u.username = d.codiceFiscale " +
				"WHERE d.codiceFiscale = ?";

		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, codiceFiscale);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String name = rs.getString("name");
					String lastName = rs.getString("last_name");
					String dobStr = rs.getString("dob");
					LocalDate dob = (dobStr != null && !dobStr.isEmpty()) ? LocalDate.parse(dobStr) : null;
					String placeBirth = rs.getString("place_birth");
					String nationality = rs.getString("nationality");
					String sex = rs.getString("sex");
					String telNumber = rs.getString("tel_number");
					String user = rs.getString("username");
					String email = rs.getString("email");
					String password = rs.getString("password");
					String type = rs.getString("type");

					return new Doctor(
							name, lastName, dob, placeBirth, nationality, sex,
							telNumber, user, password, email, type
					);
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving doctor", e);
		}
		return null;
	}

	@Override
	public List<Doctor> getAll() {
		List<Doctor> doctors = new ArrayList<>();
		String sql = "SELECT d.codiceFiscale FROM Doctors d";

		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String cf = rs.getString("codiceFiscale");
				Doctor doc = getById(cf);
				if (doc != null) {
					doctors.add(doc);
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving all doctor", e);
		}
		return doctors;
	}

	@Override
	public void update(Doctor d) {
		System.out.println("Doctor updated via UserRepository context: " + d.getCodiceFiscale());
	}

	@Override
	public void delete(String id) {
		String sql = "DELETE FROM Doctors WHERE codiceFiscale = ?";
		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, id);
			stmt.executeUpdate();
			System.out.println("Doctor deleted from Doctors table: " + id);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error deleting doctor", e);
		}
	}
}