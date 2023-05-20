package com.example.stockapp;

import SQL.MySQL;
import SQL.SQLite;
import api.AlphaVantage;
import api.KeyReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import stock.Stock;
import user.Hasher;
import user.NumberValidator;
import user.User;
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
    private AnchorPane status;
    @FXML
    private Label balance;
    @FXML
    private Label growth;

    @FXML
    private PasswordField passwordField;
    private User user;
    private int attempts = 0; // Tre försök att skriva in rätt lösenord



    public HelloController() {
        keyReader = new KeyReader("Alpha");
        alphaVantage = new AlphaVantage(keyReader.getAPIKey());
        sqLite = new SQLite("m");
    }
    public void onLoginButtonClick() {
        String password = passwordField.getText();
        String userNumber = personNumbField.getText();
        // Vi börjar med att kontrollera att användarens input är tio siffror (personnummer)
        if(NumberValidator.isNumeric(userNumber) && NumberValidator.isLenTen(userNumber)) {
            try {
                user = sqLite.getUser(userNumber);
                while (attempts < 3) {
                    if (Hasher.verify(password, user.getPassword(), user.getPasSalt())) {
                        // Här har användaren skrivit in rätt lösenord och den finns i databasen.
                        // Låt programmet fortsätta och dölj inloggnings fälten och visa
                        // lables med användarens balance och growth och aktuellt innehav.
                        balance.setText(user.getCash());
                        growth.setText("Hej");
                        status.setVisible(true);

                    }
                    else {
                        // Här har användaren skrivit in fel lösenord, men den finns i databasen.
                        // Be om lösenord igen
                        attempts ++;
                    }
                }
                if (attempts == 3) {
                    System.exit(0);
                }
            } catch (RuntimeException e) {
                // Catchar att usern inte finns i databasen
                // Måste validera att personnummert är korrekt
                if(NumberValidator.controllID(NumberValidator.toIntArray(userNumber))) {
                    // Nummret är ett giltligt svenskt personnummer
                    // Lägg till i databasen

                } else {
                    // Här har användaren angivit ett felaktigt personnummer
                }
            }
        }
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