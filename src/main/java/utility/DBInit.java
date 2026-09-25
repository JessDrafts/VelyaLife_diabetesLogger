package utility;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBInit {

    private static final Logger LOGGER = Logger.getLogger(DBInit.class.getName());

    public static void clearTables() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = OFF");

            stmt.executeUpdate("DELETE FROM Therapy");
            stmt.executeUpdate("DELETE FROM DailyReport");
            stmt.executeUpdate("DELETE FROM Patient_Health");
            stmt.executeUpdate("DELETE FROM MedLogHistory");
            stmt.executeUpdate("DELETE FROM Notifications");
            stmt.executeUpdate("DELETE FROM Patients");
            stmt.executeUpdate("DELETE FROM Doctors");
            stmt.executeUpdate("DELETE FROM Users");

            stmt.execute("PRAGMA foreign_keys = ON");

            System.out.println("All tables cleared successfully.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error clearing tables", e);
        }
    }

    public static void dropTable(String tableName) {
        if (tableName == null || !tableName.matches("^[a-zA-Z0-9_]+$")) {
            System.err.println("table name not valid");
            return;
        }

        String sql = "DROP TABLE IF EXISTS " + tableName;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("table " + tableName + " eliminated.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting table " + tableName, e);
        }
    }

    public static void launch() {

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("JDBC registered");
            System.out.println("Database connected 😁");

            // Foreign key activation
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error for foreign keys", e);
            }

            System.out.println("Creating tables...");

            // Create user table
            try (Statement stmt = conn.createStatement()) {
                String sql = """
                    CREATE TABLE IF NOT EXISTS Users (
                        name TEXT NOT NULL,
                        last_name TEXT NOT NULL,
                        dob TEXT NOT NULL,
                        place_birth TEXT NOT NULL,
                        nationality TEXT NOT NULL,
                        sex TEXT NOT NULL,
                        tel_number TEXT,
                        username TEXT PRIMARY KEY,
                        email TEXT NOT NULL,
                        password TEXT NOT NULL,
                        type TEXT NOT NULL
                    )
               """;
                stmt.executeUpdate(sql);
                System.out.println("Table Users created");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Table Users cannot be created", e);
            }

            // Create patient table
            try (Statement stmt = conn.createStatement()) {
                String sql = """
                    CREATE TABLE IF NOT EXISTS Patients (
                        codiceFiscale TEXT PRIMARY KEY,
                        weight REAL,
                        smoker INTEGER CHECK (Smoker IN (0, 1)),
                        drinker INTEGER CHECK (Drinker IN (0, 1)),
                        ref_doctor TEXT NOT NULL,
                        medical_notes TEXT,
                        risk_factor TEXT,
                        FOREIGN KEY (ref_doctor) REFERENCES Doctors(codiceFiscale) ON UPDATE CASCADE ON DELETE SET NULL,
                        FOREIGN KEY (codiceFiscale) REFERENCES Users(username) ON UPDATE CASCADE ON DELETE CASCADE
                    )
               """;
                stmt.executeUpdate(sql);
                System.out.println("Table Patients created");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Table Patients cannot be created", e);
            }

            // Create doctor table
            try (Statement stmt = conn.createStatement()) {
                String sql = """
                    CREATE TABLE IF NOT EXISTS Doctors (
                        codiceFiscale TEXT PRIMARY KEY,
                        FOREIGN KEY (codiceFiscale) REFERENCES Users(username) ON UPDATE CASCADE ON DELETE CASCADE
                    )
               """;
                stmt.executeUpdate(sql);
                System.out.println("Table Doctors created");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Table Doctors cannot be created", e);
            }

            try (Statement stmt = conn.createStatement()) {
                String sql = """
                    CREATE TABLE IF NOT EXISTS Notifications (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        message TEXT NOT NULL,
                        sender TEXT,
                        "user" TEXT,
                        createdAt TEXT NOT NULL,
                        seen INTEGER NOT NULL CHECK (Seen IN (0, 1)),
                        FOREIGN KEY ("user") REFERENCES Users(username) ON UPDATE CASCADE ON DELETE CASCADE
                    )
               """;
                stmt.executeUpdate(sql);
                System.out.println("Table Notifications created");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Table Notifications cannot be created", e);
            }

            // Create therapy table
            try (Statement stmt = conn.createStatement()) {
                String sql = """
                    CREATE TABLE IF NOT EXISTS Therapy (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        prescription TEXT NOT NULL,
                        daily_dose REAL NOT NULL,
                        amount_intaken REAL NOT NULL,
                        instructions TEXT NOT NULL,
                        start_date TEXT NOT NULL,
                        end_date TEXT,
                        patient TEXT NOT NULL,
                        doctor TEXT NOT NULL,
                        FOREIGN KEY (doctor) REFERENCES Doctors(codiceFiscale) ON UPDATE CASCADE ON DELETE CASCADE,
                        FOREIGN KEY (patient) REFERENCES Patients(codiceFiscale) ON UPDATE CASCADE ON DELETE CASCADE
                    )
               """;
                stmt.executeUpdate(sql);
                System.out.println("Table Therapy created");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Table therapy cannot be created", e);
            }

            // Create patient's health history table
            try (Statement stmt = conn.createStatement()) {
                String sql = """
                    CREATE TABLE IF NOT EXISTS Patient_Health (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        type TEXT NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        start_date TEXT NOT NULL,
                        end_date TEXT,
                        patient TEXT NOT NULL,
                        FOREIGN KEY (patient) REFERENCES Patients(codiceFiscale) ON UPDATE CASCADE ON DELETE CASCADE
                    )
               """;
                stmt.executeUpdate(sql);
                System.out.println("Table Patient_Health created");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Table Patient_Health cannot be created", e);
            }

            // Create DailyReport table
            try (Statement stmt = conn.createStatement()) {
                String sql = """
                    CREATE TABLE IF NOT EXISTS DailyReport (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        blood_sugar_level REAL,
                        patient TEXT NOT NULL,
                        date_time TEXT NOT NULL,
                        before_meal INTEGER CHECK (before_Meal IN (0, 1)),
                        drugs TEXT,
                        amount INTEGER,
                        FOREIGN KEY (patient) REFERENCES Patients(codiceFiscale) ON UPDATE CASCADE ON DELETE CASCADE
                    )
               """;
                stmt.executeUpdate(sql);
                System.out.println("Table DailyReport created");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Table DailyReport cannot be created", e);
            }

            // Create MedLogHistory table
            try (Statement stmt = conn.createStatement()) {
                String sql = """
                    CREATE TABLE IF NOT EXISTS MedLogHistory (
                        ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        patient TEXT NOT NULL,
                        doctor TEXT NOT NULL,
                        createdAt TEXT NOT NULL,
                        content TEXT NOT NULL,
                        FOREIGN KEY (doctor) REFERENCES Doctors(codiceFiscale) ON UPDATE CASCADE ON DELETE SET NULL,
                        FOREIGN KEY (patient) REFERENCES Patients(codiceFiscale) ON UPDATE CASCADE ON DELETE CASCADE
                    )
               """;
                stmt.executeUpdate(sql);
                System.out.println("Table MedLogHistory created");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Table MedLogHistory cannot be created", e);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection error", e);
        }
    }
}