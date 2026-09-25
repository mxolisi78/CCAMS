package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = 
        "jdbc:mysql://localhost:3306/taste_haven_rms?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    
    private static final String USER = "root";
    private static final String PASSWORD = "Mxolisi@78"; // CHANGE THIS TO YOUR MYSQL PASSWORD
    
    private static Connection connection = null;
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Database connected successfully!");
            } catch (ClassNotFoundException e) {
                System.err.println("❌ MySQL JDBC Driver not found!");
                System.err.println("Please add mysql-connector-j-*.jar to your project libraries.");
                throw new SQLException("Driver not found", e);
            } catch (SQLException e) {
                System.err.println("❌ Database connection failed: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("✅ Database connection closed.");
            } catch (SQLException e) {
                System.err.println("❌ Error closing connection: " + e.getMessage());
            }
        }
    }
    
    // Test method - run this to verify connection
    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            System.out.println("✅ Connection successful!");
            closeConnection();
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }
}