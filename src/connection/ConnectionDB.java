package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    private static final String URL = "jdbc:mysql://localhost:3306/cafeteria_db";
    private static final String USER = "root";
    private static final String PASSWORD = "senha";

    // Create a new connection every time it is called
    public static Connection getDatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Registrar o driver
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}