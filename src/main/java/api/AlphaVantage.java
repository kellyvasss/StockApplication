package api;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import stock.Market;
import stock.MarketBuilder;
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
        String url = base_url + function + "&symbol=" + symbol + "&apikey=$" ;//+ key;
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

    // För att få fram all information index = 0, för att få fram enbart senaste pris, index = 1
    // För att få fram aktuell symbol, index = 2
    public Object[] quote(String symbol) {
        // Denna visar information om senaste försäljningspriset, open, high, low, antal aktier handlade, förändring
        // i pris i pengar och procent jämnfört med förgående dag.
        String function = "GLOBAL_QUOTE";
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

                JsonNode get = jsonNode.get("Global Quote");
                String str =  "Last trading price: " + get.get("05. price").asText()
                        + "\nOpen: " + get.get("02. open").asText()
                        + "\nHigh: " + get.get("03. high").asText()
                        + "\nLow: " + get.get("04. low").asText()
                        + "\nVolume: " + get.get("06. volume").asText()
                        + "\nLatest trading day: " + get.get("07. latest trading day").asText()
                        + "\nPrevious close: " + get.get("08. previous close").asText();
                Double price = get.get("05. price").asDouble();
                Object[] r = {str,price,symbol};
                return r;

            } else {
                throw new IOException("Wrong symbol");
            }
        } catch (IOException e) {
            return null;
        }
    }

    // sparar alla pris och datum för sökningen. KLAR
    public ArrayList<Stock> timeSeriesDailyAdjusted(String symbol) {
        String function = "TIME_SERIES_DAILY_ADJUSTED";
        String url = base_url + function + "&symbol=" + symbol + "&apikey=$";// + key;

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

    public String searchEndpoint(String keyword) {
        String function = "SYMBOL_SEARCH";
        String url = base_url + function + "&keywords=" + keyword + "&apikey=" + key;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        String result = "";
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(json);

                JsonNode matchesNode = jsonNode.get("bestMatches");
                for (JsonNode matchNode : matchesNode) {
                    result += "Symbol: " + matchNode.get("1. symbol").asText()
                            + "\nName: " + matchNode.get("2. name").asText()
                            + "\nType: " + matchNode.get("3. type").asText()
                            + "\nCountry: " + matchNode.get("4. region").asText()
                            + "\nTrading hours: " + matchNode.get("5. marketOpen").asText()
                            + " - " + matchNode.get("6. marketClose").asText()
                            + "\nCurrency: " + matchNode.get("8. currency").asText()
                            + "\n----------------------------\n";
                }
                return result;
            } else {
                throw new IOException("Wrong symbol");
            }
        } catch (IOException e) {
            return null;
        }
    }
    public ArrayList<Market> getMarkets() {
        ArrayList<Market> markets = new ArrayList<>();
        String function = "MARKET_STATUS";
        String url = base_url + function + "&apikey=" + key;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String json = response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(json);
                JsonNode marketsNode = jsonNode.get("markets");
                for (JsonNode marketNode : marketsNode) {
                    String region = marketNode.get("region").asText();
                    String primaryExchanges = marketNode.get("primary_exchanges").asText();
                    String[] exchanges = primaryExchanges.split(","); // Separera exchanges vid kommatecken
                    String localOpen = marketNode.get("local_open").asText();
                    String localClose = marketNode.get("local_close").asText();
                    String note = marketNode.get("notes").asText();
                    for (String exchange : exchanges) {
                        Market market = new MarketBuilder()
                                .name(exchange.trim())
                                .open(localOpen)
                                .close(localClose)
                                .note(note)
                                .country(region)
                                .build();
                        markets.add(market);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return markets;
    }
    // Returnerar priset att * med USD
    // 1 jpy = 0,00730674 USD
    // 100 * 0,00730674 = 0,730674 USD
    public Double currencyConverter(String fromCurrency) {
        String function = "CURRENCY_EXCHANGE_RATE";
        String url = base_url + function
                + "&from_currency=" + fromCurrency
                + "&to_currency=USD"
                + "&apikey=" + key;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(json);
                JsonNode answer = jsonNode.get("Realtime Currency Exchange Rate");
                return answer.get("5. Exchange Rate").asDouble();
            }
        } catch (Exception e) {
           e.printStackTrace();
        } return null;
    }

}
