import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;

public class RandomTest {
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        System.out.println("Hello world!");
    }

    public static void connect() {
        String connectionUrl = "jdbc:mysql://localhost:3306/FlyByNight";
        String userPass = "root";
        testConnection(connectionUrl, userPass);
    }

    public static boolean testConnection(String connectionUrl, String userPass) {
        try {
            Connection connection = DriverManager.getConnection(connectionUrl, userPass, userPass);
            connection.close();
            System.out.println("Connection successful!");
            return true;
        } catch (SQLException e) {
            System.out.println("Connection failed! in Data.java");
            e.printStackTrace();
            return false;
        }
    }
}
