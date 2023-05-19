package com.example.stockapp;

import SQL.MySQL;
import SQL.SQLite;
import api.AlphaVantage;
import api.KeyReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import stock.Stock;
import user.NumberValidator;
import user.hash;

import java.util.Optional;

public class HelloController {
    @FXML
    private TextArea result;
    @FXML
    private Label welcomeText;

    private KeyReader keyReader;
    private AlphaVantage alphaVantage;
    private SQLite sqLite;
    @FXML
    private VBox loginBox;
    @FXML
    private TextField personNumbField;

    @FXML
    private PasswordField passwordField;


    public HelloController() {
        keyReader = new KeyReader("Alpha");
        alphaVantage = new AlphaVantage(keyReader.getAPIKey());

    }
    public void onLoginButtonClick() {
        String password = passwordField.getText();
        String userNumber = personNumbField.getText();
    }

    @FXML
    protected void onLogOut() {

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