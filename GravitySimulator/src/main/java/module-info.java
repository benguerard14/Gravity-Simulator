module org.example.gravitysimulator {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.gravitysimulator to javafx.fxml;
    exports org.example.gravitysimulator;
    exports org.example.gravitysimulator.AstralBodies;
    opens org.example.gravitysimulator.AstralBodies to javafx.fxml;
    exports org.example.gravitysimulator.Utility;
    opens org.example.gravitysimulator.Utility to javafx.fxml;
}