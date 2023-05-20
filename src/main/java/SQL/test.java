package SQL;
import api.AlphaVantage;
import api.KeyReader;
import org.apache.shiro.util.ByteSource;
import stock.Market;
import stock.Portfolio;
import stock.Stock;
import stock.StockBuilder;
import user.Hasher;
import user.User;
import user.hash;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class test {
    static AlphaVantage alphaVantage;
    static KeyReader keyReader;
    public static void main(String[] args) {

        keyReader = new KeyReader("Alpha");
        alphaVantage = new AlphaVantage(keyReader.getAPIKey());


       // System.out.println(alphaVantage.companyOverview("BA").getName());

        User u = new User("9906220101");
        u.setPasSalt(Hasher.generateSalt().toString());
        u.setPassword(Hasher.hash("hej", ByteSource.Util.bytes(u.getPasSalt())));

        System.out.println(Hasher.verify("hej", u.getPassword(), u.getPasSalt()));

        System.out.println(u.getPassword());

        System.out.println(u.getPassword());
        System.out.println(Hasher.hash("hej", ByteSource.Util.bytes(u.getPasSalt())));
        SQLite sqLite = new SQLite("l");
       // User u1 = sqLite.getUser("he");
        //System.out.println(u1.getCash() + u1.getPassword() + u1.getPasSalt());
        //sqLite.insertDimStock(alphaVantage.companyOverview("BA"));
        User user = new User("2121212121");
        user.setPasSalt(Hasher.generateSalt().toString());
        user.setPassword(Hasher.hash("hej", ByteSource.Util.bytes(user.getPasSalt())));
        sqLite.insertUser(user);

        Stock stock = alphaVantage.companyOverview("AAPL");
       // ArrayList<Stock> a = alphaVantage.timeSeriesDailyAdjusted("AAPL");
       // sqLite.insertCurrency(stock);
       // sqLite.insertDimStock(stock);
        //System.out.println(stock.getCurrency());
      //  System.out.println(u.getPerson_id());
        //System.out.println(stock.getCountry());
       // String info = String.valueOf(alphaVantage.quote(stock.getSymbol())[0]);
       // System.out.println(info);
       // System.out.println(stock.toString());
      /*  for (Stock s : a) {
            s.setSymbol(stock.getSymbol());
            s.setCurrency(stock.getCurrency());
            s.setCountry(stock.getCountry());
            sqLite.insertFactStock(s);
        }*/

       // System.out.println(stock.getPrice());
        sqLite.insertTransaction(user, 10, Double.valueOf((Double) alphaVantage.quote(stock.getSymbol())[1]), stock.getSymbol());
        ArrayList<String> holdings = sqLite.getGetUserStatusHoldings(user);
        for (String s: holdings) {
            System.out.println(s);
        }
        System.out.println(sqLite.isAllowedSell(user, 9, "AAPL", 10.0));
        //sqLite.insertTransactionOut(user, 1, 170.0, "AAPL");
        System.out.println(user.getCash());
        String[] b = sqLite.getBalanceAndGrowth(user);
        System.out.println(b[0] + " %");
        System.out.println(b[1]);
        /*
        try  {
            User uu = sqLite.getUser("hej");
            System.out.println("jej");
            System.out.println(uu.getCash());
        }
         catch (RuntimeException e) {
            System.out.println("fångade" + e.getMessage());
        }*/

}
}
