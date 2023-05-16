package stock;

public class MarketBuilder {
    private Market market;

    public MarketBuilder() {
        market = new Market();
    }

    public MarketBuilder name(String name) {
        market.setName(name);
        return this;
    }
    public MarketBuilder open(String open) {
        market.setOpen(open);
        return this;
    }
    public MarketBuilder close(String close) {
        market.setClose(close);
        return this;
    }
    public MarketBuilder note(String note) {
        market.setNote(note);
        return this;
    }
    public MarketBuilder country(String country) {
        market.setCountry(country);
        return this;
    }
    public Market build() {
        return market;
    }
}
