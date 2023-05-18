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




        SQLite sqLite = new SQLite("i");
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
        //sqLite.insertUser(user1);
        /*ArrayList<Market> allMarkets = alphaVantage.getMarkets();
        for (Market m : allMarkets) {
            sqLite.insertMarket(m);
        }*/

        //sqLite.insertDimStock(stock1);

        ArrayList<Stock> stocks = alphaVantage.timeSeriesDailyAdjusted("BRA.FRK");
        Stock AAPL = alphaVantage.companyOverview("BRA.FRK");

        //System.out.println(alphaVantage.searchEndpoint("bra"));
        //System.out.println(stocks.get(0).getSymbol());
        sqLite.insertDimStock(AAPL);
       for (Stock s: stocks) {
           s.setCurrency(AAPL.getCurrency());
           s.setSymbol(AAPL.getSymbol());
           sqLite.insertFactStock(s);
       }



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
