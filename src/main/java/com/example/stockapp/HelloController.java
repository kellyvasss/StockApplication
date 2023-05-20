package com.example.stockapp;

import SQL.MySQL;
import SQL.SQLite;
import api.AlphaVantage;
import api.KeyReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.shiro.util.ByteSource;
import stock.Stock;
import user.Hasher;
import user.NumberValidator;
import user.User;
import user.hash;

import java.util.ArrayList;
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
    private TextInputDialog textInputDialog;
    private Alert alert;


    public HelloController() {
        keyReader = new KeyReader("Alpha");
        alphaVantage = new AlphaVantage(keyReader.getAPIKey());
        sqLite = new SQLite("m");
        textInputDialog = new TextInputDialog();
        alert = new Alert(Alert.AlertType.INFORMATION);
    }
    private void setAlert(String title, String content) {
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void onLoginButtonClick() {
        String password = passwordField.getText();
        String userNumber = personNumbField.getText();
        if (password.isEmpty() || userNumber.isEmpty()) { // <- Kontrollera att det finns input
            setAlert("Saknas värde", "Ange personnummer och lösenord innan du klickar på Log in.");
            return;
        }
        // Vi börjar med att kontrollera att användarens input är tio siffror (personnummer)
        if(NumberValidator.isNumeric(userNumber) && NumberValidator.isLenTen(userNumber)) {
            try {
                setAlert("try", "try");
                user = sqLite.getUser(userNumber); //stämmer inputen kollar vi om usern finns i databasen eller ej
                checkPassword(password); // kontrollera att lösenordet stämmer
            } catch (RuntimeException e) {
                // Catchar att usern inte finns i databasen
                // Måste validera att personnummert är korrekt
                setAlert("catch", "innuti catch");
                if(NumberValidator.controllID(NumberValidator.toIntArray(userNumber))) {
                    // Nummret är ett giltligt svenskt personnummer
                    // Lägg till i databasen och lägg till lösenordet och salt
                    System.out.println(NumberValidator.controllID(NumberValidator.toIntArray(userNumber)));
                    setAlert("nummret stämmer", "1");
                    user = new User(userNumber);
                    user.setPasSalt(Hasher.generateSalt().toString());
                    user.setPassword(Hasher.hash(password, ByteSource.Util.bytes(user.getPasSalt())));
                    sqLite.insertUser(user); // <- Lägg till användaren i databasen
                    // Fortsätt programmet, sätt status till visible och dölj inloggningsrutan
                    loginBox.setVisible(false);
                    setUserStatus();


                } else {
                    // Här har användaren angivit ett felaktigt personnummer
                    System.out.println(NumberValidator.controllID(NumberValidator.toIntArray(userNumber)));
                    setAlert("Ogiltligt personnummer", "Du har angivit ett ogiltligt personnummer");

                }
            }
        } else {
            System.out.println(NumberValidator.controllID(NumberValidator.toIntArray(userNumber)));
            setAlert("catch", "catch");
        }
    }
    private void setUserStatus() {
        String[] res = sqLite.getBalanceAndGrowth(user);
        growth.setText(res[0] + " %");
        balance.setText(res[1]);
        status.setVisible(true);
        growth.setVisible(true);
        balance.setVisible(true);


    }

    private void checkPassword(String password) {
        if (attempts == 3) {
            System.exit(0);
        }
            if (Hasher.verify(password, user.getPassword(), user.getPasSalt())) {
                // Här har användaren skrivit in rätt lösenord och den finns i databasen.
                // Låt programmet fortsätta och dölj inloggnings fälten och visa
                // lables med användarens balance och growth och aktuellt innehav.
                setUserStatus();

            }
            else {
                // Här har användaren skrivit in fel lösenord, men den finns i databasen.
                // Be om lösenord igen
                setAlert("Fel lösenord", "Angelösenord igen.");
                passwordField.setOnKeyPressed(event -> {
                    if(event.getCode() == KeyCode.ENTER) {
                        String newPassword = passwordField.getText();
                        checkPassword(newPassword);
                    }
                });
                attempts ++;
            }

    }


    @FXML
    protected void onLogOut() {
        System.exit(0);
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