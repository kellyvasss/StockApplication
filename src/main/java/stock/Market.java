package stock;

public class Market {
    private String name;
    private String open;
    private String close;
    private String note;
    private String country;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    @Override
    public String toString() {
        return "\nName: " + name
                + "\nCountry: " + country
                + "\nTrading hours: " + open + " - " + close
                + "\nNote: " + note;
    }
}
