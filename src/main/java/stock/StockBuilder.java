package stock;

public class StockBuilder {
    private Stock stock;
    public StockBuilder() {
        stock = new Stock();
    }
    public StockBuilder symbol(String symbol) {
        stock.setSymbol(symbol);
        return this;
    }
    public StockBuilder country(String country) {
        stock.setCountry(country);
        return this;
    }
    public StockBuilder name(String name) {
        stock.setName(name);
        return this;
    }
    public StockBuilder description(String description) {
        stock.setDescription(description);
        return this;
    }
    public StockBuilder exchange(String exchange) {
        stock.setExchange(exchange);
        return this;
    }
    public StockBuilder currency(String currency) {
        stock.setCurrency(currency);
        return this;
    }
    public StockBuilder sector(String sector) {
        stock.setSector(sector);
        return this;
    }

    public StockBuilder price(Double price) {
        stock.setPrice(price);
        return this;
    }
    public StockBuilder date(String date) {
        stock.setDate(date);
        return this;
    }
    public Stock build() {
        return stock;
    }


}
