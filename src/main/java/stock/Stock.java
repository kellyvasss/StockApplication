package stock;

public class Stock {
    private String symbol;
    private String name;
    private String description;
    private String exchange;
    private String currency;
    private String sector;
    private String industry;
    private String country;
    private String date;
    private Double price;


    @Override
    public String toString() {
        return "Name: " + name
                + "\nSymbol: " + symbol
                + "\nDescription: " + description
                + "\nExchange: " + exchange
                + "\nSector: " + sector
                + "\nIndustry: " + industry
                + "\nCountry: " + country
                + "\nCurrency: " + currency;
    }
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }


    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
