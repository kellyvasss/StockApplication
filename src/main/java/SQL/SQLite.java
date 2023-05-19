package SQL;

import org.apache.shiro.session.mgt.DelegatingSession;
import stock.Market;
import stock.Portfolio;
import stock.Stock;
import user.User;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
        create();
    }

    private void tryStatement(String sql) { // används vid alla createTable statements
        try {
            Statement statement = conn.createStatement();
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage() + e.getErrorCode() + e.getSQLState());

        }
    }

    // sköter skapandet av tabeller
    void create() {
        createTableUser();
        createTableCountry();
        createTableDimCurrency();
        createTableDimIndustry();
        createTableDimMarket();
        createTableDimSector();
        createTableDimStock();
        createTableFactStockPrice();
        createTableFactTransactionIn();
        createTableFactTransactionOut();
    }

    void createTableUser() {
        tryStatement(Statements.DimUser.create);
    }

    void createTableDimStock() {
        tryStatement(Statements.DimStock.create);
    }

    void createTableFactTransactionIn() {
        tryStatement(Statements.FactTransaction.create);
    }

    void createTableFactTransactionOut() {
        tryStatement(Statements.FactTransaction.createSell);
    }

    void createTableDimCurrency() {
        tryStatement(Statements.DimCurrency.create);
    }

    void createTableFactStockPrice() {
        tryStatement(Statements.FactStock.create);
    }

    void createTableDimSector() {
        tryStatement(Statements.DimSector.create);
    }

    void createTableCountry() {
        tryStatement(Statements.DimCountry.create);
    }

    void createTableDimIndustry() {
        tryStatement(Statements.DimIndustry.create);
    }

    void createTableDimMarket() {
        tryStatement(Statements.DimMarket.create);
    }

    // uppdaterar rad i köp när sälj
    Boolean updateBuySub(Integer quantity, Double price, User user, String symbol) {
        Boolean succes = true;
        try {
            PreparedStatement p = conn.prepareStatement(Statements.FactTransaction.updateBuySub);
            p.setInt(1, quantity);
            p.setDouble(2, price);
            p.setInt(3, quantity);
            p.setInt(4, security(user));
            p.setInt(5, stock(symbol));
            p.executeUpdate();
        } catch (SQLException e) {
            System.out.println("fem med att uppdatera vid sälj " + e.getMessage());
            succes = false;
        }
        return succes;
    }

    // uppdatera in tbl sätt nytt antal, pris -> läs i Statementklass för att förstå parametrarna
    Boolean updateBuy(Integer quantity, Double price, User user, String symbol) {
        Boolean succes = true;
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.updateBuy);
            prepared.setInt(1, quantity); // <- antal = antal + antal
            prepared.setInt(2, quantity); // <- nytt antal köpta *
            prepared.setDouble(3, price); // <- nytt pris/ aktie
            prepared.setInt(4, quantity); // <- delat med totala antalet, (antal + antal)
            prepared.setDouble(5, price); // <- nytt pris
            prepared.setInt(6, quantity); // <- nytt pris * (antal + antal)
            prepared.setDouble(7, price); // <- nya priset att subtrahera från (price)
            prepared.setDouble(8, price); // <- nya priset att dela med två
            prepared.setDouble(9, price); // <- nya priset att dela med nya-gamla
            prepared.setInt(10, security(user));
            prepared.setInt(11, stock(symbol));
            prepared.executeUpdate();
        } catch (SQLException e) {
            succes = false;
        }
        return succes;
    }

    // lägger till en användare
    public Boolean insertUser(User user) {
        Boolean succes = true;
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.DimUser.insert);
            prepared.setString(1, user.getPerson_id());
            prepared.setString(2, user.getPassword());
            prepared.setString(3, user.getPasSalt());
            prepared.executeUpdate();
        } catch (SQLException e) {
            System.out.println("fel med att lägga till user i dim_user. " + e.getMessage());
            succes = false;
        }
        return succes;
    }

    // lägger till ett köp i in
    public Boolean insertTransaction(User user, Integer quantity, Double price, String symbol) {
        Boolean succes = true;
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.insert);
            prepared.setInt(1, security(user));
            prepared.setInt(2, stock(symbol));
            prepared.setInt(3, quantity);
            prepared.setDouble(4, price);
            prepared.setDouble(5, price * quantity);
            prepared.executeUpdate();
        } catch (SQLException e) {
            System.out.println("fel med att insert into fact_transaction_in " + e.getMessage());
            succes = false;
        }
        return succes;
    }

    // lägger till ett sälj i out
    public Boolean insertTransactionOut(User user, Integer quantity, Double price, String symbol) {
        Boolean succes = true;
        Integer buy_id = getBuyID(user, symbol);
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.insertSell);
            prepared.setInt(1, security(user));
            prepared.setInt(2, stock(symbol));
            prepared.setInt(3, buy_id);
            prepared.setInt(4, quantity);
            prepared.setDouble(5, price);
            prepared.executeUpdate();
        } catch (SQLException e) {
            System.out.println("fel med att insert into fact_transaction_out " + e.getMessage());
            succes = false;
        }
        return succes;
    }

    // funkar
    void insertDimStock(Stock stock) {
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
        }
    }

    // funkar
    void insertFactStock(Stock s) {
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
            System.out.println("fel med insert Fact Stock");
        }
    }

    // lägger till market i dim_market
    void insertMarket(Market market) {
        String insertMarket = "INSERT INTO dim_market (name, open, close, note) VALUES(?,?,?,?)";
        try {
            PreparedStatement p = conn.prepareStatement(insertMarket);
            p.setString(1, market.getName());
            p.setString(2, market.getOpen());
            p.setString(3, market.getClose());
            p.setString(4, market.getNote());
            p.executeUpdate();
        } catch (SQLException e) {
            System.out.println("fel med insert Market " + e.getMessage());
        }
    }

    // funkar
    Integer insertMarket(Stock stock) {
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
    Integer insertCountry(Stock stock) {
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
    Integer insertSector(Stock stock) {
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
    Integer insertIndustry(Stock stock) {
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

    void insertCurrency(Stock stock) {
        try {
            PreparedStatement p = conn.prepareStatement(Statements.DimCurrency.insert);
            p.setString(1, stock.getCurrency());
            p.executeUpdate();
        } catch (SQLException e) {
        }
    }

    // returnerar alla holdings
    public ArrayList<String> getGetUserStatusHoldings(User user) {
        ArrayList<String> results = new ArrayList<>();
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.getUserStatusHoldings);
            prepared.setInt(1, security(user));
            ResultSet rs = prepared.executeQuery();
            while (rs.next()) {
                String result = "Name of stock: " + rs.getString("n")
                        + "\nSymbol: " + rs.getString("s")
                        + "\nQuantity: " + rs.getString("q")
                        + "\nBuy Price: " + rs.getString("p")
                        + "\nGrowth: " + rs.getString("g") + "%";

                results.add(result);
            }
        } catch (SQLException e) {
            System.out.println("fel med att hämta holdings " + e.getMessage());
        }
        return results;
    }

    // returnerar user_id för aktuell user
    private Integer security(User user) {
        Integer sec = null;
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.DimUser.selectID);
            prepared.setInt(1, Integer.valueOf(user.getPerson_id()));
            ResultSet rs = prepared.executeQuery();
            sec = rs.getInt("id");
        } catch (SQLException e) {
            System.out.println("fel med security metoden. " + e.getMessage());
            return sec;
        }
        return sec;
    }
    public User getUser(String user_id) {
        try {
            PreparedStatement prepared = conn.prepareStatement("SELECT password, pas_salt, cash FROM dim_user WHERE person_id = ?");
            prepared.setString(1, user_id);
            ResultSet rs = prepared.executeQuery();
            if (rs.next()) {
                User user = new User(user_id);
                user.setPassword(rs.getString("password"));
                user.setPasSalt(rs.getString("pas_salt"));
                user.setCash(rs.getString("cash"));
                return user;
            }
        } catch (SQLException e) {
            System.out.println("ingen person hittades " + e.getMessage());
        } throw new RuntimeException();
    }


    // få ett id på vilket köp det är från in tbl
    public Integer getBuyID(User user, String symbol) {
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.getBuyID);
            prepared.setInt(1, security(user));
            prepared.setInt(2, stock(symbol));
            ResultSet rs = prepared.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
        }
        return null;
    }

    public Integer stock(Stock stock) {
        try {
            PreparedStatement p = conn.prepareStatement(Statements.DimStock.selectID);
            p.setString(1, stock.getSymbol());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                insertDimStock(stock);
                PreparedStatement ps = conn.prepareStatement(Statements.DimStock.count);
                ResultSet r = ps.executeQuery();
                if (r.next()) {
                    return r.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // funkar
    Integer currency(Stock stock) {
        try {
            PreparedStatement p = conn.prepareStatement(Statements.DimCurrency.selectID);
            p.setString(1, stock.getCurrency());
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                insertCurrency(stock);
                PreparedStatement ps = conn.prepareStatement(Statements.DimCurrency.count);
                ResultSet r = ps.executeQuery();
                if (r.next()) {
                    return r.getInt("id");
                }
            }
        } catch (SQLException e) {
        }
        return null;
    }

    public Integer stock(String symbol) {
        try {
            PreparedStatement p = conn.prepareStatement(Statements.DimStock.selectID);
            p.setString(1, symbol);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // raderar rad från in
    void sellAll(User user, String symbol) {
        try {
            PreparedStatement p = conn.prepareStatement(Statements.FactTransaction.sellAllStocks);
            p.setInt(1, security(user));
            p.setInt(2, stock(symbol));
            p.executeUpdate();
        } catch (SQLException e) {
        }
    }

    // funkar returnerar true om antalet att vilja sälja är mindre eller lika med antal som finns
    Boolean isAllowedSell(User user, Integer quantity, String symbol, Double price) {
        Boolean succes = true;
        try {
            PreparedStatement p = conn.prepareStatement(Statements.FactTransaction.isAllowedSell);
            p.setInt(1, security(user));
            p.setInt(2, stock(symbol));
            ResultSet rs = p.executeQuery();
            if (rs.next()) { // om det finns quantity i tabellen
                Integer q = rs.getInt("q");
                if (q == quantity || q == 0) { // om antalet önskat sälj är lika med antalet som finns eller om antalet är 0
                    sell(user, quantity, symbol, price); // låt sell ta hand om det ska uppdateras/läggas till
                    sellAll(user, symbol); // sälj och radera raden
                } else if (q > quantity) { // om antalet som finns är mer än önskat sälj
                    updateBuySub(quantity, price, user, symbol); // uppdatera in tbl till nytt antal och pris
                    sell(user, quantity, symbol, price); // lägg in ett sälj i out tbl
                }
            }
            return rs.getInt("q") >= quantity; // om antalet som finns är mer eller lika med än önskat sälj = true

        } catch (SQLException e) {
            succes = false;
        }
        return succes;
    }

    // lägger in en ny rad i out tbl om det finns ett köp
    void sell(User user, Integer quantity, String symbol, Double price) {
        try {
            PreparedStatement pp = conn.prepareStatement("SELECT id FROM fact_transaction_out WHERE buy_id =?");
            pp.setInt(1, getBuyID(user, symbol));
            ResultSet r = pp.executeQuery();
            if (r.next()) { // om det finns ett id som matchar
                insertTransactionOut(user, quantity, price, symbol); // uppdatera out
                updateBuySub(quantity, price, user, symbol); // uppdatera in
            }
        } catch (SQLException e) {
        }
    }

}





