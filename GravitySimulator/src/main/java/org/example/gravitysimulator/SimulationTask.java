package org.example.gravitysimulator;

import javafx.application.*;
import javafx.scene.shape.*;
import org.example.gravitysimulator.AstralBodies.*;
import org.example.gravitysimulator.Utility.*;

import java.util.*;

public class SimulationTask implements Runnable {
    private long previousTime;
    SimulationHandler handler;

    public SimulationTask(long previousTime, SimulationHandler handler) {
        this.previousTime = previousTime;
        this.handler = handler;
    }

    @Override
    public void run() {
        // Calculate real delta time in seconds
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - previousTime) / 1000.0;
        previousTime = currentTime;

        // Scale time
        deltaTime = deltaTime * handler.getTimeScale();

        // Physics step — also handles collision resolution and debris staging
        handler.updatePositions(deltaTime);
        handler.checkCollisions();

        // Snapshot the lists while still on the background thread so the
        // sizes are consistent for the Platform.runLater closure.
        final List<AstralBody> bodiesSnap = new ArrayList<>(handler.bodies);
        final List<Circle>     circlesSnap = new ArrayList<>(handler.bodiesInUI);

        Platform.runLater(() -> {
            // Use the minimum of both lists as a safety guard
            int count = Math.min(bodiesSnap.size(), circlesSnap.size());
            for (int i = 0; i < count; i++) {
                Vector2 pos = bodiesSnap.get(i).getPosition();
                circlesSnap.get(i).setLayoutX(pos.getX());
                circlesSnap.get(i).setLayoutY(pos.getY());
            }
        });
    }
}