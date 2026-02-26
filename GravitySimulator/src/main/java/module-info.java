module org.example.gravitysimulator {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.gravitysimulator to javafx.fxml;
    exports org.example.gravitysimulator;
}