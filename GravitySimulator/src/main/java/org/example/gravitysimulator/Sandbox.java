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
import org.example.gravitysimulator.AstralBodies.Asteroid;
import org.example.gravitysimulator.AstralBodies.AstralBody;
import org.example.gravitysimulator.AstralBodies.Planet;
import org.example.gravitysimulator.AstralBodies.Star;
import org.example.gravitysimulator.Utility.Vector2;

import java.util.Random;


public class Sandbox {

    public static Scene createScene(Stage stage, SimulationHandler handler) {

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


        final String[] selectedType = {"Planet"}; // Track which button is "active"

        planetBtn.setOnAction(e -> selectedType[0] = "Planet");
        starBtn.setOnAction(e -> selectedType[0] = "Star");
        asteroidBtn.setOnAction(e -> selectedType[0] = "Asteroid");

        launchBtn.setOnAction(e -> {
            handleLaunch(selectedType[0], handler, canvas,
                    massField, radiusField, velocityField,
                    tempField, angleSlider);
        });

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

        // This is the "Glue" that connects the Math to the Pixels
        new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                // 1. Tell the Backend to calculate the next step of physics
                // We use a fixed deltaTime (0.016s) for stability
                handler.updatePositions(0.016);
                handler.checkCollisions();

                // 2. Prepare the Canvas for drawing
                GraphicsContext gc = canvas.getGraphicsContext2D();

                // 3. Clear the previous frame (otherwise planets leave "trails")
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                // 4. Redraw the background stars
                drawStars(gc, canvas.getWidth(), canvas.getHeight());

                // 5. Draw the current state of every AstralBody
                for (AstralBody body : handler.getBodies()) {
                    // Get color from the backend array
                    int[] rgb = body.getColor();
                    gc.setFill(Color.rgb(rgb[0], rgb[1], rgb[2]));

                    double r = body.getRadius();
                    Vector2 pos = body.getPosition();

                    // Draw the body!
                    // We subtract the radius from X and Y because
                    // fillOval starts drawing from the top-left corner,
                    // but our Vector2 is the center of the planet.
                    gc.fillOval(
                            pos.getX() - r,
                            pos.getY() - r,
                            r * 2,
                            r * 2
                    );
                }
            }
        }.start(); // Don't forget to start the timer!

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
            // 1. Parse Data
            double mass = Double.parseDouble(mField.getText());
            double radius = Double.parseDouble(rField.getText());
            double velocityMag = Double.parseDouble(vField.getText());
            double temperature = Double.parseDouble(tField.getText());
            double angleDeg = aSlider.getValue();

            // 2. Convert Angle + Magnitude to Vector2
            double rads = Math.toRadians(angleDeg);
            Vector2 velocity = new Vector2(velocityMag * Math.cos(rads), velocityMag * Math.sin(rads));

            // Spawn at center of canvas
            Vector2 position = new Vector2(canvas.getWidth() / 2, canvas.getHeight() / 2);

            // 3. Create concrete object based on selection
            AstralBody body;
            switch (type) {
                case "Star":     body = new Star(mass, radius, velocity, position); break;
                case "Asteroid": body = new Asteroid(mass, radius, velocity, position); break;
                default:         body = new Planet(mass, radius, velocity, position); break;
            }

            body.setTemperature(temperature);

            // 4. Send to backend
            handler.addBody(body);

        } catch (NumberFormatException e) {
            System.err.println("Invalid input: Please enter valid numbers.");
        }
    }
}

