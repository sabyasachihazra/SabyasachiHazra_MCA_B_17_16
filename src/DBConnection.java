// Importing required packages
import java.sql.Connection;
import java.sql.DriverManager;

// DBConnection class
public class DBConnection {
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/clinic", "sabyasachi", "");
    }
}
