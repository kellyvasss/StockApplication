package com.example.stockapp;

import SQL.MySQL;
import SQL.SQLite;
import api.AlphaVantage;
import api.KeyReader;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import stock.Stock;
import user.NumberValidator;
import user.hash;

import java.util.Optional;

public class HelloController {
    @FXML
    private TextArea result;
    @FXML
    private Label welcomeText;
    private TextInputDialog inputDialog;
    private KeyReader keyReader;
    private AlphaVantage alphaVantage;
    private SQLite sqLite;
    private ButtonType btnLogIn;

    public HelloController() {
        keyReader = new KeyReader("Alpha");
        alphaVantage = new AlphaVantage(keyReader.getAPIKey());
        inputDialog = new TextInputDialog();
        inputDialog.setTitle("Logga in");
        inputDialog.setHeaderText(null);
        inputDialog.setContentText("Personnummer:");
        btnLogIn = new ButtonType("Logga in");
        Optional<String> optional = inputDialog.showAndWait();
        String user = optional.get();


    }


    @FXML
    protected void onHelloButtonClick() {

        welcomeText.setText("Welcome to JavaFX Application!");
        //result.setText(alphaVantage.searchEndpoint(result.getText()));
        search();
    }
    private void logIn() {

    }
    private void buy() {

    }
    private void sell() {

    }
    private void seeStatus() {

    }
    // byt ut så att man ej söker i fönstret där resultat visas
    private void search() {
        try {
            result.setText(String.valueOf(alphaVantage.companyOverview(result.getText())));
        } catch (NullPointerException e) {
            try {
                result.setText(alphaVantage.searchEndpoint(result.getText()));
            }
            catch (NumberFormatException ee) {
                result.setText("Ingen matchande sökning på " + result.getText());
            }
        }
    }
}