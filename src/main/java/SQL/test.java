package SQL;
import api.AlphaVantage;
import api.KeyReader;
import stock.Market;
import stock.Portfolio;
import stock.Stock;
import stock.StockBuilder;
import user.User;
import user.hash;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class test {
    static AlphaVantage alphaVantage;
    static KeyReader keyReader;
    public static void main(String[] args) {

        keyReader = new KeyReader("Alpha");
        alphaVantage = new AlphaVantage(keyReader.getAPIKey());
        //Stock stock = alphaVantage.companyOverview("IBM");
        User user = new User("9906220182", "kelly");
        user.setPasSalt("5");
        User user1 = new User("0000000000", "testperson");
        Stock stock3 = new StockBuilder()
                .currency("USD")
                .exchange("NYSE")
                .industry("Tech")
                .price(50.2)
                .symbol("ABC")
                .build();
        Stock stock1 = new StockBuilder()
                .currency("EUR")
                .name("Testforuser1")
                .description("This is a apple company that sells electronic products")
                .country("USA")
                .sector("Tv and Tele")
                .exchange("NYSE")
                .industry("Tech")
                .price(100.0)
                .symbol("HELLOT")
                .build();




        SQLite sqLite = new SQLite("ii");
        sqLite.createTableUser();
        sqLite.createTableCountry();
        sqLite.createTableDimCurrency();
        sqLite.createTableDimIndustry();
        sqLite.createTableDimMarket();
        sqLite.createTableDimSector();
        sqLite.createTableDimStock();
        sqLite.createTableFactStockPrice();
        sqLite.createTableFactTransactionIn();
        sqLite.createTableFactTransactionOut();
        String pas = hash.hasha(user.getPassword());
        sqLite.insertUser(user1);
        /*ArrayList<Market> allMarkets = alphaVantage.getMarkets();
        for (Market m : allMarkets) {
            sqLite.insertMarket(m);
        }*/

        //sqLite.insertDimStock(stock1);

        ArrayList<Stock> stocks = alphaVantage.timeSeriesDailyAdjusted("ARVN");
        Stock AAPL = alphaVantage.companyOverview("ARVN");

        //System.out.println(alphaVantage.searchEndpoint("bra"));
        //System.out.println(stocks.get(0).getSymbol());
        sqLite.insertDimStock(AAPL);
       for (Stock s: stocks) {
           s.setCurrency(AAPL.getCurrency());
           s.setSymbol(AAPL.getSymbol());
           sqLite.insertFactStock(s);
       }
       sqLite.insertDimStock(stock1);
     //  sqLite.insertTransaction(user1, 10, stocks.get(0).getPrice(), stock1.getSymbol());
      // sqLite.updateBuy(10, 80000.0, user1, stock1.getSymbol());

        User u = new User("9906220101", "hej");
        sqLite.insertUser(u);
        sqLite.insertTransaction(u, 10, stock1.getPrice(), stock1.getSymbol());
        sqLite.updateBuy(5, stock1.getPrice()*0.2, u, stock1.getSymbol());
        sqLite.insertUser(user);
        sqLite.insertTransaction(user, 10, 10.0, stock1.getSymbol());
        sqLite.updateBuy(10, 0.5, user, stock1.getSymbol());
        User user2 = new User("hej", "då");
        sqLite.insertUser(user2);
        sqLite.updateBuy(-100, 100.0, user2, stock1.getSymbol());
        sqLite.insertTransaction(user2, 10, stock1.getPrice(), stock1.getSymbol());
        System.out.println("HEJ");
        System.out.println(stock1.getSymbol());
        System.out.println(sqLite.isAllowedSell(user2, 991, stock1.getSymbol()));




/*
       sqLite.insertTransaction(user1, 10, stock1.getPrice(), stock1.getSymbol());
        ArrayList<String> result = sqLite.getGetUserStatusHoldings(user1);
        sqLite.insertTransactionOut(user1,1,stock1.getPrice(),stock1.getSymbol());
        sqLite.updateBuy(10, stock1.getPrice(), user1, stock1.getSymbol());
        sqLite.updateBuy(-100, stock1.getPrice(), user1, stock1.getSymbol());
        System.out.println(result.get(0));
*/



    }
}
