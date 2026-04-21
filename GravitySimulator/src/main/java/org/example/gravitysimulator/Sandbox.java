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
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.gravitysimulator.AstralBodies.Asteroid;
import org.example.gravitysimulator.AstralBodies.AstralBody;
import org.example.gravitysimulator.AstralBodies.Planet;
import org.example.gravitysimulator.AstralBodies.Star;
import org.example.gravitysimulator.Utility.Vector2;

import java.util.Random;
import java.util.concurrent.*;

public class Sandbox {

    // Public so SimulationHandler can remove circles from the scene
    public static Pane spaceForPlanets = new Pane();

    public static Scene createScene(Stage stage, SimulationHandler handler) {
        // Top-left buttons (Planet, Star, Asteroid)
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

        //akjsdkjasdhkasjdjashkdjash
        Button zoomPlusBtn = new Button("+");
        zoomPlusBtn.setOnAction( e -> {
            spaceForPlanets.setScaleX(spaceForPlanets.getScaleX()/0.5);
            spaceForPlanets.setScaleY(spaceForPlanets.getScaleY()/0.5);
        });
        Button zoomMinusBtn = new Button("-");
        zoomMinusBtn.setOnAction( e -> {
            spaceForPlanets.setScaleX(spaceForPlanets.getScaleX()/2);
            spaceForPlanets.setScaleY(spaceForPlanets.getScaleY()/2);
        });
        typeButtons.getChildren().addAll(zoomPlusBtn,zoomMinusBtn);

        // Help button (top-right)
        Button helpBtn = new Button("?");
        helpBtn.setPrefSize(30, 30);
        helpBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        helpBtn.setStyle(
                "-fx-background-color: #1a73e8; -fx-text-fill: white;" +
                        "-fx-border-radius: 50; -fx-background-radius: 50; -fx-cursor: hand;"
        );

        // Star canvas (black space background)
        Canvas canvas = new Canvas();
        drawStars(canvas.getGraphicsContext2D(), 800, 560);

        // Overlay: canvas → planet pane → top bar
        BorderPane topBar = new BorderPane();
        topBar.setLeft(typeButtons);
        topBar.setRight(new HBox(helpBtn));
        topBar.setPadding(new Insets(6));
        topBar.setStyle("-fx-background-color: transparent;");
        BorderPane.setAlignment(helpBtn, Pos.TOP_RIGHT);

        // Canvas must be in the stack so it fills the space area
        StackPane spaceArea = new StackPane(canvas, spaceForPlanets, topBar);
        spaceArea.setStyle("-fx-background-color: black;");
        StackPane.setAlignment(topBar, Pos.TOP_LEFT);

        canvas.widthProperty().bind(spaceArea.widthProperty());
        canvas.heightProperty().bind(spaceArea.heightProperty());
        canvas.widthProperty().addListener(o ->
                drawStars(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight()));
        canvas.heightProperty().addListener(o ->
                drawStars(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight()));

        // Bottom control panel
        Label  tempLabel    = createLabel("Temperature:");
        TextField tempField = createField(60);

        Label  radiusLabel    = createLabel("Radius:");
        TextField radiusField = createField(60);

        Label  velocityLabel    = createLabel("Velocity:");
        TextField velocityField = createField(60);

        Label  massLabel    = createLabel("Mass:");
        TextField massField = createField(60);

        Label  angleLabel    = createLabel("Angle:");
        TextField angleField = createField(40);
        Slider angleSlider   = new Slider(0, 360, 180);
        angleSlider.setPrefWidth(130);
        angleSlider.setStyle("-fx-control-inner-background: white;");

        Button launchBtn = new Button("LAUNCH");
        launchBtn.setPrefSize(100, 50);
        launchBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        launchBtn.setStyle(
                "-fx-background-color: #e8670a; -fx-text-fill: white;" +
                        "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );

        HBox row1 = new HBox(10,
                tempLabel, tempField,
                radiusLabel, radiusField,
                velocityLabel, velocityField,
                launchBtn);
        row1.setAlignment(Pos.CENTER_LEFT);

        HBox row2 = new HBox(10,
                massLabel, massField,
                angleLabel, angleField, angleSlider);
        row2.setAlignment(Pos.CENTER_LEFT);

        final String[] selectedType = {"Planet"};

        planetBtn.setOnAction(e -> selectedType[0] = "Planet");
        starBtn.setOnAction(e -> selectedType[0] = "Star");
        asteroidBtn.setOnAction(e -> selectedType[0] = "Asteroid");

        launchBtn.setOnAction(e ->
                handleLaunch(selectedType[0], handler, canvas,
                        massField, radiusField, velocityField,
                        tempField, angleSlider));

        VBox controlPanel = new VBox(8, row1, row2);
        controlPanel.setPadding(new Insets(12, 16, 12, 16));
        controlPanel.setStyle(
                "-fx-background-color: #1a1a1a;" +
                        "-fx-border-color: #555; -fx-border-width: 1 0 0 0;"
        );

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");
        root.setCenter(spaceArea);
        root.setBottom(controlPanel);

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        SimulationTask task = new SimulationTask(System.currentTimeMillis(), handler);
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 16, TimeUnit.MILLISECONDS);

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

    private static void handleLaunch(String type, SimulationHandler handler, Canvas canvas,
                                     TextField mField, TextField rField, TextField vField,
                                     TextField tField, Slider aSlider) {
        try {
            double mass        = Double.parseDouble(mField.getText());
            double radius      = Double.parseDouble(rField.getText());
            double velocityMag = Double.parseDouble(vField.getText());
            double temperature = Double.parseDouble(tField.getText());
            double angleDeg    = aSlider.getValue();

            double rads     = Math.toRadians(angleDeg);
            Vector2 velocity = new Vector2(velocityMag * Math.cos(rads), velocityMag * Math.sin(rads));
            Vector2 position = new Vector2(canvas.getWidth() / 2, canvas.getHeight() / 2);

            AstralBody body;
            switch (type) {
                case "Star":     body = new Star(mass, radius, velocity, position);     break;
                case "Asteroid": body = new Asteroid(mass, radius, velocity, position); break;
                default:         body = new Planet(mass, radius, velocity, position);   break;
            }

            body.setTemperature(temperature);

            Circle circle = new Circle(radius);
            circle.setFill(Color.RED);
            circle.setLayoutX(position.getX());
            circle.setLayoutY(position.getY());

            handler.addBody(body, circle);
            spaceForPlanets.getChildren().add(circle);

        } catch (NumberFormatException e) {
            System.err.println("Invalid input: please enter valid numbers.");
        }
    }
}