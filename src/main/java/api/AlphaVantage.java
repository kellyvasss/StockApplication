package api;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



import java.io.IOException;

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

    public String companyOverview(String symbol) {
        // Denna kommer låta användaren söka på en ticket och den genererar bolagsbeskrivning, fullständigt namn,
        // sektor, land, symbol, targetprice, currency, genomsnitt för 50 och 200 MA (moving average), utdelningsdag,
        // 52 veckors högsta och lägsta kurs
        // Den kräver parametrarna function, symbol och apikey.
        String function = "OVERVIEW";
        String url = base_url + function + "&symbol=" + symbol + "&apikey=" + key;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        // Nu funkar denna metoden. Jag behöver bara lägga till vad jag vill få fram, ex jsonNode.get("Currency").asText(), osv.
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json =  response.body().string();



                ObjectMapper objectMapper = new ObjectMapper();

                JsonNode jsonNode = objectMapper.readTree(json);


                return jsonNode.get("Sector").asText();
            } else {
                throw new IOException("Wrong symbol");
            }
        } catch (IOException e) {
            return null;
        }
    }


}
