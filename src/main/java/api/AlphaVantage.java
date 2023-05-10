package api;


public class AlphaVantage {
    private String key = "";
    private OkHttpClient client;
    //private String function = "";
    private String base_url = "https://www.alphavantage.co/query?function=";
    // Ta in keyReader och kalla på metod för att få fram nyckel
    public AlphaVantage(String key) {
        this.key = key;
        client = new OkHttpClient();
    }

    public static JSONObject companyOverview(String symbol) {
        // Denna kommer låta användaren söka på en ticket och den genererar bolagsbeskrivning, fullständigt namn,
        // sektor, land, symbol, targetprice, genomsnitt för 50 och 200 MA (moving average), utdelningsdag,
        String function = "OVERVIEW";
        String url = base_url + function +
    }


}
