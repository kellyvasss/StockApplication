package SQL;
import stock.Portfolio;
import stock.Stock;
import stock.StockBuilder;
import user.User;
import user.hash;

import java.sql.Date;
import java.time.LocalDate;

public class test {
    public static void main(String[] args) {

        User user = new User("9906220182", "kelly");
        user.setIdSalt("10");
        user.setPasSalt("5");

        Portfolio portfolio = new Portfolio();
        Stock stock = new StockBuilder()
                .currency("USD")
                .exchange("NYSE")
                .industry("Tech")
                .price(50.2)
                .symbol("ABC")
                .build();
        Stock stock1 = new StockBuilder()
                .currency("USD")
                .name("Apple INC")
                .description("This is a apple company that sells electronic products")
                .country("USA")
                .sector("Tv and Tele")
                .exchange("NYSE")
                .industry("Tech")
                .price(79.5)
                .symbol("123")
                .build();
        System.out.println(stock1.getExchange());

        portfolio.setName("Hej");
        portfolio.setStock(stock);
        portfolio.setStock(stock1);

        SQLite sqLite = new SQLite("Testt");
        sqLite.createTableUser();
        sqLite.createTablePortfolio();
        sqLite.createTableCountry();
        sqLite.createTableDimCurrency();
        sqLite.createTableDimIndustry();
        sqLite.createTableDimMarket();
        sqLite.createTableDimSector();
        sqLite.createTableDimStock();
        sqLite.createTableFactStockPrice();
        sqLite.createTableFactTransaction();
        String pas = hash.hasha(user.getPassword());
        sqLite.insertUser(user);
        sqLite.insertPortfolio(portfolio,user);
        System.out.println(LocalDate.now());
        LocalDate today = LocalDate.now();
        Date date = Date.valueOf(today);
        System.out.println(date);
        sqLite.insertDimStock(stock1);

        Stock stock2 = new StockBuilder()
                .country("Canada")
                .sector("Vegetarian and Vegan")
                .exchange("NASDAQ")
                .industry("Food")
                .description("This is a test company")
                .name("HEje")
                .price(14.5)
                .currency("EUR")
                .symbol("DATE")
                .date("2022-02-01")
                .build();
        sqLite.insertDimStock(stock2);
        sqLite.insertFactStock(stock2);
        sqLite.insertFactStock(stock2);
        Stock stock3 = new StockBuilder()
                .country("Spain")
                .sector("Makeup")
                .exchange("NASDAQ")
                .industry("Cosmetique")
                .description("This is a test company")
                .name("Test AB")
                .price(14.5)
                .currency("EUR")
                .symbol("Loreal")
                .date("2022-02-01")
                .build();
        sqLite.insertFactStock(stock3);
        sqLite.insertFactStock(stock3);
        sqLite.insertDimStock(stock3);



    }
}
