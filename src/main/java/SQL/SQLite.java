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
            System.out.println(e.getMessage() + e.getErrorCode() + e.getSQLState());

        }
    }
    public void createTableDimStock() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_stock (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "symbol VARCHAR(6),\n"
                + "name VARCHAR(50),\n"
                + "description TEXT,\n"
                + "country_id INTEGER,\n"
                + "sector_id INTEGER,\n"
                + "industry_id INTEGER,\n"
                + "FOREIGN KEY (country_id) REFERENCES dim_country(id),\n"
                + "FOREIGN KEY (sector_id) REFERENCES dim_sector(id),\n"
                + "FOREIGN KEY (industry_id) REFERENCES dim_industry(id));";


        tryStatement(sql);
    }
    public void createTableFactTransaction() {
        String sql = "CREATE TABLE IF NOT EXISTS fact_transaction (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "portfolio_id INTEGER NOT NULL,\n"
                + "stock_id INTEGER NOT NULL,\n"
                + "date DATE,\n"
                + "FOREIGN KEY (portfolio_id) REFERENCES dim_portfolio(id),\n"
                + "FOREIGN KEY (stock_id) REFERENCES fact_StockPrice(stock_id),\n"
                + "FOREIGN KEY (date) REFERENCES fact_StockPrice(date),\n"
                + "UNIQUE (stock_id, date));";
        tryStatement(sql);
    }
    public void createTableDimCurrency() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_currency (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "currency VARCHAR(3));";
        tryStatement(sql);
    }
    public void createTableFactStockPrice() {
        String sql = "CREATE TABLE IF NOT EXISTS fact_StockPrice (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "stock_id INTEGER NOT NULL,\n"
                + "date DATE,\n"
                + "price DECIMAL,\n"
                + "currency_id INTEGER NOT NULL,\n"
                + "FOREIGN KEY (stock_id) REFERENCES dim_stock(id),\n"
                + "FOREIGN KEY (currency_id) REFERENCES dim_currency(id));";
        tryStatement(sql);
    }
    public void createTablePortfolio() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_portfolio (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(30),\n"
                + "created_at DATE,\n"
                + "user_id INTEGER NOT NULL,\n"
                + "FOREIGN KEY (user_id) REFERENCES dim_user(id)\n"
                + ");";
        tryStatement(sql);

    }
    public void createTableDimSector() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_sector (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(30));";
        tryStatement(sql);
    }
    public void createTableCountry() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_country (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(30));";
        tryStatement(sql);
    }
    public void createTableDimIndustry() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_industry (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(50));";
        tryStatement(sql);
    }
    public void createTableDimMarket() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_market (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(10));";
        tryStatement(sql);
    }
    public void createTableUser() {
        String sql= "CREATE TABLE IF NOT EXISTS dim_user (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "person_id VARCHAR(10),\n"
                + "password TEXT,\n"
                + "email VARCHAR(50)\n"
                + ");";
        tryStatement(sql);

    }




}
