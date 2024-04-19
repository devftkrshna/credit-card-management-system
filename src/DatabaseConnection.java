import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://sql6.freemysqlhosting.net:3306/sql6697169";
            String username = "sql6697169";
            String password = "zaEfFHrWHV";
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
