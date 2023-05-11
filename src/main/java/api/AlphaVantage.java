package api;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


                return jsonNode.get("Sector").asText() + "\n" + jsonNode.get("Description").asText();
            } else {
                throw new IOException("Wrong symbol");
            }
        } catch (IOException e) {
            return null;
        }
    }

    public String quote(String symbol) {
        // Denna visar information om senaste försäljningspriset, open, high, low, antal aktier handlade, förändring
        // i pris i pengar och procent jämnfört med förgående dag.
        String function = "GLOBAL_QUOTE";
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

                // ändra detta
                return jsonNode.get("Sector").asText();
            } else {
                throw new IOException("Wrong symbol");
            }
        } catch (IOException e) {
            return null;
        }
    }
    public String timeSeriesDaily(String symbol) {
        // Denna visar information om försäljningspriset, open, high, low, antal aktier handlade, förändring
        // för så lång till tillbaka det sträcker sig. Resultat är dagligt uppdaterad
        String function = "TIME_SERIES_DAILY_ADJUSTED";
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

                // ändra detta
                return jsonNode.get("Sector").asText();
            } else {
                throw new IOException("Wrong symbol");
            }
        } catch (IOException e) {
            return null;
        }
    }
    public List<String> searchEndpoint(String keyword) {
        String function = "SYMBOL_SEARCH";
        String url = base_url + function + "&keywords=" + keyword + "&apikey=" + key;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(json);

                List<String> names = new ArrayList<>();

                JsonNode matchesNode = jsonNode.get("bestMatches");
                for (JsonNode matchNode : matchesNode) {
                    JsonNode nameNode = matchNode.get("2. name");
                    if (nameNode != null && !nameNode.isNull()) {
                        String name = nameNode.asText();
                        names.add(name);
                    }
                }

                return names;
            } else {
                throw new IOException("Wrong symbol");
            }
        } catch (IOException e) {
            return null;
        }
    }
    public List<String> showMarketStatus() {
        String function = "MARKET_STATUS";
        String url = base_url + function + "&apikey=" + key;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(json);

                List<String> names = new ArrayList<>();

                JsonNode matchesNode = jsonNode.get("markets");
                for (JsonNode matchNode : matchesNode) {
                    JsonNode nameNode = matchNode.get("region");
                    if (nameNode != null && !nameNode.isNull()) {
                        String name = nameNode.asText();
                        names.add(name);
                    }
                }

                return names;
            } else {
                throw new IOException("Wrong symbol");
            }
        } catch (IOException e) {
            return null;
        }
    }


}
