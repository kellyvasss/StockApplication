package SQL;

import stock.Portfolio;
import stock.Stock;
import user.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneOffset;

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
                + "symbol VARCHAR(6) UNIQUE,\n"
                + "name VARCHAR(50),\n"
                + "description TEXT,\n"
                + "market_id INTEGER,\n"
                + "country_id INTEGER,\n"
                + "sector_id INTEGER,\n"
                + "industry_id INTEGER,\n"
                + "FOREIGN KEY (market_id) REFERENCES dim_market(id),\n"
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
                + "FOREIGN KEY (date) REFERENCES fact_StockPrice(date)\n"
                + ");";
        tryStatement(sql);
    }
    public void createTableDimCurrency() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_currency (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "currency VARCHAR(3) UNIQUE);";
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
                + "name VARCHAR(30) UNIQUE);";
        tryStatement(sql);
    }
    public void createTableCountry() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_country (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(30) UNIQUE);";
        tryStatement(sql);
    }
    public void createTableDimIndustry() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_industry (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(50) UNIQUE);";
        tryStatement(sql);
    }
    public void createTableDimMarket() {
        String sql = "CREATE TABLE IF NOT EXISTS dim_market (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(10) UNIQUE);";
        tryStatement(sql);
    }
    public void createTableUser() {
        String sql= "CREATE TABLE IF NOT EXISTS dim_user (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "person_id VARCHAR(10) UNIQUE,\n"
                + "password TEXT,\n"
                + "email VARCHAR(50),\n"
                + "pas_salt VARCHAR(16),"
                + "id_salt VARCHAR(16)"
                + ");";
        tryStatement(sql);
    }
    private Integer security(User user) {
        String sql = "SELECT id FROM dim_user WHERE person_id=?";
        Integer sec = null;
        try {
            PreparedStatement prepared = conn.prepareStatement(sql);
            prepared.setString(1, user.getPerson_id());
            ResultSet rs = prepared.executeQuery();
            sec = rs.getInt("id");
        } catch (SQLException e) {
            System.out.println("fel med security metoden. " + e.getMessage());
            return sec;
        }
        return sec;
    }
    public Boolean insertUser(User user) {
        Boolean succes = true;
        String sql = "INSERT INTO dim_user(person_id, password, pas_salt, id_salt) VALUES(?,?,?,?)";
        try {
            PreparedStatement prepared = conn.prepareStatement(sql);
            prepared.setString(1, user.getPerson_id());
            prepared.setString(2,user.getPassword());
            prepared.setString(3, user.getPasSalt());
            prepared.setString(4, user.getIdSalt());
            prepared.executeUpdate();
        } catch (SQLException e) {
            System.out.println("fel med att lägga till user i dim_user. " + e.getMessage());
            succes = false;
        } return succes;
    }
    public Boolean insertPortfolio(Portfolio portfolio, User user) {
        Boolean succes = true;
        LocalDate today = LocalDate.now();
        Date date = Date.valueOf(today);

        String sql = "INSERT INTO dim_portfolio(name, created_at, user_id) VALUES(?, ?, ?)";
        try {
            PreparedStatement prepared = conn.prepareStatement(sql);
            prepared.setString(1, portfolio.getName());
            prepared.setString(2, String.valueOf(date));
            prepared.setInt(3, security(user));
            prepared.executeUpdate();
        } catch (SQLException e) {
            System.out.println("fel med antagligen user implementationen... " + e.getMessage());
            succes = false;
        } return succes;
    }
    public Integer insertMarket(Stock stock) {
        String sql = "SELECT id FROM dim_market WHERE name=?";
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, stock.getExchange());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() + " Nu är den inne i första catch");
            try {
                String sql1 = "INSERT INTO dim_market(name) VALUES(?)";
                PreparedStatement p = conn.prepareStatement(sql1);
                p.setString(1,stock.getExchange());
                p.executeUpdate();
                String s = "SELECT count(*) id FROM dim_market;";
                p = conn.prepareStatement(s);
                ResultSet rs = p.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch (SQLException ee ) {
                System.out.println(ee.getMessage() + " andra catchen");
            }
        } return null;
    }
    public Integer insertDimMarketGPT(Stock stock) {
        String sql = "INSERT INTO dim_market(name) VALUES(?)";
        try {
            PreparedStatement prepared = conn.prepareStatement(sql);
            prepared.setString(1, stock.getExchange());
            prepared.executeUpdate();

            String sql1 = "SELECT id FROM dim_market WHERE name=?";
            PreparedStatement prepared1 = conn.prepareStatement(sql1);
            prepared1.setString(1, stock.getExchange());
            ResultSet rs = prepared1.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() + " finns antagligen redan");
            String sql1 = "SELECT id FROM dim_market WHERE name=?";
            try {
                PreparedStatement prepared = conn.prepareStatement(sql1);
                prepared.setString(1, stock.getExchange());
                ResultSet rs = prepared.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch (SQLException ee) {
                System.out.println(ee.getMessage() + " i den andra try");
            }
        }
        return null;
    }

    public Integer insertDimMarket(Stock stock) {
        String sql = "INSERT INTO dim_market(name) VALUES(?)";
        try {
            PreparedStatement prepared = conn.prepareStatement(sql);
            prepared.setString(1,stock.getExchange());
            prepared.executeUpdate();
            ResultSet rs = prepared.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() + " finns antagligen redan");
            String sql1 = "SELECT id FROM dim_market WHERE name=?";
            try {
                PreparedStatement prepared = conn.prepareStatement(sql1);
                prepared.setString(1,stock.getExchange());
                ResultSet rs = prepared.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch (SQLException ee) {
                System.out.println(ee.getMessage() + " i den andra try");
            }
        }
        return null;
    }
    public Integer insertCountry(Stock stock) {
        String sql = "SELECT id FROM dim_country WHERE name=?";
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, stock.getCountry());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() + " Nu är den inne i första catch");
            try {
                String sql1 = "INSERT INTO dim_country(name) VALUES(?)";
                PreparedStatement p = conn.prepareStatement(sql1);
                p.setString(1,stock.getExchange());
                p.executeUpdate();
                String s = "SELECT count(*) id FROM dim_country;";
                p = conn.prepareStatement(s);
                ResultSet rs = p.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch (SQLException ee ) {
                System.out.println(ee.getMessage() + " andra catchen");
            }
        } return null;
    }
    public Integer insertSector(Stock stock) {
        String sql = "SELECT id FROM dim_sector WHERE name=?";
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, stock.getSector());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }  p.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage() + " Nu är den inne i första catch");
            try {
                String sql1 = "INSERT INTO dim_sector(name) VALUES(?)";
                PreparedStatement p = conn.prepareStatement(sql1);
                p.setString(1,stock.getSector());
                p.executeUpdate();
                String s = "SELECT count(*) id FROM dim_sector;";
                p = conn.prepareStatement(s);
                ResultSet rs = p.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                } p.close();
            } catch (SQLException ee ) {
                System.out.println(ee.getMessage() + " andra catchen");
            }
        } return null;
    }
    public Integer insertIndustry(Stock stock) {
        String sql = "SELECT id FROM dim_industry WHERE name=?";
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, stock.getIndustry());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } p.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage() + " Nu är den inne i första catch");
            try {
                String sql1 = "INSERT INTO dim_industry(name) VALUES(?)";
                PreparedStatement p = conn.prepareStatement(sql1);
                p.setString(1,stock.getIndustry());
                p.executeUpdate();
                String s = "SELECT count(*) id FROM dim_industry;";
                p = conn.prepareStatement(s);
                ResultSet rs = p.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                } p.close();
            } catch (SQLException ee ) {
                System.out.println(ee.getMessage() + " andra catchen");
            }
        } return null;
    }
    public void insertDimStock(Stock stock) {
        Integer market = insertMarket(stock);
        Integer country = insertCountry(stock);
        Integer sector = insertSector(stock);
        Integer industry = insertIndustry(stock);
        String sql = "INSERT INTO dim_stock(symbol,name,description,"
                + "market_id,country_id,sector_id,industry_id) "
                + "VALUES(?,?,?,?,?,?,?)";
        try {
            PreparedStatement prepared = conn.prepareStatement(sql);
            prepared.setString(1, stock.getSymbol());
            prepared.setString(2, stock.getName());
            prepared.setString(3, stock.getDescription());
            prepared.setInt(4, market);
            prepared.setInt(5, country);
            prepared.setInt(6, sector);
            prepared.setInt(7, industry);
            prepared.executeUpdate();
            prepared.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }






}
