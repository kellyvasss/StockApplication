module com.example.stockapp {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.example.stockapp to javafx.fxml;
    exports com.example.stockapp;
}