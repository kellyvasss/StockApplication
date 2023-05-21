package SQL;

public final class Statements {
    static final class FactStock {
        static final String insertFactStock = "INSERT INTO fact_StockPrice(stock_id, date, price, currency_id) "
                + "VALUES(?,?,?,?)";
        static final String create = "CREATE TABLE IF NOT EXISTS fact_StockPrice (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "stock_id INTEGER NOT NULL,\n"
                + "date DATE NOT NULL,\n"
                + "price DECIMAL,\n"
                + "currency_id INTEGER NOT NULL,\n"
                + "FOREIGN KEY (stock_id) REFERENCES dim_stock(id),\n"
                + "FOREIGN KEY (currency_id) REFERENCES dim_currency(id)"
                + "UNIQUE(stock_id, date));";
    }

    static final class DimStock {
        static final String insertDimStock = "INSERT INTO dim_stock(symbol,name,description,"
                + "market_id,country_id,sector_id,industry_id) "
                + "VALUES(?,?,?,?,?,?,?)";
        static final String selectID = "SELECT id FROM dim_stock WHERE symbol=?";
        static final String count = "SELECT COUNT(*) id FROM dim_stock;";
        static final String create = "CREATE TABLE IF NOT EXISTS dim_stock (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "symbol VARCHAR(10) UNIQUE,\n"
                + "name VARCHAR(50),\n"
                + "description TEXT,\n"
                + "market_id INTEGER,\n"
                + "country_id INTEGER,\n"
                + "sector_id INTEGER,\n"
                + "industry_id INTEGER,\n"
                + "FOREIGN KEY (market_id) REFERENCES dim_market(id),\n"
                + "FOREIGN KEY (country_id) REFERENCES dim_country(id),\n"
                + "FOREIGN KEY (sector_id) REFERENCES dim_sector(id),\n"
                + "FOREIGN KEY (industry_id) REFERENCES dim_industry(id));";
    }

    static class DimIndustry {
        static final String count = "SELECT COUNT(*) id FROM dim_industry;";
        static final String insert = "INSERT INTO dim_industry(name) VALUES(?)";
        static final String selectID = "SELECT id FROM dim_industry WHERE name=?";
        static final String create = "CREATE TABLE IF NOT EXISTS dim_industry (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(50) UNIQUE);";

    }

    static final class DimCurrency {
        static final String selectID = "SELECT id FROM dim_currency WHERE currency=?";
        static final String count = "SELECT COUNT(*) id FROM dim_currency;";
        static final String insert = "INSERT INTO dim_currency(currency) VALUES(?)";
        static final String create = "CREATE TABLE IF NOT EXISTS dim_currency (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "currency VARCHAR(3) UNIQUE);";

    }

    static final class DimCountry {
        static final String selectID = "SELECT id FROM dim_country WHERE name=?";
        static final String insert = "INSERT INTO dim_country(name) VALUES(?)";
        static final String count = "SELECT COUNT(*) id FROM dim_country;";
        static final String create = "CREATE TABLE IF NOT EXISTS dim_country (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(30) UNIQUE);";

    }

    static final class DimMarket {
        static final String selectID = "SELECT id FROM dim_market WHERE name=?";
        static final String insert = "INSERT INTO dim_market(name) VALUES(?)";
        static final String count = "SELECT COUNT(*) id FROM dim_market;";
        static final String create = "CREATE TABLE IF NOT EXISTS dim_market (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(10) UNIQUE,\n"
                + "open VARCHAR(5),\n"
                + "close VARCHAR(5),\n"
                + "note VARCHAR(75));";

    }

    static final class DimSector {
        static final String selectID = "SELECT id FROM dim_sector WHERE name=?";
        static final String insert = "INSERT INTO dim_sector(name) VALUES(?)";
        static final String count = "SELECT COUNT(*) id FROM dim_sector;";
        static final String create = "CREATE TABLE IF NOT EXISTS dim_sector (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(30) UNIQUE);";

    }

    static final class DimUser {
        // alla users ska få 100.000 att handla för
        static final String insert = "INSERT INTO dim_user(person_id, password, pas_salt, cash) VALUES(?,?,?,100000.0)";
        static final String selectID = "SELECT id FROM dim_user WHERE person_id=?";
        static final String create = "CREATE TABLE IF NOT EXISTS dim_user (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "person_id VARCHAR(10) UNIQUE,\n"
                + "password TEXT,\n"
                + "pas_salt VARCHAR(75),\n"
                + "cash DECIMAL"
                + ");";
    }

    static final class FactTransaction {
        // Balance är vad man har för värde på sina aktier + belopp som ej använts
        // Förändring i procent räknas ut med formel ((nytt värde - ursprungliga värde) / ursprungliga värde) * 100

        static final String getGrowthAndBalance = "SELECT\n" +
                "IFNULL(ROUND((((SUM(fin.approxValue) + d.cash) - 100000.0) / 100000.0) * 100, 2), 0) AS p,\n" + // <- om värdet är null (ingen rad finns) ersätts det med 0
                "IFNULL(ROUND(SUM(fin.approxValue) + d.cash, 2), d.cash) AS b\n" +
                "FROM dim_user d\n" +
                "INNER JOIN fact_transaction_in fin ON fin.user_id = d.id\n" +
                "WHERE d.id = ?";

        static final String getUserStatusHoldings = "SELECT d.name n, d.symbol s, fin.quantity q, "
                + "fin.price p, (((fin.approxValue/fin.quantity) - fin.price) / fin.price) * 100 AS g \n" // ((nytt värde - gammaltvärde) / gammaltvärde) *100
                + "FROM fact_transaction_in fin \n"
                + "JOIN dim_stock d ON fin.stock_id = d.id \n"
                + "WHERE user_id = ?";
        // Namn på aktie, Symbol, datum, totala priset och procent för säljet
        static final String getAllSales = "SELECT d.name AS n, d.symbol AS s,\n"
                + "fout.date AS d, fout.price * fout.quantity as p, ((fout.price - fin.price)/fin.price) * 100 AS g\n"
                + "FROM fact_transaction_out fout \n"
                + "JOIN dim_stock d ON fout.stock_id = d.id \n"
                + "LEFT JOIN fact_transaction_in fin ON fout.buy_id = fin.id \n"
                + "WHERE fout.user_id = ?;";


        static final String create = "CREATE TABLE IF NOT EXISTS fact_transaction_in (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "user_id INTEGER NOT NULL,\n"
                + "stock_id INTEGER NOT NULL,\n"
                + "date DATE NOT NULL,\n"
                + "quantity INTEGER NOT NULL,\n"
                + "price DECIMAL NOT NULL,\n" // anskaffningsvärdet(getValue)
                + "approxValue DECIMAL,\n"// pris försäljning
                + "growth DECIMAL NOT NULL,"
                + "FOREIGN KEY (user_id) REFERENCES dim_user(id),\n"
                + "FOREIGN KEY (stock_id) REFERENCES dim_stock(id),\n"
                + "UNIQUE(user_id, stock_id)"
                + ");";
        static final String insert = "INSERT INTO fact_transaction_in(user_id, stock_id, \n"
                + "quantity, price, approxValue, date, growth) VALUES(?,?,?,?,?, date('now'),0.0);";

        // inget value ska vara unikt här eftersom alla sälj är unika
        static final String createSell = "CREATE TABLE IF NOT EXISTS fact_transaction_out (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "user_id INTEGER NOT NULL,\n"
                + "stock_id INTEGER NOT NULL,\n"
                + "buy_id INTEGER NOT NULL,\n"
                + "date DATE NOT NULL,\n"
                + "quantity INTEGER NOT NULL,\n"
                + "price DECIMAL NOT NULL,\n"
                + "FOREIGN KEY (user_id) REFERENCES dim_user(id),\n"
                + "FOREIGN KEY (stock_id) REFERENCES dim_stock(id),\n"
                + "FOREIGN KEY (buy_id) REFERENCES fact_transaction_in(id));";
        // lägg till en rad i out(sälj)
        static final String insertSell = "INSERT INTO fact_transaction_out(user_id, stock_id, \n"
                + "buy_id, quantity, price, date) VALUES(?,?,?,?,?,date('now'));";

        // vid sälj av alla aktier
        static final String sellAllStocks = "DELETE FROM fact_transaction_in WHERE user_id = ? AND stock_id = ?;";
        // när man vill veta vilket köp säljet tillhör
        static final String getBuyID = "SELECT id FROM fact_transaction_in WHERE user_id=? AND stock_id=?";

        // approxValue = försäljningspris per aktie (eller det senaste hämtade värdet) * antal holdings
        // getValue ska bara uppdateras vid köp INTE sälj -> denna ger nya anskaffningspris/aktie
        // getValue = (antal * pris/aktie) + (nytt antal * pris/aktie) / (antal + nytt antal)
        // procentuella ökning = approx / (getValue*antal)
        // utveckling räknas ut (getValue - approxValue) / getValue -> growth
        // uppdaterar köp vid köp av fler aktier som redan finns i befintligt holdings.
        static final String updateCash = "UPDATE dim_user SET cash = cash - ? WHERE id = ?;"; // uppdatera antal pengar user har att handla för när köp
        static final String updateCashSell = "UPDATE dim_user SET cash = cash + ? WHERE id = ?;"; // uppdatera antal pengar user har att handla för vid sälj
        static final String updateBuy = "UPDATE fact_transaction_in\n"
                + "SET quantity = quantity + ?, price = (( price * quantity )+( ? * ? ))/( quantity + ? ), approxValue = ?*(?+quantity) \n"
                + "WHERE user_id=? AND stock_id=?;";


        // uppdaterar köp vid sälj av delar den uppdaterar enbart quantity och
        // approxvalue eftersom vid sälj får man inget nytt getValue(price)
        static final String updateBuySub = "UPDATE fact_transaction_in\n"
                + "SET quantity = quantity - ?, approxValue = ?*(quantity-?)\n"
                + "WHERE user_id = ? AND stock_id = ?;";
        static final String isAllowedSell = "SELECT quantity q FROM fact_transaction_in WHERE user_id = ? AND stock_id = ?";
        static final String getCashToUse = "SELECT cash FROM dim_user WHERE id = ?";

        // visar utveckling i procent för allt
        static final String selectGrowthProcentAll = "SELECT (price - (CAST(approxValue AS REAL) / quantity)) / price AS r\n" +
                "FROM fact_transaction_in\n" +
                "WHERE user_id = ?;";
        // visar utvecklingen i procent för en
        static final String selectGrowthProcentOne = "SELECT (price - (CAST(approxValue AS REAL) / quantity)) / price AS r\n" +
                "FROM fact_transaction_in\n" +
                "WHERE user_id = ? AND stock_id = ?;";



    }


}
