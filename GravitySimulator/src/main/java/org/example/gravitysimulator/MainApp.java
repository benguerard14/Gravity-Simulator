package org.example.gravitysimulator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Gravity Simulator");
        stage.setScene(MainMenu.create(stage));
        stage.setFullScreenExitHint("");
        stage.show();
    }
}