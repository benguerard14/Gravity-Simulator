package org.example.gravitysimulator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.gravitysimulator.AstralBodies.Asteroid;
import org.example.gravitysimulator.AstralBodies.AstralBody;
import org.example.gravitysimulator.AstralBodies.Planet;
import org.example.gravitysimulator.AstralBodies.Star;
import org.example.gravitysimulator.Utility.Vector2;

import java.security.*;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.Math.pow;

public class Sandbox {
    public static Pane spaceForPlanets = new Pane();
    public static Group wrapperForMOVEMENT = new Group(spaceForPlanets);
    public static HashSet<KeyCode> keyPressed = new HashSet<>();
    public static double moveAmount = 5;
    public static double currentScale = 1;

    public static Scene createScene(Stage stage, SimulationHandler handler) {
        spaceForPlanets.setFocusTraversable(true);
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        // Top-left buttons (Planet, Star, Asteroid)
        Button planetBtn   = createTypeButton("Planet");
        Button starBtn     = createTypeButton("Star");
        Button asteroidBtn = createTypeButton("Asteroid");

        planetBtn.setStyle(
                "-fx-background-color: white; -fx-text-fill: black;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        );

        VBox typeButtons = new VBox(6, planetBtn, starBtn, asteroidBtn);
        typeButtons.setPadding(new Insets(10));
        typeButtons.setAlignment(Pos.TOP_LEFT);

        //Zoom Buttons
        Button zoomPlusBtn = new Button("+");
        zoomPlusBtn.setPrefSize(80, 26);
        zoomPlusBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        zoomPlusBtn.setStyle(
                "-fx-background-color: black; -fx-text-fill: white;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        );
        zoomPlusBtn.setOnMouseEntered(e -> zoomPlusBtn.setStyle(
                "-fx-background-color: white; -fx-text-fill: black;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        ));
        zoomPlusBtn.setOnMouseExited(e -> zoomPlusBtn.setStyle(
                "-fx-background-color: black; -fx-text-fill: white;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        ));
        zoomPlusBtn.setOnAction( e -> {
            spaceForPlanets.setScaleX(spaceForPlanets.getScaleX()/0.5);
            spaceForPlanets.setScaleY(spaceForPlanets.getScaleY()/0.5);
            currentScale = spaceForPlanets.getScaleX();
        });

        Button zoomMinusBtn = new Button("-");
        zoomMinusBtn.setPrefSize(80, 26);
        zoomMinusBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        zoomMinusBtn.setStyle(
                "-fx-background-color: black; -fx-text-fill: white;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        );
        zoomMinusBtn.setOnMouseEntered(e -> zoomMinusBtn.setStyle(
                "-fx-background-color: white; -fx-text-fill: black;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        ));
        zoomMinusBtn.setOnMouseExited(e -> zoomMinusBtn.setStyle(
                "-fx-background-color: black; -fx-text-fill: white;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        ));
        zoomMinusBtn.setOnAction( e -> {
            spaceForPlanets.setScaleX(spaceForPlanets.getScaleX()/2);
            spaceForPlanets.setScaleY(spaceForPlanets.getScaleY()/2);
            currentScale = spaceForPlanets.getScaleX();
        });

        typeButtons.getChildren().addAll(zoomPlusBtn,zoomMinusBtn);

        // Help button (top-right)
        Button helpBtn = new Button("?");
        helpBtn.setPrefSize(35, 35);
        helpBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        helpBtn.setStyle(
                "-fx-background-color: #1a73e8; -fx-text-fill: white;" +
                        "-fx-border-radius: 50; -fx-background-radius: 50; -fx-cursor: hand;"
        );
        helpBtn.setOnAction(e -> showHelpDialog(stage));

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
        spaceArea.setAlignment(spaceForPlanets, Pos.CENTER);

        canvas.widthProperty().bind(spaceArea.widthProperty());
        canvas.heightProperty().bind(spaceArea.heightProperty());
        canvas.widthProperty().addListener(o ->
                drawStars(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight()));
        canvas.heightProperty().addListener(o ->
                drawStars(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight()));

        // Bottom control panel with default values
        Label  massLabel    = createLabel("Mass (kg×10²⁴):");
        TextField massField = createField(60);
        massField.setText("5.97"); // Earth-like mass

        Label  radiusLabel    = createLabel("Radius (m × 10¹⁴):");
        TextField radiusField = createField(60);
        radiusField.setText("15");

        Label  velocityLabel    = createLabel("Velocity (m × 10¹⁴/s):");
        TextField velocityField = createField(60);
        velocityField.setText("50");

        Label  tempLabel    = createLabel("Temperature (K):");
        TextField tempField = createField(60);
        tempField.setText("5778"); // Sun-like temperature

        Label  angleLabel    = createLabel("Launch Angle:");
        TextField angleField = createField(60);
        angleField.setEditable(false);
        angleField.setText("0° →");
        angleField.setStyle("-fx-background-color: #333; -fx-text-fill: #4CAF50; -fx-border-color: #888; -fx-font-weight: bold;");

        Slider angleSlider   = new Slider(0, 360, 0);
        angleSlider.setPrefWidth(160);
        angleSlider.setShowTickMarks(true);
        angleSlider.setShowTickLabels(true);
        angleSlider.setMajorTickUnit(90);
        angleSlider.setMinorTickCount(2);
        angleSlider.setBlockIncrement(15);
        angleSlider.setStyle("-fx-control-inner-background: white;");

        // Update angle field with visual direction indicator
        angleSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int angle = newVal.intValue();
            String arrow = getDirectionArrow(angle);
            angleField.setText(angle + "° " + arrow);
        });

        Button launchBtn = new Button("LAUNCH");
        launchBtn.setPrefSize(100, 50);
        launchBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        launchBtn.setStyle(
                "-fx-background-color: #e8670a; -fx-text-fill: white;" +
                        "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        );
        launchBtn.setOnMouseEntered(e -> launchBtn.setStyle(
                "-fx-background-color: #ff7f24; -fx-text-fill: white;" +
                        "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));
        launchBtn.setOnMouseExited(e -> launchBtn.setStyle(
                "-fx-background-color: #e8670a; -fx-text-fill: white;" +
                        "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;"
        ));

        HBox row1 = new HBox(10,
                massLabel, massField,
                radiusLabel, radiusField,
                velocityLabel, velocityField,
                launchBtn);
        row1.setAlignment(Pos.CENTER_LEFT);

        HBox row2 = new HBox(10,
                tempLabel, tempField,
                angleLabel, angleField, angleSlider);
        row2.setAlignment(Pos.CENTER_LEFT);

        final String[] selectedType = {"Planet"};

        planetBtn.setOnAction(e -> {
            selectedType[0] = "Planet";
            updateButtonSelection(planetBtn, starBtn, asteroidBtn);
            updateDefaults(selectedType[0], massField, radiusField, velocityField, tempField);
        });
        starBtn.setOnAction(e -> {
            selectedType[0] = "Star";
            updateButtonSelection(starBtn, planetBtn, asteroidBtn);
            updateDefaults(selectedType[0], massField, radiusField, velocityField, tempField);
        });
        asteroidBtn.setOnAction(e -> {
            selectedType[0] = "Asteroid";
            updateButtonSelection(asteroidBtn, planetBtn, starBtn);
            updateDefaults(selectedType[0], massField, radiusField, velocityField, tempField);
        });

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

        root.setStyle("-fx-background-color: black;");
        root.setCenter(spaceArea);
        root.setBottom(controlPanel);

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        SimulationTask task = new SimulationTask(System.currentTimeMillis(), handler);
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 16, TimeUnit.MILLISECONDS);

        // Track when keys are pressed
        scene.setOnKeyPressed(e -> keyPressed.add(e.getCode()));

        // Track when keys are released
        scene.setOnKeyReleased(e -> keyPressed.remove(e.getCode()));

        javafx.animation.AnimationTimer smoothTimer = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                double adjustedSpeed = moveAmount;
                if (keyPressed.contains(javafx.scene.input.KeyCode.W))
                    spaceForPlanets.setTranslateY(spaceForPlanets.getTranslateY() + adjustedSpeed);
                if (keyPressed.contains(javafx.scene.input.KeyCode.S))
                    spaceForPlanets.setTranslateY(spaceForPlanets.getTranslateY() - adjustedSpeed);
                if (keyPressed.contains(javafx.scene.input.KeyCode.A))
                    spaceForPlanets.setTranslateX(spaceForPlanets.getTranslateX() + adjustedSpeed);
                if (keyPressed.contains(javafx.scene.input.KeyCode.D))
                    spaceForPlanets.setTranslateX(spaceForPlanets.getTranslateX() - adjustedSpeed);
            }
        };
        smoothTimer.start();
        return scene;
    }

    private static String getDirectionArrow(int angle) {
        // Normalize angle to 0-360
        angle = angle % 360;
        if (angle < 0) angle += 360;

        // Return arrow based on angle range
        if (angle >= 337.5 || angle < 22.5) return "→";
        else if (angle >= 22.5 && angle < 67.5) return "↘";
        else if (angle >= 67.5 && angle < 112.5) return "↓";
        else if (angle >= 112.5 && angle < 157.5) return "↙";
        else if (angle >= 157.5 && angle < 202.5) return "←";
        else if (angle >= 202.5 && angle < 247.5) return "↖";
        else if (angle >= 247.5 && angle < 292.5) return "↑";
        else return "↗";
    }

    private static void updateDefaults(String type, TextField massField, TextField radiusField,
                                       TextField velocityField, TextField tempField) {
        switch (type) {
            case "Star":
                massField.setText("1989000"); // Solar mass (much larger)
                radiusField.setText("30");
                velocityField.setText("10");
                tempField.setText("5778"); // Sun surface temperature
                break;
            case "Asteroid":
                massField.setText("0.001"); // Very small mass
                radiusField.setText("5");
                velocityField.setText("100");
                tempField.setText("273"); // Cold
                break;
            default: // Planet
                massField.setText("5.97"); // Earth mass
                radiusField.setText("15");
                velocityField.setText("8");
                tempField.setText("288"); // Earth average
                break;
        }
    }

    private static void updateButtonSelection(Button selected, Button... others) {
        selected.setStyle(
                "-fx-background-color: white; -fx-text-fill: black;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        );
        for (Button btn : others) {
            btn.setStyle(
                    "-fx-background-color: black; -fx-text-fill: white;" +
                            "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
            );
        }
    }

    private static void showHelpDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Gravity Simulator - Help");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a1a;");

        // Title
        Label title = new Label("Gravity Simulator");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#1a73e8"));

        // Physics Section
        Text physicsTitle = new Text("Physics Behind the Simulation");
        physicsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        physicsTitle.setFill(Color.web("#4CAF50"));

        Text physics = new Text(
                "This simulator models gravitational interactions using Newton's Law of Universal Gravitation:\n\n" +
                        "F = G × (m₁ × m₂) / r²\n\n" +
                        "Where:\n" +
                        "• F = Gravitational force between two objects\n" +
                        "• G = Gravitational constant (6.674 × 10⁻¹¹ N⋅m²/kg²)\n" +
                        "• m₁, m₂ = Masses of the two objects\n" +
                        "• r = Distance between their centers\n\n" +
                        "The simulation calculates forces between all bodies and updates their positions\n" +
                        "and velocities in real-time, showing realistic orbital mechanics and collisions."
        );
        physics.setFont(Font.font("Arial", 12));
        physics.setFill(Color.WHITE);

        // Controls Section
        Text controlsTitle = new Text("🎮 Controls");
        controlsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        controlsTitle.setFill(Color.web("#FF9800"));

        Text controls = new Text(
                "Navigation:\n" +
                        "• W/A/S/D - Pan the view up/left/down/right\n" +
                        "• +/− buttons - Zoom in and out\n\n" +
                        "Creating Bodies:\n" +
                        "1. Select type (Planet, Star, or Asteroid)\n" +
                        "2. Adjust parameters (mass, radius, velocity, temperature, angle)\n" +
                        "3. Click LAUNCH to spawn at screen center\n\n" +
                        "Angle Guide:\n" +
                        "• 0° = Right →\n" +
                        "• 90° = Up ↑\n" +
                        "• 180° = Left ←\n" +
                        "• 270° = Down ↓"
        );
        controls.setFont(Font.font("Arial", 12));
        controls.setFill(Color.WHITE);

        // Parameters Section
        Text paramsTitle = new Text("⚙️ Parameters Explained");
        paramsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        paramsTitle.setFill(Color.web("#9C27B0"));

        Text params = new Text(
                "• Mass: Determines gravitational pull strength (kg × 10²⁴)\n" +
                        "  - Larger mass = stronger gravity\n" +
                        "  - Stars typically have much higher mass than planets\n\n" +
                        "• Radius: Visual size of the body (m× 10¹⁴)\n" +
                        "  - Affects collision detection\n\n" +
                        "• Velocity: Initial launch speed ((m× 10¹⁴)/second)\n" +
                        "  - Combined with angle to set trajectory\n" +
                        "  - Higher velocity = faster orbit/escape\n\n" +
                        "• Temperature: Surface temperature in Kelvin\n" +
                        "  - Visual effect only (affects color)\n" +
                        "  - Sun: ~5778K, Earth: ~288K\n\n" +
                        "• Angle: Launch direction (0-360 degrees)\n" +
                        "  - Use slider for precise control\n" +
                        "  - Watch the arrow indicator for direction"
        );
        params.setFont(Font.font("Arial", 12));
        params.setFill(Color.WHITE);

        // Tips Section
        Text tipsTitle = new Text("💡 Tips");
        tipsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        tipsTitle.setFill(Color.web("#E91E63"));

        Text tips = new Text(
                "• Create a heavy star first as an anchor point\n" +
                        "• Launch planets with perpendicular velocity to create orbits\n" +
                        "• Try different velocities to see escape velocity in action\n" +
                        "• Experiment with multi-body systems for chaotic dynamics\n" +
                        "• Press ESC/Enter in input fields to return focus to navigation"
        );
        tips.setFont(Font.font("Arial", 12));
        tips.setFill(Color.WHITE);

        Button closeBtn = new Button("Got it!");
        closeBtn.setStyle(
                "-fx-background-color: #1a73e8; -fx-text-fill: white;" +
                        "-fx-font-size: 14px; -fx-padding: 10 30; -fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> dialog.close());

        ScrollPane scrollPane = new ScrollPane();
        VBox scrollContent = new VBox(12,
                title,
                new Separator(),
                physicsTitle, physics,
                new Separator(),
                controlsTitle, controls,
                new Separator(),
                paramsTitle, params,
                new Separator(),
                tipsTitle, tips);
        scrollContent.setPadding(new Insets(10));
        scrollPane.setContent(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1a1a1a; -fx-background-color: #1a1a1a;");

        content.getChildren().addAll(scrollPane, closeBtn);
        content.setAlignment(Pos.CENTER);

        Scene dialogScene = new Scene(content, 600, 700);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private static Button createTypeButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(80, 26);
        btn.setFont(Font.font("Arial", 12));
        btn.setStyle(
                "-fx-background-color: black; -fx-text-fill: white;" +
                        "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("background-color: white")) {
                btn.setStyle(
                        "-fx-background-color: #333; -fx-text-fill: white;" +
                                "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
                );
            }
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("background-color: white")) {
                btn.setStyle(
                        "-fx-background-color: black; -fx-text-fill: white;" +
                                "-fx-border-color: white; -fx-border-width: 1.5; -fx-cursor: hand;"
                );
            }
        });
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
        tf.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.A || e.getCode() == KeyCode.W || e.getCode() == KeyCode.S || e.getCode() == KeyCode.D) {
                spaceForPlanets.requestFocus();
            }
        });
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
            double screenCenterX = canvas.getWidth() / 2;
            double screenCenterY = canvas.getHeight() / 2;

            // Convert screen-space center to spaceForPlanets local space
            javafx.geometry.Point2D localPoint = spaceForPlanets.parentToLocal(screenCenterX, screenCenterY);

            Vector2 position = new Vector2(localPoint.getX(), localPoint.getY());
            Circle bodyVisual = new Circle(radius);
            bodyVisual.setLayoutX(position.getX());
            bodyVisual.setLayoutY(position.getY());


            AstralBody body;
            switch (type) {
                case "Star":
                    body = new Star(mass, radius, velocity, position);
                    bodyVisual.setFill(Color.YELLOW);
                    break;
                case "Asteroid":
                    body = new Asteroid(mass, radius, velocity, position);
                    bodyVisual.setFill(Color.GRAY);
                    break;
                default:
                    body = new Planet(mass, radius, velocity, position);
                    bodyVisual.setFill(Color.BLUE);
                    break;
            }

            body.setTemperature(temperature);

            handler.addBody(body, bodyVisual);
            spaceForPlanets.getChildren().add(bodyVisual);
            System.out.println("Number of bodies at this moment(" + System.currentTimeMillis() + "): " + handler.bodies.size());

        } catch (NumberFormatException e) {
            System.err.println("Invalid input: please enter valid numbers.");
        }
    }
}