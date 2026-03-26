package org.example.gravitysimulator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.util.Random;

public class MainMenu {

    public static Scene create(Stage stage) {
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawStars(gc, 800, 600);

        Text title = new Text("Gravity Simulator");
        title.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 52));
        title.setFill(Color.WHITE);

        Button sandboxBtn = createMenuButton("SandBox Mode");
        Button tutorialBtn = createMenuButton("Tutorial");
        Button exitBtn    = createMenuButton("Exit");

        exitBtn.setOnAction(e -> stage.close());
        sandboxBtn.setOnAction(e -> stage.setScene(Sandbox.createScene(stage)));

        VBox menu = new VBox(20, title, sandboxBtn, tutorialBtn, exitBtn);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(40));

        Pane dotsPane = new Pane();
        addDot(dotsPane, Color.rgb(200, 100, 100), 390, 300);
        addDot(dotsPane, Color.rgb(180, 100, 180), 120, 160);
        addDot(dotsPane, Color.rgb(220, 160,  60), 680, 140);
        addDot(dotsPane, Color.rgb( 80, 180,  80), 140, 440);
        dotsPane.setMouseTransparent(true);

        StackPane root = new StackPane(new StackPane(canvas, menu), dotsPane);
        root.setPrefSize(800, 600);

        Scene scene = new Scene(root, 800, 600);

        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());
        canvas.widthProperty().addListener(o -> drawStars(gc, canvas.getWidth(), canvas.getHeight()));
        canvas.heightProperty().addListener(o -> drawStars(gc, canvas.getWidth(), canvas.getHeight()));

        return scene;
    }

    private static Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(220, 55);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        String defaultStyle =
                "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2.5;" +
                        "-fx-cursor: hand;";
        String hoverStyle =
                "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2.5;" +
                        "-fx-cursor: hand;";
        btn.setStyle(defaultStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(defaultStyle));
        return btn;
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

    private static void addDot(Pane pane, Color color, double x, double y) {
        Circle dot = new Circle(7, color);
        dot.setLayoutX(x);
        dot.setLayoutY(y);
        pane.getChildren().add(dot);
    }
}