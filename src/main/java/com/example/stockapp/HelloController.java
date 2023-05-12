package com.example.stockapp;

import SQL.MySQL;
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
    public HelloController() {
        keyReader = new KeyReader("Alpha");
        alphaVantage = new AlphaVantage(keyReader.getAPIKey());
        mySQL = new MySQL("root", "&password=***");
    }

    @FXML
    protected void onHelloButtonClick() {


        welcomeText.setText("Welcome to JavaFX Application!");

        result.setText(alphaVantage.searchEndpoint("apple").toString());

    }
}