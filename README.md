# StockApp


## Reqiuers:

* apache.shiro.tools.hasher          -> get it from Maven Libaries (v 1.8.0)
* fasterxml.jackson.core             -> get it from Maven Libaries (v 2.14.1)
* fasterxml.jackson.core.databind    -> get it from Maven Libaries (v 2.14.1)
* github.gotson.sqlite.jdbc          -> get it from Maven Libaries (v 3.32.3.8)
* squareup.okhttp3.okhttp1           -> get it from Maven Libaries (v 4.11.0)
* API key from Alpha Vantage, claim it here -> https://www.alphavantage.co/
* openjdk-20

## Store your API-key

* /OneDrive/Dokument/APIKeys/ in this path and make a txf-file "ApiKey={your-key}"

## Use the app

Alpha Vantage har begränsningar som enbart tillåter 5 call/min och att hämta prisinformation är mestadels 
enbart tillgängligt för aktier som är noterade på NYSE / NASDAQ. Därför är balansen i USD by default. Detta 
går att ändra till SEK / EUR. 

PRO TIP: 

1. API nycklen kan med största sannolikhet bytas ut mot "?" (Istället för "key" sätter du alltså ett "?")
2. För att logga in med ett "ogiltligt personnummer", testa "0000000000", "2222222222" och liknande kombinationer. Detta finns ingen spärr för ännu
3. Går du minus, logga in i databasen manuellt och ändra :D



