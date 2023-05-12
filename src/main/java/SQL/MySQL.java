package SQL;
import java.sql.*;

public class MySQL {

    Connection connection = null;
    String url = "jdbc:mysql://127.0.0.1:3306/?user=";


    String username = "";
    //String password ;
   // String dbName = "";

    public MySQL(String username, String password) {

        this.username = username;
        String passwordd = "&password=" + password;

        try {

            connection = DriverManager.getConnection(url , "newuser" , "roott");
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
            //System.out.println(url + username + "sup3rFeet18");
            System.out.println(e.getMessage());
        }
    }
    public void createTable() {

    }
    public void insertUser(User user) {

        try {
            PreparedStatement pstm = connection.prepareStatement(
                    "INSERT INTO user (person_number, password, email) VALUES (?, ?, ?)");
            pstm.setString(1, user.getNumber());
            pstm.setString(2, user.getPassword());
        }
    }

}
