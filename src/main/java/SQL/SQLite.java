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
                + "date DATE NOT NULL,\n"
                + "price DECIMAL,\n"
                + "currency_id INTEGER NOT NULL,\n"
                + "FOREIGN KEY (stock_id) REFERENCES dim_stock(id),\n"
                + "FOREIGN KEY (currency_id) REFERENCES dim_currency(id)"
                + "UNIQUE(stock_id, date));";
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
        String selectSql = "SELECT id FROM dim_market WHERE name=?";
        String insertSql = "INSERT INTO dim_market(name) VALUES(?)";
        String selectCountSql = "SELECT COUNT(*) id FROM dim_market;";
        try {
            PreparedStatement selectStatement = conn.prepareStatement(selectSql);
            selectStatement.setString(1, stock.getExchange());
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                PreparedStatement insertStatement = conn.prepareStatement(insertSql);
                insertStatement.setString(1, stock.getExchange());
                insertStatement.executeUpdate();
                PreparedStatement selectCountStatement = conn.prepareStatement(selectCountSql);
                ResultSet countRs = selectCountStatement.executeQuery();
                if (countRs.next()) {
                    return countRs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Integer insertCountry(Stock stock) {
        String selectSql = "SELECT id FROM dim_country WHERE name=?";
        String insertSql = "INSERT INTO dim_country(name) VALUES(?)";
        String selectCountSql = "SELECT COUNT(*) id FROM dim_country;";
        try {
            PreparedStatement selectStatement = conn.prepareStatement(selectSql);
            selectStatement.setString(1, stock.getCountry());
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                PreparedStatement insertStatement = conn.prepareStatement(insertSql);
                insertStatement.setString(1, stock.getCountry());
                insertStatement.executeUpdate();
                PreparedStatement selectCountStatement = conn.prepareStatement(selectCountSql);
                ResultSet countRs = selectCountStatement.executeQuery();
                if (countRs.next()) {
                    return countRs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Integer insertSector(Stock stock) {
        String selectSql = "SELECT id FROM dim_sector WHERE name=?";
        String insertSql = "INSERT INTO dim_sector(name) VALUES(?)";
        String selectCountSql = "SELECT COUNT(*) id FROM dim_sector;";
        try {
            PreparedStatement selectStatement = conn.prepareStatement(selectSql);
            selectStatement.setString(1, stock.getSector());
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                PreparedStatement insertStatement = conn.prepareStatement(insertSql);
                insertStatement.setString(1, stock.getSector());
                insertStatement.executeUpdate();
                PreparedStatement selectCountStatement = conn.prepareStatement(selectCountSql);
                ResultSet countRs = selectCountStatement.executeQuery();
                if (countRs.next()) {
                    return countRs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer insertIndustry(Stock stock) {
        String selectSql = "SELECT id FROM dim_industry WHERE name=?";
        String insertSql = "INSERT INTO dim_industry(name) VALUES(?)";
        String selectCountSql = "SELECT COUNT(*) id FROM dim_industry;";
        try {
            PreparedStatement selectStatement = conn.prepareStatement(selectSql);
            selectStatement.setString(1, stock.getIndustry());
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                PreparedStatement insertStatement = conn.prepareStatement(insertSql);
                insertStatement.setString(1, stock.getIndustry());
                insertStatement.executeUpdate();
                PreparedStatement selectCountStatement = conn.prepareStatement(selectCountSql);
                ResultSet countRs = selectCountStatement.executeQuery();
                if (countRs.next()) {
                    return countRs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Integer stock(Stock stock) {
        String sql = "SELECT id FROM dim_stock WHERE symbol=?";
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1,stock.getSymbol());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                insertDimStock(stock);
                String sqlCount = "SELECT COUNT(*) id FROM dim_currency;";
                PreparedStatement ps = conn.prepareStatement(sqlCount);
                ResultSet r = ps.executeQuery();
                if (r.next()) {
                    return r.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } return null;
    }
    private void insertCurrency(Stock stock) {
        String sql = "INSERT INTO dim_currency(currency) VALUES(?)";
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, stock.getCurrency());
            p.executeUpdate();
            p.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public Integer currency(Stock stock) {
        String sql = "SELECT id FROM dim_currency WHERE currency=?";
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, stock.getCurrency());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                insertCurrency(stock);
                String sqlCount = "SELECT COUNT(*) id FROM dim_currency;";
                PreparedStatement ps = conn.prepareStatement(sqlCount);
                ResultSet r = ps.executeQuery();
                if (r.next()) {
                    return r.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
    public void insertFactStock(Stock s) {
        Integer stock = stock(s);
        Integer currency = currency(s);
        String sql = "INSERT INTO fact_StockPrice(stock_id, date, price, currency_id) "
                + "VALUES(?,?,?,?)";
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            p.setInt(1, stock);
            p.setString(2, s.getDate());
            p.setDouble(3, s.getPrice());
            p.setInt(4, currency);
            p.executeUpdate();
            p.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
