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
                + "FOREIGN KEY (country_id) REFERENCES dim_country(id),\n"
                + "FOREIGN KEY (sector_id) REFERENCES dim_sector(id),\n"
                + "FOREIGN KEY (industry_id) REFERENCES dim_industry(id));";
        tryStatement(sql);
    }
    private void createTableFactTransaction() {
        String sql = "CREATE TABLE IF NOT EXISTS fact_transaction (\n"
                + "id PRIMARY KEY AUTOINCREMENT,\n"
                + "FOREIGN KEY (portfolio_id) REFERENCES dim_portfolio(id),\n"
                + "FOREIGN KEY (stock_id) REFERENCES dim_stock(id),\n"
                + "FOREIGN KEY (price) REFERENCES fact_StockPrice(date),\n"
                + "quantity INTEGER,\n"
                + "date DATE);";
        tryStatement(sql);
    }
    private void createTableDimCurrency() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_currency (\n"
                + "id PRIMARY KEY AUTOINCREMENT,\n"
                + "currency VARCHAR(3));";
        tryStatement(sql);
    }
    private void createTableFactStockPrice() {
        String sql = "CREATE TABLE IF NOT EXISTS fact_StockPrice (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "FOREIGN KEY (stock_id) REFERENCES dim_stock(id),\n"
                + "date DATE,\n"
                + "price DECIMAL,\n"
                + "FOREIGN KEY (currency_id) REFERENCES dim_currency(id));";
        tryStatement(sql);
    }
    public void createTablePortfolio() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_portfolio (\n"
                + "id PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(25)\n"
                + "created_at DATE,\n"
                + "FOREIGN KEY (user_id) REFERENCES user(id)\n"
                + ");";
        tryStatement(sql);

    }
    private void createTableDimSector() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_sector (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(30));";
        tryStatement(sql);
    }
    private void createTableCountry() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_country (\n"
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


}
