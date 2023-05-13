package SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite {

    Connection conn = null;

    String dbName = "";

    public SQLite(String dbName) {
        this.dbName = dbName;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbName + ".db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private void tryStatement(String sql) {
        try {
            Statement statement = conn.createStatement();
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private void createTableDimStock() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_stock (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "symbol VARCHAR(6),\n"
                + "name VARCHAR(50),\n"
                + "description TEXT,\n"
                + "market VARCHAR(30),\n"
                + "type VARCHAR(30),\n"
                + "";

    }
    private void createTableDimSector() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_sector (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(30));";
        tryStatement(sql);
    }
    private void createTableCountry() {
        String sql = "CREATE TABLE IF NOT EXISTS country (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(30));";
        tryStatement(sql);
    }
    private void createTableDimIndustry() {
        String sql = "CREATE TABLE IF NOT EXISTS industry (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(50));";
        tryStatement(sql);
    }
    private void createTableDimMarket() {
        String sql = "CREATE TABLE IF NOT EXISTS market (\n"
                + "id PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(10));";
        tryStatement(sql);
    }
    public void createTableUser() {
        String sql= "CREATE TABLE IF NOT EXISTS user (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "person_id VARCHAR(10),\n"
                + "password VARCHAR(250),\n"
                + "email VARCHAR(50)\n"
                + ");";
        tryStatement(sql);

    }

    public void createTablePortfolio() {
        String sql = "CREATE TABLE IF NOT EXISTS portfolio (\n"
                + "id PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(25)\n"
                + "created_at DATE,\n"
                + "FOREIGN KEY (user_id) REFERENCES user(id)\n"
                + ");";
        tryStatement(sql);

    }
}
