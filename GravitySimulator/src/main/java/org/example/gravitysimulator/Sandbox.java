package org.example.gravitysimulator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.Random;
public class Sandbox {


    public static Scene createScene(Stage stage) {

        //Top-left buttons (Planet, Star, Asteroid)
        Button planetBtn   = createTypeButton("Planet");
        Button starBtn     = createTypeButton("Star");
        Button asteroidBtn = createTypeButton("Asteroid");

        starBtn.setStyle(
                "-fx-background-color: white; -fx-text-fill: black;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        );

        VBox typeButtons = new VBox(6, planetBtn, starBtn, asteroidBtn);
        typeButtons.setPadding(new Insets(10));
        typeButtons.setAlignment(Pos.TOP_LEFT);

        // --- Help button (top-right) ---
        Button helpBtn = new Button("?");
        helpBtn.setPrefSize(30, 30);
        helpBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        helpBtn.setStyle(
                "-fx-background-color: #1a73e8; -fx-text-fill: white;" +
                        "-fx-border-radius: 50; -fx-background-radius: 50; -fx-cursor: hand;"
        );

        // --- Star canvas (black space area) ---
        Canvas canvas = new Canvas();
        canvas.setStyle("-fx-background-color: black;");
        drawStars(canvas.getGraphicsContext2D(), 800, 560);

        // Layout: type buttons top-left, help top-right, over canvas
        BorderPane topBar = new BorderPane();
        topBar.setLeft(typeButtons);
        topBar.setRight(new javafx.scene.layout.HBox(helpBtn));
        topBar.setPadding(new Insets(6));
        topBar.setStyle("-fx-background-color: transparent;");
        BorderPane.setAlignment(helpBtn, Pos.TOP_RIGHT);

        StackPane spaceArea = new StackPane(canvas, topBar);
        spaceArea.setStyle("-fx-background-color: black;");
        StackPane.setAlignment(topBar, Pos.TOP_LEFT);

        canvas.widthProperty().bind(spaceArea.widthProperty());
        canvas.heightProperty().bind(spaceArea.heightProperty());
        canvas.widthProperty().addListener(o ->
                drawStars(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight()));
        canvas.heightProperty().addListener(o ->
                drawStars(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight()));

        // --- Bottom control panel ---
        Label tempLabel = createLabel("Temperature:");
        TextField tempField = createField(60);

        Label radiusLabel = createLabel("Radius:");
        TextField radiusField = createField(60);

        Label velocityLabel = createLabel("Velocity:");
        TextField velocityField = createField(60);

        Label massLabel = createLabel("Mass:");
        TextField massField = createField(60);

        Label angleLabel = createLabel("Angle:");
        TextField angleField = createField(40);
        Slider angleSlider = new Slider(0, 360, 180);
        angleSlider.setPrefWidth(130);
        angleSlider.setStyle("-fx-control-inner-background: white;");

        Button launchBtn = new Button("LAUNCH");
        launchBtn.setPrefSize(100, 50);
        launchBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        launchBtn.setStyle(
                "-fx-background-color: #e8670a; -fx-text-fill: white;" +
                        "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );

        // Row 1: Temperature | Radius | Velocity | LAUNCH
        HBox row1 = new HBox(10,
                tempLabel, tempField,
                radiusLabel, radiusField,
                velocityLabel, velocityField,
                launchBtn
        );
        row1.setAlignment(Pos.CENTER_LEFT);

        // Row 2: Mass | Angle + Slider
        HBox row2 = new HBox(10,
                massLabel, massField,
                angleLabel, angleField, angleSlider
        );
        row2.setAlignment(Pos.CENTER_LEFT);

        VBox controlPanel = new VBox(8, row1, row2);
        controlPanel.setPadding(new Insets(12, 16, 12, 16));
        controlPanel.setStyle(
                "-fx-background-color: #1a1a1a;" +
                        "-fx-border-color: #555; -fx-border-width: 1 0 0 0;"
        );





        // --- Root layout ---
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");
        root.setCenter(spaceArea);
        root.setBottom(controlPanel);

        return new Scene(root, 800, 600);
    }

    private static Button createTypeButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(80, 26);
        btn.setFont(Font.font("Arial", 12));
        btn.setStyle(
                "-fx-background-color: black; -fx-text-fill: white;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: white; -fx-text-fill: black;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: black; -fx-text-fill: white;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        ));
        return btn;
    }

    private static Label createLabel(String text) {
        Label lbl = new Label(text);
        lbl.setTextFill(Color.WHITE);
        lbl.setFont(Font.font("Arial", 13));
        return lbl;
    }

    private static TextField createField(double width) {
        TextField tf = new TextField();
        tf.setPrefWidth(width);
        tf.setPrefHeight(24);
        tf.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-border-color: #888;");
        return tf;
    }

    private static void drawStars(GraphicsContext gc, double w, double h) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, w, h);
        gc.setFill(Color.WHITE);
        Random rand = new Random(42);
        for (int i = 0; i < 200; i++) {
            double x = rand.nextDouble() * w;
            double y = rand.nextDouble() * h;
            double r = rand.nextDouble() * 1.8 + 0.3;
            gc.fillOval(x, y, r, r);
        }
    }
}

