package SQL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {

    Connection connection = null;
    String url = "jdbc:mysql://127.0.0.1:3306/?user=";

    String username = "";
    //String password ;
   // String dbName = "";

    public MySQL(String username, String password) {

        this.username = username;
        String passwordd = "" + password;

        try {

            connection = DriverManager.getConnection(url + username + passwordd);
           // Statement statement = connection.createStatement();
           // statement.executeUpdate("CREATE DATABASE IF NOT EXISTS mydatabase");
            //System.out.println("Anslutning etablerad och databas skapad");
            System.out.println(url + username + password);
            //Statement statement = connection.createStatement();
           // connection.createStatement().execute("SELECT * FROM sql_store;");
           // String sql = "CREATE DATABASE test";
            //statement.executeUpdate(sql);
            System.out.println("HEJ det funkar");
            connection.close();

        } catch (SQLException e) {
            e.getMessage();
            System.out.println(url + username + password);
            System.out.println(e.getMessage());
        }
    }

}
