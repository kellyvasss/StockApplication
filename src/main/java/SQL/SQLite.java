package SQL;

import stock.Portfolio;
import stock.Stock;
import user.User;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        tryStatement(Statements.DimStock.create);
    }
    public void createTableFactTransactionIn() {
        tryStatement(Statements.FactTransaction.create);
    }

    public void createTableDimCurrency() {
        tryStatement(Statements.DimCurrency.create);
    }
    public void createTableFactStockPrice() {
        tryStatement(Statements.FactStock.create);
    }
    public void createTablePortfolio() {
        tryStatement(Statements.DimPortfolio.create);
    }
    public void createTableDimSector() {
        tryStatement(Statements.DimSector.create);
    }
    public void createTableCountry() {
        tryStatement(Statements.DimCountry.create);
    }
    public void createTableDimIndustry() {
        tryStatement(Statements.DimIndustry.create);
    }
    public void createTableDimMarket() {
        tryStatement(Statements.DimMarket.create);
    }
    public void createTableUser() {
        tryStatement(Statements.DimUser.create);
    }
    private Integer security(User user) {
        Integer sec = null;
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.DimUser.selectID);
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
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.DimUser.insert);
            prepared.setString(1, user.getPerson_id());
            prepared.setString(2,user.getPassword());
            prepared.setString(3, user.getPasSalt());
            prepared.executeUpdate();
        } catch (SQLException e) {
            System.out.println("fel med att lägga till user i dim_user. " + e.getMessage());
            succes = false;
        } return succes;
    }
   public Boolean insertTransaction(User user, Integer quantity, Double price, String symbol, String c) {
        Boolean succes = true;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.insert);
            prepared.setInt(1,security(user));
            prepared.setInt(2, stock(symbol));
            prepared.setString(3, date);
            prepared.setInt(4, quantity);
            prepared.setString(5, c);
            prepared.setDouble(6, price);
            prepared.executeUpdate();
        } catch (SQLException e) {
            System.out.println("fel med att insert into fact_transaction " + e.getMessage());
            succes = false;
        } return succes;
    }
    public Boolean insertPortfolio(Portfolio portfolio, User user) {
        Boolean succes = true;
        LocalDate today = LocalDate.now();
        Date date = Date.valueOf(today);
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.DimPortfolio.insert);
            prepared.setString(1, portfolio.getName());
            prepared.setString(2, String.valueOf(date));
            prepared.setInt(3, security(user));
            prepared.executeUpdate();
        } catch (SQLException e) {
            System.out.println("fel med antagligen user implementationen... " + e.getMessage());
            succes = false;
        } return succes;
    }
    // funkar
    public Integer insertMarket(Stock stock) {
        try {
            PreparedStatement selectStatement = conn.prepareStatement(Statements.DimMarket.selectID);
            selectStatement.setString(1, stock.getExchange());
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                PreparedStatement insertStatement = conn.prepareStatement(Statements.DimMarket.insert);
                insertStatement.setString(1, stock.getExchange());
                insertStatement.executeUpdate();
                PreparedStatement selectCountStatement = conn.prepareStatement(Statements.DimMarket.count);
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
    // funkar
    public Integer insertCountry(Stock stock) {
        try {
            PreparedStatement selectStatement = conn.prepareStatement(Statements.DimCountry.selectID);
            selectStatement.setString(1, stock.getCountry());
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                PreparedStatement insertStatement = conn.prepareStatement(Statements.DimCountry.insert);
                insertStatement.setString(1, stock.getCountry());
                insertStatement.executeUpdate();
                PreparedStatement selectCountStatement = conn.prepareStatement(Statements.DimCountry.count);
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
    // funkar
    public Integer insertSector(Stock stock) {
        try {
            PreparedStatement selectStatement = conn.prepareStatement(Statements.DimSector.selectID);
            selectStatement.setString(1, stock.getSector());
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                PreparedStatement insertStatement = conn.prepareStatement(Statements.DimSector.insert);
                insertStatement.setString(1, stock.getSector());
                insertStatement.executeUpdate();
                PreparedStatement selectCountStatement = conn.prepareStatement(Statements.DimSector.count);
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

    // funkar
    public Integer insertIndustry(Stock stock) {
        try {
            PreparedStatement selectStatement = conn.prepareStatement(Statements.DimIndustry.selectID);
            selectStatement.setString(1, stock.getIndustry());
            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                PreparedStatement insertStatement = conn.prepareStatement(Statements.DimIndustry.insert);
                insertStatement.setString(1, stock.getIndustry());
                insertStatement.executeUpdate();
                PreparedStatement selectCountStatement = conn.prepareStatement(Statements.DimIndustry.count);
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
    // funkar
    public Integer stock(Stock stock) {
        String sql = Statements.DimStock.selectID;
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1,stock.getSymbol());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                insertDimStock(stock);
                String sqlCount = Statements.DimCurrency.count;
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
    public Integer stock(String symbol) {

        try {
            PreparedStatement p = conn.prepareStatement(Statements.DimStock.selectID);
            p.setString(1,symbol);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } return null;
    }
    // funkar
    private void insertCurrency(Stock stock) {
        String sql = Statements.DimCurrency.insert;
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, stock.getCurrency());
            p.executeUpdate();
            p.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // funkar
    public Integer currency(Stock stock) {
        String sql = Statements.DimCurrency.selectID;
        try {
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1, stock.getCurrency());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                insertCurrency(stock);
                String sqlCount = Statements.DimCurrency.count;
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

    // funkar
    public void insertDimStock(Stock stock) {
        Integer market = insertMarket(stock);
        Integer country = insertCountry(stock);
        Integer sector = insertSector(stock);
        Integer industry = insertIndustry(stock);
        String sql = Statements.DimStock.insertDimStock;
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
    // funkar
    public void insertFactStock(Stock s) {
        Integer stock = stock(s);
        Integer currency = currency(s);
        String sql = Statements.FactStock.insertFactStock;
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
