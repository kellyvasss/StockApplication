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
import javafx.scene.paint.Color;
import org.apache.shiro.util.ByteSource;
import stock.Market;
import stock.Stock;
import user.Hasher;
import user.NumberValidator;
import user.User;
import user.hash;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class HelloController {
    @FXML
    private TextArea result;
    @FXML
    private TextArea result1;
    @FXML
    private Label welcomeText;
    @FXML
    private Button btnBuy;
    @FXML
    private Button btnSell;
    private Stock stock;
    @FXML
    private TextField txfAmount;
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
    private Label lblPassword;
    @FXML
    private Button btnLogIn;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label lblPersNumb;
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
    private void showMarkets() {
        String markets = "MARKET STATUS:\n";
        ArrayList<Market> m = alphaVantage.getMarkets();
        for (Market mar : m) {
             markets += "Name: " + mar.getName()
                     + "\nRegion: " + mar.getCountry()
                     + "\nOpen hours: " + mar.getOpen() + " - " + mar.getClose()
                     + "\nNote: " + mar.getNote()
                     + "\n-------------------------\n";
        } result.setText(markets);
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
                    //loginBox.setVisible(false);
                    setUserStatus();
                    updateUserStatus();


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
    private void updateUserStatus() {
        Double[] res = sqLite.getBalanceAndGrowth(user);
        growth.setText(res[0] + " %");
        if(res[0] < 0) {
            growth.setTextFill(Color.RED);
        } else if (res[0] > 0){
            growth.setTextFill(Color.GREEN);
        } else {
            growth.setTextFill(Color.YELLOW);
        }
        balance.setText(res[1].toString());
    }
    private void setUserStatus() {
        status.setVisible(true);
        growth.setVisible(true);
        balance.setVisible(true);
        btnLogIn.setVisible(false);
        lblPassword.setVisible(false);
        passwordField.setVisible(false);
        lblPersNumb.setText("SEARCH");
        result.setVisible(true);
        result1.setVisible(true);
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
                updateUserStatus();
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
        String search = getSearch();
        if (search.isEmpty()) {
            setAlert("No search", "Search for a company name or ticket/symbol.");
            return;
        }
        try {
            stock = alphaVantage.companyOverview(search);
            Object[] info = alphaVantage.quote(stock.getSymbol());
            result.setText(stock.toString());
            result1.setText((String) info[0] + "\nAnalyst target price: " + stock.getPrice());
            stock.setPrice((Double) info[1]);
            addStock();
            activateBuySell(false);
        } catch (NullPointerException e) {
            result.setText(alphaVantage.searchEndpoint(search));
            if (result.getText().isEmpty()) {
                result.setText("No stock matching the search " + search);
            }
        }

    }
    private void addStock() {
        String symbol = stock.getSymbol();
        sqLite.insertDimStock(stock);
        ArrayList<Stock> stocks = alphaVantage.timeSeriesDailyAdjusted(symbol);
        for (Stock s: stocks) {
            s.setCurrency(stock.getCurrency());
            s.setCountry(stock.getCountry());
            s.setSymbol(symbol);
            sqLite.insertFactStock(s);
        }
    }
    private void activateBuySell(Boolean b) {
        btnBuy.setDisable(b);
        btnSell.setDisable(b);
    }


    private String getSearch() {
        return personNumbField.getText();
    }
    private Integer getAmount() {
        if(NumberValidator.isNumeric(txfAmount.getText())) {
            return Integer.valueOf(txfAmount.getText());
        } else {
            return null;
        }
    }

    @FXML
    private void onBuy() {

    }
    private void setUserStatusHoldings() {
        ArrayList<String> res = sqLite.getGetUserStatusHoldings(user);
        String holdings = "*** CURRENT HOLDINGS ***\n\n";
        for (String s : res) {
            holdings += s + "\n------------------------\n";
        } result.setText(holdings);
    }
    @FXML
    private void onSell() {
        Integer quantity = getAmount();
        if(quantity == null || quantity == 0) {
            setAlert("No amount", "Write a valid amount of stocks you wish to sell");
            return;
        }
        try {
            sqLite.isAllowedSell(user,quantity, stock.getSymbol(), stock.getPrice());
            setAlert("Congratulation!", "You sold " + quantity
                        + " " + stock.getName() + " stocks.\n"
                        + "Price per stock: " + stock.getPrice());
            setUserStatusHoldings();


        } catch (RuntimeException e) {
            setAlert("false", getSearch());
        }
    }

    @FXML
    private void onHoldings() {
    }

    @FXML
    private void onHistory() {
    }

    @FXML
    private void onMarket() {
        showMarkets();
    }

    @FXML
    private void onSearch() {
        search();
    }
}