module com.example.stockapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires org.json;
    requires com.fasterxml.jackson.databind;
    requires java.sql;

    requires shiro.core;

    opens com.example.stockapp to javafx.fxml;
    exports com.example.stockapp;
}