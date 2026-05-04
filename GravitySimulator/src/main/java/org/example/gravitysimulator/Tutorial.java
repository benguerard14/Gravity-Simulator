package org.example.gravitysimulator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class Tutorial {

    private static int currentSlide = 0;
    private static final String[] SLIDES = {
            "Do you need a tutorial?",
            "Are you absolutely sure you need a tutorial?",
            "Are you certain you can't figure it out alone?",
            "What if I told you there is no tutorial?",
            "Still looking for instructions?",
            "Just one more time: Do you need a tutorial?",
            "Really you can't figure it alone",
            "What's the problem, you can't figure it out yourself",
            "Keep trying, you might find a tutorial",
            "Don't give up yet",
            "You are almost there",
            "One more"
    };

    public static Scene createScene(Stage stage) {
        // Main container
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-width: 5;");

        // The Question Text
        Text questionText = new Text(SLIDES[0]);
        questionText.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        questionText.setFill(Color.WHITE);
        questionText.setTextAlignment(TextAlignment.CENTER);
        questionText.setWrappingWidth(600);

        // Navigation Button (The "Next" button that loops)
        Button nextBtn = createStyledButton("Yes, I need one.");
        nextBtn.setOnAction(e -> {
            currentSlide = (currentSlide + 1) % SLIDES.length; // Loop back to 0 at the end
            questionText.setText(SLIDES[currentSlide]);
        });

        // Exit Button (Returns to Main Menu)
        Button exitBtn = createStyledButton("Exit to Menu");
        exitBtn.setOnAction(e -> {
            currentSlide = 0; // Reset for next time
            stage.setScene(MainMenu.create(stage));
        });

        // Layout
        root.getChildren().addAll(questionText, nextBtn, exitBtn);

        return new Scene(root, 800, 600);
    }

    // Reusing your specific button style from MainMenu
    private static Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(300, 55);
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
}
