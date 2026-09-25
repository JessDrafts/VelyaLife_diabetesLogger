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

import model.AbstractUser;
import utility.AdminCreator;
import model.Doctor;
import model.Patient;
import utility.DBConnection;

public class UserRepository implements DAOServices<AbstractUser> {

	private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

	@Override
	public void save(AbstractUser obj) {
		String sqlUsers = """
           INSERT OR REPLACE INTO Users\s
           (name, last_name, dob, place_birth, nationality, sex, tel_number, username, email, password, type)\s
           VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
      \s""";

		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement stmtUsers = conn.prepareStatement(sqlUsers)) {
			stmtUsers.setString(1, obj.getName());
			stmtUsers.setString(2, obj.getLastName());
			stmtUsers.setString(3, obj.getDateBirth().toString());
			stmtUsers.setString(4, obj.getPlaceBirth());
			stmtUsers.setString(5, obj.getNationality());
			stmtUsers.setString(6, obj.getSex());
			stmtUsers.setString(7, obj.getTelNumber());
			stmtUsers.setString(8, obj.getUsername());
			stmtUsers.setString(9, obj.getEmail());
			stmtUsers.setString(10, obj.getPassword());
			stmtUsers.setString(11, obj.getType());
			stmtUsers.executeUpdate();
			System.out.println("User inserted correctly");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error saving User", e);
		}
	}

	@Override
	public AbstractUser getById(String username) {
		String sql = """
           SELECT *\s
           FROM Users\s
           WHERE username = ?
      \s""";

		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, username);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String name = rs.getString("name");
					String lastName = rs.getString("last_name");
					String dob = rs.getString("dob");
					String placeBirth = rs.getString("place_birth");
					String nationality = rs.getString("nationality");
					String sex = rs.getString("sex");
					String telNumber = rs.getString("tel_number");
					String user = rs.getString("username");
					String email = rs.getString("email");
					String password = rs.getString("password");
					String role = rs.getString("type");

					switch (role.toUpperCase()) {
						case "DOCTOR": {
							return new Doctor(name, lastName, LocalDate.parse(dob), placeBirth, nationality, sex, telNumber, user, password, email, role);
						}
						case "PATIENT": {
							String patientSql = """
                          SELECT *\s
                          FROM Patients\s
                          WHERE codiceFiscale = ?
                     \s""";
							try (PreparedStatement stmtPatient = conn.prepareStatement(patientSql)) {
								stmtPatient.setString(1, user);
								try (ResultSet rsPatient = stmtPatient.executeQuery()) {
									if (rsPatient.next()) {
										double weight = rsPatient.getDouble("weight");
										boolean smoker = rsPatient.getInt("smoker") == 1;
										boolean drinker = rsPatient.getInt("drinker") == 1;
										String refDoctorCf = rsPatient.getString("ref_doctor");
										String riskFactor = rsPatient.getString("risk_factor");
										String doctorNotes = rsPatient.getString("medical_notes");

										Doctor refDoctor = null;
										if (refDoctorCf != null && !refDoctorCf.isEmpty()) {
											AbstractUser docUser = getById(refDoctorCf);
											if (docUser instanceof Doctor) {
												refDoctor = (Doctor) docUser;
											}
										}

										return new Patient(
												name, lastName, LocalDate.parse(dob), placeBirth, nationality, sex,
												weight, smoker, drinker, riskFactor, refDoctor, doctorNotes,
												telNumber, user, password, email, role
										);
									}
								}
							}
							break;
						}
						case "ADMIN":
							return new AdminCreator(user, password, email);

						default:
							System.err.println("User type don't exist: " + role);
							return null;
					}
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving User by ID", e);
		}
		return null;
	}

	@Override
	public List<AbstractUser> getAll() {
		List<AbstractUser> users = new ArrayList<>();
		String sql = """
           SELECT username\s
           FROM Users\s
           WHERE type <> 'Admin'
      \s""";

		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String username = rs.getString("username");
				AbstractUser user = getById(username);
				if (user != null) {
					users.add(user);
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error retrieving all Users", e);
		}
		return users;
	}

	@Override
	public void update(AbstractUser obj) {
		StringBuilder sql = new StringBuilder("UPDATE Users SET ");
		List<Object> params = new ArrayList<>();

		if (obj.getName() != null) {
			sql.append("name = ?, ");
			params.add(obj.getName());
		}
		if (obj.getLastName() != null) {
			sql.append("last_name = ?, ");
			params.add(obj.getLastName());
		}
		if (obj.getDateBirth() != null) {
			sql.append("dob = ?, ");
			params.add(obj.getDateBirth().toString());
		}
		if (obj.getPlaceBirth() != null) {
			sql.append("place_birth = ?, ");
			params.add(obj.getPlaceBirth());
		}
		if (obj.getNationality() != null) {
			sql.append("nationality = ?, ");
			params.add(obj.getNationality());
		}
		if (obj.getSex() != null) {
			sql.append("sex = ?, ");
			params.add(obj.getSex());
		}
		if (obj.getTelNumber() != null) {
			sql.append("tel_number = ?, ");
			params.add(obj.getTelNumber());
		}
		if (obj.getEmail() != null) {
			sql.append("email = ?, ");
			params.add(obj.getEmail());
		}
		if (obj.getPassword() != null) {
			sql.append("password = ?, ");
			params.add(obj.getPassword());
		}

		if (params.isEmpty()) {
			System.out.println("No updates for user: " + obj.getUsername());
			return;
		}

		sql.setLength(sql.length() - 2);
		sql.append(" WHERE username = ?");
		params.add(obj.getUsername());

		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++) {
				stmt.setObject(i + 1, params.get(i));
			}

			int updated = stmt.executeUpdate();
			if (updated > 0) {
				System.out.println("User Updated: " + obj.getUsername());
			} else {
				System.out.println("No user found with username: " + obj.getUsername());
			}

		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error during user update", e);
		}
	}

	@Override
	public void delete(String id) {
		String sql = """
           DELETE FROM Users\s
           WHERE username = ? AND type <> 'Admin'
      \s""";

		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, id);
			stmt.executeUpdate();
			System.out.println("User deleted: " + id);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error during user elimination", e);
		}
	}

	public boolean usernameExists(String username) {
		String sql = """
            SELECT 1\s
            FROM Users\s
            WHERE username = ?
       \s""";

		try (Connection conn = DBConnection.getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Error checking if username exists", e);
		}
		return false;
	}
}