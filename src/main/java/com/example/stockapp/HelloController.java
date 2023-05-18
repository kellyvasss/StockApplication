package com.example.stockapp;

import SQL.MySQL;
import SQL.SQLite;
import api.AlphaVantage;
import api.KeyReader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import stock.Stock;
import user.NumberValidator;
import user.hash;

public class HelloController {
    @FXML
    private TextArea result;
    @FXML
    private Label welcomeText;
    private TextInputDialog inputDialog;
    private KeyReader keyReader;
    private AlphaVantage alphaVantage;
    private SQLite sqLite;

    public HelloController() {
        inputDialog = new TextInputDialog();
        Button login = new Button("Logga in");
        login.setOnAction(event -> onHelloButtonClick());
    }


    @FXML
    protected void onHelloButtonClick() {

        welcomeText.setText("Welcome to JavaFX Application!");
        result.setText(hash.hasha("oooooooooooooooooooooooooooooooooooooooooo"));

    }
}