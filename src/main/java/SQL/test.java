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

public class test {
    static AlphaVantage alphaVantage;
    static KeyReader keyReader;
    public static void main(String[] args) {

        keyReader = new KeyReader("Alpha");
        alphaVantage = new AlphaVantage(keyReader.getAPIKey());


        System.out.println(alphaVantage.companyOverview("BA").getName());

        User u = new User("990622010");
        u.setPasSalt(Hasher.generateSalt().toString());
        u.setPassword(Hasher.hash("hej", ByteSource.Util.bytes(u.getPasSalt())));

        System.out.println(Hasher.verify("hej", u.getPassword(), u.getPasSalt()));

        System.out.println(u.getPassword());

        System.out.println(u.getPassword());
        System.out.println(Hasher.hash("hej", ByteSource.Util.bytes(u.getPasSalt())));
        SQLite sqLite = new SQLite("m");
       // User u1 = sqLite.getUser("he");
        //System.out.println(u1.getCash() + u1.getPassword() + u1.getPasSalt());
        try  {
            User uu = sqLite.getUser("hej");
            System.out.println("jej");
            System.out.println(uu.getCash());
        }
         catch (RuntimeException e) {
            System.out.println("fångade" + e.getMessage());
        }

}
}
