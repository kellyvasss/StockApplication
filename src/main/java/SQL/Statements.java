package SQL;

public class Statements {
    static class FactStock {
        static String insertFactStock = "INSERT INTO fact_StockPrice(stock_id, date, price, currency_id) "
                + "VALUES(?,?,?,?)";
        static String create = "CREATE TABLE IF NOT EXISTS fact_StockPrice (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "stock_id INTEGER NOT NULL,\n"
                + "date DATE NOT NULL,\n"
                + "price DECIMAL,\n"
                + "currency_id INTEGER NOT NULL,\n"
                + "FOREIGN KEY (stock_id) REFERENCES dim_stock(id),\n"
                + "FOREIGN KEY (currency_id) REFERENCES dim_currency(id)"
                + "UNIQUE(stock_id, date));";
    }

    static class DimStock {
        static String insertDimStock = "INSERT INTO dim_stock(symbol,name,description,"
                + "market_id,country_id,sector_id,industry_id) "
                + "VALUES(?,?,?,?,?,?,?)";
        static String selectID = "SELECT id FROM dim_stock WHERE symbol=?";
        static String create = "CREATE TABLE IF NOT EXISTS dim_stock (\n"
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
        static String count = "SELECT COUNT(*) id FROM dim_industry;";
        static String insert = "INSERT INTO dim_industry(name) VALUES(?)";
        static String selectID = "SELECT id FROM dim_industry WHERE name=?";
        static String create = "CREATE TABLE IF NOT EXISTS dim_industry (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(50) UNIQUE);";

    }

    static class DimCurrency {
        static String selectID = "SELECT id FROM dim_currency WHERE currency=?";
        static String count = "SELECT COUNT(*) id FROM dim_currency;";
        static String insert = "INSERT INTO dim_currency(currency) VALUES(?)";
        static String create = "CREATE TABLE IF NOT EXISTS dim_currency (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "currency VARCHAR(3) UNIQUE);";

    }

    static class DimCountry {
        static String selectID = "SELECT id FROM dim_country WHERE name=?";
        static String insert = "INSERT INTO dim_country(name) VALUES(?)";
        static String count = "SELECT COUNT(*) id FROM dim_country;";
        static String create = "CREATE TABLE IF NOT EXISTS dim_country (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(30) UNIQUE);";

    }

    static class DimMarket {
        static String selectID = "SELECT id FROM dim_market WHERE name=?";
        static String insert = "INSERT INTO dim_market(name) VALUES(?)";
        static String count = "SELECT COUNT(*) id FROM dim_market;";
        static String create = "CREATE TABLE IF NOT EXISTS dim_market (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(10) UNIQUE,\n"
                + "open VARCHAR(5),\n"
                + "close VARCHAR(5),\n"
                + "note VARCHAR(75));";

    }

    static class DimSector {
        static String selectID = "SELECT id FROM dim_sector WHERE name=?";
        static String insert = "INSERT INTO dim_sector(name) VALUES(?)";
        static String count = "SELECT COUNT(*) id FROM dim_sector;";
        static String create = "CREATE TABLE IF NOT EXISTS dim_sector (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(30) UNIQUE);";

    }

    static class DimPortfolio {
        static String insert = "INSERT INTO dim_portfolio(name, created_at, user_id) VALUES(?, ?, ?)";
        static String create = "CREATE TABLE IF NOT EXISTS dim_portfolio (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "name VARCHAR(30),\n"
                + "created_at DATE,\n"
                + "user_id INTEGER NOT NULL,\n"
                + "FOREIGN KEY (user_id) REFERENCES dim_user(id)\n"
                + ");";
    }

    static class DimUser {
        static String insert = "INSERT INTO dim_user(person_id, password, pas_salt) VALUES(?,?,?)";
        static String selectID = "SELECT id FROM dim_user WHERE person_id=?";
        static String create = "CREATE TABLE IF NOT EXISTS dim_user (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "person_id VARCHAR(10) UNIQUE,\n"
                + "password TEXT,\n"
                + "email VARCHAR(50),\n"
                + "pas_salt VARCHAR(16)"
                + ");";
    }

    static class FactTransaction {
        // ändra dim_stock till d, fact_in till in, fact_out till out
        static String getUserStatus = "SELECT d.name, d.symbol, in.quantity - in.price,"
        static String create = "CREATE TABLE IF NOT EXISTS fact_transaction_in (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "user_id INTEGER NOT NULL,\n"
                + "stock_id INTEGER NOT NULL,\n"
                + "date DATE NOT NULL,\n"
                + "quantity INTEGER NOT NULL,\n"
                + "price DECIMAL NOT NULL,\n"
                + "approxValue DECIMAL,\n"
                + "FOREIGN KEY (user_id) REFERENCES dim_user(id),\n"
                + "FOREIGN KEY (stock_id) REFERENCES dim_stock(id),\n"
                + "UNIQUE(user_id, stock_id)"
                + ");";
        static String insert = "INSERT INTO fact_transaction(user_id, stock_id, date, \n"
                + "quantity, price) VALUES(?,?,?,?,?)";
        static String createSell = "CREATE TABLE IF NOT EXISTS fact_transaction_out (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "user_id INTEGER NOT NULL,\n"
                + "stock_id INTEGER NOT NULL,\n"
                + "buy_id INTEGER NOT NULL UNIQUE,\n"
                + "date DATE NOT NULL,\n"
                + "quantity INTEGER NOT NULL,\n"
                + "price DECIMAL NOT NULL,\n"
                + "FOREIGN KEY (user_id) REFERENCES dim_user(id),\n"
                + "FOREIGN KEY (stock_id) REFERENCES dim_stock(id),\n"
                + "FOREIGN KEY (buy_id) REFERENCES fact_transaction_in(id));";
        static String insertSell = "INSERT INTO fact_transaction_out(user_id, stock_id, \n"
                + "buy_id, date, quantity, price) VALUES(?,?,?,?,?,?)";
        static String getBuyID = "SELECT id FROM fact_transaction_in WHERE user_id=? AND stock_id=?";
        static String updateBuy = "UPDATE fact_transaction_in\n"
                + "SET quantity = quantity + ?, price =price + ?\n"
                + "WHERE user_id=? AND stock_id=?;";
        static String updateSell = "UPDATE fact_transaction_out\n"
                + "SET quantity = quantity + ?, price = price + ?\n"
                + "WHERE user_id = ? AND buy_id = ?;";

    }


}
