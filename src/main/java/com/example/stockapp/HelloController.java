package com.example.stockapp;

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
    public HelloController() {
        keyReader = new KeyReader("Alpha");
        alphaVantage = new AlphaVantage(keyReader.getAPIKey());
    }

    @FXML
    protected void onHelloButtonClick() {


        welcomeText.setText("Welcome to JavaFX Application!");

        result.setText(alphaVantage.companyOverview("IBM"));
    }
}