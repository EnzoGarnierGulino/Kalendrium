module com.example.kalendrium {
    requires javafx.controls;
    requires javafx.fxml;
    requires ical4j;
    requires json.simple;
    requires java.desktop;
    requires org.controlsfx.controls;


    opens com.example.kalendrium to javafx.fxml;
    exports com.example.kalendrium;
}