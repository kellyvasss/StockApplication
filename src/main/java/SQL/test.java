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


        System.out.println(alphaVantage.companyOverview("BA").getName());

    }
}
