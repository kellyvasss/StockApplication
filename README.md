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

En demo-app för köp/sälj av aktier noterade på NYSE och NASDAQ. Du kommer att kunna köpa och sälja. 
Det går att sälja hela sitt innehav eller bara ett visst antal. Likadant funkar det med köp, du kan köpa 
och du kan köpa mer aktier av samma aktie.
Du kan se dina nuvarande holdings och du kan se din sälj historik.
Du kan se börsöppettider runt i världen.
Du kan välja att se din balance i USD (by default), EUR och SEK
Du kan söka på ticket eller en frisökning på alla aktier som har en liknelse med din sökning. Detta är något 
programmet kommer att sköta åt dig. Finns det en exakt matchande sökning för aktiens ticket kommer du ha valet
att köpa eller sälja denna, annars inte.

Alpha Vantage har begränsningar som enbart tillåter 5 call/min och att hämta prisinformation är mestadels 
enbart tillgängligt för aktier som är noterade på NYSE / NASDAQ. Därför är balansen i USD by default. Detta 
går att ändra till SEK / EUR. 

PRO TIP: 

1. API nycklen kan med största sannolikhet bytas ut mot "$" (Istället för "key" sätter du alltså ett "$")
2. För att logga in med ett "ogiltligt personnummer", testa "0000000000", "2222222222" och liknande kombinationer. Detta finns ingen spärr för ännu
3. Går du minus, logga in i databasen manuellt och ändra :D





