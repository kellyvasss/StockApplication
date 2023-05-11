package SQL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {

    public void main() {
        String url = "jdbc:mysql://localhost:3306/";
        String username = "root";
        String password = "your_password";
        String databaseName = "your_database";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            // Skapa databas
            String createDatabaseQuery = "CREATE DATABASE " + databaseName;
            statement.executeUpdate(createDatabaseQuery);
            System.out.println("Databasen " + databaseName + " skapades.");

            // Använd den nya databasen
            String useDatabaseQuery = "USE " + databaseName;
            statement.executeUpdate(useDatabaseQuery);
            System.out.println("Använder databasen " + databaseName + ".");

            // Annan kod för att utföra önskade operationer på databasen

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
