package api;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import stock.Stock;
import stock.StockBuilder;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    // KLAR
    public Stock companyOverview(String symbol) {
        String function = "OVERVIEW";
        String url = base_url + function + "&symbol=" + symbol + "&apikey=" + key;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json =  response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(json);
                Stock stock = new StockBuilder()
                        .symbol(jsonNode.get("Symbol").asText())
                        .name(jsonNode.get("Name").asText())
                        .description(jsonNode.get("Description").asText())
                        .exchange(jsonNode.get("Exchange").asText())
                        .currency(jsonNode.get("Currency").asText())
                        .country(jsonNode.get("Country").asText())
                        .sector(jsonNode.get("Sector").asText())
                        .industry(jsonNode.get("Industry").asText())
                        .build();
                return stock;
            } else {
                throw new IOException("Wrong symbol");
            }
        } catch (IOException e) {
            return null;
        }
    }

  /*  public Stock quote(String symbol) {
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
    }*/

    // sparar alla pris och datum för sökningen. KLAR
    public ArrayList<Stock> timeSeriesDailyAdjusted(String symbol) {
        String function = "TIME_SERIES_DAILY_ADJUSTED";
        String url = base_url + function + "&symbol=" + symbol + "&apikey=" + key;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(json);

                ArrayList<Stock> stocks = new ArrayList<>();

                JsonNode timeSeriesNode = jsonNode.get("Time Series (Daily)");
                if (timeSeriesNode != null) {
                    Iterator<Map.Entry<String, JsonNode>> iterator = timeSeriesNode.fields();
                    while (iterator.hasNext()) {
                        Map.Entry<String, JsonNode> entry = iterator.next();
                        String date = entry.getKey();
                        JsonNode dateData = entry.getValue();
                        if (dateData != null && dateData.has("5. adjusted close")) {
                            String adjustedClose = dateData.get("5. adjusted close").asText();
                            Stock stock = new StockBuilder()
                                    .date(date)
                                    .price(Double.valueOf(adjustedClose))
                                    .symbol(symbol)
                                    .build();
                            stocks.add(stock);
                        }
                    }
                }

                return stocks;
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
