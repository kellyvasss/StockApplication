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
                .exchange("NYSE")
                .industry("Tech")
                .price(79.5)
                .symbol("123")
                .build();

        portfolio.setName("Hej");
        portfolio.setStock(stock);
        portfolio.setStock(stock1);

        SQLite sqLite = new SQLite("Test");
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



    }
}
