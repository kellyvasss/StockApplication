package com.example.stockapp;

import SQL.MySQL;
import SQL.SQLite;
import api.AlphaVantage;
import api.KeyReader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class HelloController {
    @FXML
    private TextArea result;
    @FXML
    private Label welcomeText;
    private KeyReader keyReader;
    private AlphaVantage alphaVantage;
    private MySQL mySQL;
    private SQLite sqLite;
    public HelloController() {
      //  keyReader = new KeyReader("Alpha");
       // alphaVantage = new AlphaVantage(keyReader.getAPIKey());
       // mySQL = new MySQL("root", keyReader.getKey());
        sqLite = new SQLite("hej");
        sqLite.createTableUser();

    }

    @FXML
    protected void onHelloButtonClick() {


        welcomeText.setText("Welcome to JavaFX Application!");

        result.setText(alphaVantage.searchEndpoint("apple").toString());

    }
}