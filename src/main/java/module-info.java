module com.example.kalendrium {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.kalendrium to javafx.fxml;
    exports com.example.kalendrium;
}