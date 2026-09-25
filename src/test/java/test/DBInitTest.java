package test;

import utility.DBConnection;
import utility.DBInit;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class DBInitTest {

    @Test
    void launch() {
        // Run database initialization to create tables
        DBInit.launch();

        // Verify that core tables were created successfully in SQLite
        try (Connection conn = DBConnection.getConnection()) {
            assertNotNull(conn, "Connection should be established");
            DatabaseMetaData meta = conn.getMetaData();

            // Check Users table existence
            try (ResultSet rs = meta.getTables(null, null, "Users", null)) {
                assertTrue(rs.next(), "Table Users should exist");
            }

            // Check Patients table existence
            try (ResultSet rs = meta.getTables(null, null, "Patients", null)) {
                assertTrue(rs.next(), "Table Patients should exist");
            }

            // Check Doctors table existence
            try (ResultSet rs = meta.getTables(null, null, "Doctors", null)) {
                assertTrue(rs.next(), "Table Doctors should exist");
            }

        } catch (SQLException e) {
            fail("Database inspection failed: " + e.getMessage());
        }
    }

    @Test
    void dropTable() {
        // First ensure tables exist
        DBInit.launch();

        // Drop a specific table safely using the utility method
        DBInit.dropTable("Notifications");

        // Verify that the Notifications table no longer exists
        try (Connection conn = DBConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null, "Notifications", null)) {
                assertFalse(rs.next(), "Table Notifications should have been dropped");
            }
        } catch (SQLException e) {
            fail("Database inspection failed during drop test: " + e.getMessage());
        }
    }
}