package utility;

import java.sql.*;

public class DBConnection {
	// obj
	private static Connection conn;
	private static final String URL = "jdbc:sqlite:VelyaLife.db";

    //private constructer: cannot instantiate
    private DBConnection() { }
    
    public static synchronized Connection getConnection() {
    	try {
        	Class.forName("org.sqlite.JDBC");
        	conn = DriverManager.getConnection(URL);
        }
        catch (ClassNotFoundException e) {
            System.out.println("JDBC not registered 🥹");
        }
        catch (SQLException e) {
        	System.out.println("Database not connected 🥹");
        }
        return conn; // get the only available object
    }
}
