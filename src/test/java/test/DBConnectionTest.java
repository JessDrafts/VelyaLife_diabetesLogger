package test;

import utility.DBConnection;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

class DBConnectionTest {

    @Test
    void getConnection() {
        Connection connection = DBConnection.getConnection();
        assertNotNull(connection, "The database connection should not be null");
    }
}