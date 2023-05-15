package stock;

import java.util.ArrayList;

public class Portfolio {

    private ArrayList<Stock>holdings;
    private String name;

    public Portfolio() {
        holdings = new ArrayList<>();
    }
    public void setStock(Stock stock) {
        holdings.add(stock);
    }
    public void removeStock(Stock stock) {
        holdings.remove(stock);
    }
    public Double calculateTotal() {
        Double total = 0.0;
        for (Stock stock : holdings) {
            total += stock.getPrice();
        }
        return total;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
