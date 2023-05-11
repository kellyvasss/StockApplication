module com.example.stockapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;

    opens com.example.stockapp to javafx.fxml;
    exports com.example.stockapp;
}