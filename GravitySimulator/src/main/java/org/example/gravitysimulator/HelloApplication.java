package org.example.gravitysimulator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException
    {
        BorderPane pane = new BorderPane();

        pane.setStyle("-fx-background-color: black;");


        Button sandBox = new Button("Sandbox");
        sandBox.setAlignment(Pos.CENTER);

        sandBox.setStyle("-fx-border-width: 10px;-fx-border-color: white; -fx-text-fill: white; -fx-background-color: transparent;");
        sandBox.setStyle("-fx-background-radius: 10");
        sandBox.setPrefSize(200,80);
        pane.setCenter(sandBox);


        Scene scene = new Scene(pane,600,400);
        stage.setTitle("Gravity Simulator");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();


    }




    public static void main(String[] args) {
        launch();
    }
}