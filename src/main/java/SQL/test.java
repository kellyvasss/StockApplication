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

        User u = new User("9906220183", "hej");
        u.setPasSalt(Hasher.generateSalt().toString());
        u.setPassword(Hasher.hash("hej", ByteSource.Util.bytes(u.getPasSalt())));

        System.out.println(Hasher.verify("hej", u.getPassword(), u.getPasSalt()));

        System.out.println(u.getPassword());

        System.out.println(u.getPassword());
        System.out.println(Hasher.hash("hej", ByteSource.Util.bytes(u.getPasSalt())));


    }
}
