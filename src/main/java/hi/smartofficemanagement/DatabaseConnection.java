package hi.smartofficemanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection conn;

    private DatabaseConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/smartoffice"; // Replace with your database name
            String user = "root"; // Replace with your database username
            String password = "password"; // Replace with your database password
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return conn;
    }
}
