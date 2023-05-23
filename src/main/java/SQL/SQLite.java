package SQL;

import stock.Market;
import stock.Stock;
import user.User;
import java.sql.*;
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
            System.out.println(e.getMessage());
            succes = false;
        }
        return succes;
    }

    // Används för att kontrollera om det finns ett existerande köp
    public Boolean isExisting(User user, String symbol) {
        Boolean existing = false;
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.isExisting);
            prepared.setInt(1, security(user));
            prepared.setInt(2, stock(symbol));
            ResultSet rs = prepared.executeQuery();
            if(rs.next()) {
                existing = true;
            }
        }catch (SQLException e) {

        }return existing;
    }
    public Boolean updateBuy(Integer quantity, Double price, User user, String symbol) {
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.updateBuy);
            prepared.setInt(1, quantity); // <- antal = antal + antal
            prepared.setInt(2, quantity); // <- pris * antal
            prepared.setDouble(3, price);
            prepared.setInt(4, quantity); // <- delat med totala antalet, (antal + antal)
            prepared.setDouble(5, price);
            prepared.setInt(6, quantity); // <- nytt pris * total antal = approxValue
            prepared.setInt(7, security(user));
            prepared.setInt(8, stock(symbol));
            prepared.executeUpdate();
            updateCashBuy(user, quantity, price);
        } catch (SQLException e) {
            return false;
        }return true;
    }
    private void updateCashBuy(User user, Integer quantity, Double price) {
        try {
            PreparedStatement prep = conn.prepareStatement(Statements.FactTransaction.updateCash);
            prep.setDouble(1, price*quantity);
            prep.setInt(2, security(user));
            prep.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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
            System.out.println(e.getMessage());
            succes = false;
        }
        return succes;
    }

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
            updateCashBuy(user,quantity,price);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            succes = false;
        }
        return succes;
    }
    // lägger till ett sälj i out + uppdaterar dim_user cash
    public Boolean insertTransactionOut(User user, Integer quantity, Double price, String symbol) {
        Boolean succes = true;
        Object[] values = getBuyID(user,symbol);
        Integer buy_id = (Integer) values[0]; // <- Buy_ID från köpet
        Double buyPrice = (Double) values[1]; // <- GetValue från köpet
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.insertSell);
            prepared.setInt(1, security(user));
            prepared.setInt(2, stock(symbol));
            prepared.setInt(3, buy_id);
            prepared.setInt(4, quantity);
            prepared.setDouble(5, price);
            prepared.setDouble(6,buyPrice);
            prepared.executeUpdate();
            updateCashSell(user, quantity, price);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            succes = false;
        }
        return succes;
    }

    // funkar

    public void insertDimStock(Stock stock) {
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.DimStock.insertDimStock);
            prepared.setString(1, stock.getSymbol());
            prepared.setString(2, stock.getName());
            prepared.setString(3, stock.getDescription());
            prepared.setInt(4, insertMarket(stock));
            prepared.setInt(5, insertCountry(stock));
            prepared.setInt(6, insertSector(stock));
            prepared.setInt(7, currency(stock));
            prepared.executeUpdate();
            prepared.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // lägger till market i dim_market
    public void insertMarket(Market market) {
        String insertMarket = "INSERT INTO dim_market (name, open, close, note) VALUES(?,?,?,?)";
        try {
            PreparedStatement p = conn.prepareStatement(insertMarket);
            p.setString(1, market.getName());
            p.setString(2, market.getOpen());
            p.setString(3, market.getClose());
            p.setString(4, market.getNote());
            p.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // funkar
    private Integer insertMarket(Stock stock) {
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

    private void insertCurrency(Stock stock) {
        try {
            PreparedStatement p = conn.prepareStatement(Statements.DimCurrency.insert);
            p.setString(1, stock.getCurrency());
            p.executeUpdate();
        } catch (SQLException e) {
        }
    }
    // returnerar historia på alla sales för en user
    public ArrayList<String> getHistory(User user) {
        ArrayList<String> results = new ArrayList<>();
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.getAllSales);
            prepared.setInt(1, security(user));
            ResultSet rs = prepared.executeQuery();
            while (rs.next()) {
                String result = "Name of stock: " + rs.getString("n") //
                        + "\nSymbol: " + rs.getString("s") //
                        + "\nDate of sale: " + rs.getString("d")
                        + "\nProcent up/down: " + rs.getString("g") + " %";
                results.add(result);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } return results;
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
                        + "\nMarket: " + rs.getString("m")
                        + "\nGrowth: " + rs.getString("g") + " %";

                results.add(result);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }
    public Double[] getBalanceAndGrowth(User user) {
        Double[] balanceAndGrowth = new Double[2];
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.getGrowthAndBalance);
            prepared.setInt(1, security(user));
            ResultSet rs = prepared.executeQuery();
            if(rs.next()) {
                balanceAndGrowth[0] = rs.getDouble("p"); // <- procentuell förändring
                balanceAndGrowth[1] = rs.getDouble("b"); // <- balans
            } return balanceAndGrowth;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } return null;
    }

    // returnerar user_id för aktuell user
    private Integer security(User user) {
        Integer sec = null;
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.DimUser.selectID);
            prepared.setString(1, (user.getPerson_id()));
            ResultSet rs = prepared.executeQuery();
            if (rs.next()) {
                sec = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        } throw new RuntimeException();
    }

    // få ett id på vilket köp det är från in tbl och getValue
    private Object[] getBuyID(User user, String symbol) {
        Object[] values = new Object[2];
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.getBuyID);
            prepared.setInt(1, security(user));
            prepared.setInt(2, stock(symbol));
            ResultSet rs = prepared.executeQuery();
            if (rs.next()) {
                values[0] = rs.getInt("id");
                values[1] = rs.getDouble("price");
                return values;
            }
            return values;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("fel i stock symbol" + e.getMessage());
        }
        return null;
    }

    // raderar rad från in
    private void sellAll(User user, String symbol) {
        try {
            PreparedStatement p = conn.prepareStatement(Statements.FactTransaction.sellAllStocks);
            p.setInt(1, security(user));
            p.setInt(2, stock(symbol));
            p.executeUpdate();
        } catch (SQLException e) {
        }
    }
    private void updateCashSell(User user, Integer quantity, Double price) {
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.updateCashSell);
            prepared.setDouble(1, price*quantity); // addera (säljpris * antal) till cash
            prepared.setInt(2, security(user));
            prepared.executeUpdate();
        } catch (SQLException e) {
            System.out.println("fel med updateCashSell " + e.getMessage());
        }
    }
    public Double getCashToUse(User user) {
        try {
            PreparedStatement prepared = conn.prepareStatement(Statements.FactTransaction.getCashToUse);
            prepared.setInt(1, security(user));
            ResultSet rs = prepared.executeQuery();
            if(rs.next()) {
                return rs.getDouble("cash");
            }
        } catch (SQLException e) {
        }return null;
    }
    // funkar returnerar true om antalet att vilja sälja är mindre eller lika med antal som finns
    public Boolean isAllowedSell(User user, Integer quantity, String symbol, Double price) {
        try {
            PreparedStatement p = conn.prepareStatement(Statements.FactTransaction.isAllowedSell);
            p.setInt(1, security(user));
            p.setInt(2, stock(symbol));
            ResultSet rs = p.executeQuery();
            if (rs.next()) { // om det finns quantity i tabellen
                Integer q = rs.getInt("q");
                if (q < quantity || q == null) {
                    rs.close();
                    throw new RuntimeException(); // om användaren försöker att sälja mer aktier än dens innehav
                }
                if (q == quantity) { // om antalet önskat sälj är lika med antalet som finns
                    insertTransactionOut(user, quantity, price, symbol); // lägg till en rad i sälj tabellen
                    sellAll(user, symbol); // sälj och radera raden
                    return true;
                } else if (q > quantity) { // om antalet som finns är mer än önskat sälj
                    updateBuySub(quantity, price, user, symbol); // uppdatera in tbl till nytt antal och pris
                    insertTransactionOut(user, quantity, price, symbol); // lägg till rad i sälj tabell
                    return true;
                }
                updateCashSell(user, quantity, price);
                rs.close();
                return q >= quantity; // om antalet som finns är mer eller lika med än önskat sälj = true
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException();
        } return false;
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
    private void create() {
        createTableUser();
        createTableCountry();
        createTableDimCurrency();
        createTableDimMarket();
        createTableDimSector();
        createTableDimStock();
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

    void createTableDimSector() {
        tryStatement(Statements.DimSector.create);
    }

    void createTableCountry() {
        tryStatement(Statements.DimCountry.create);
    }

    void createTableDimMarket() {
        tryStatement(Statements.DimMarket.create);
    }


}





