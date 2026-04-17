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
        //Calculate real delta time in seconds
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - previousTime)/1000.0;
        previousTime = currentTime;

        //Scaling time
        deltaTime = deltaTime*handler.getTimeScale();

        //Calculate acceleration
        handler.updatePositions(deltaTime);

        //Check Collisions
        handler.checkCollisions();

        //Update to UI
        Platform.runLater(() -> {
            for (int i = 0; i < handler.bodies.size(); i++) {
                Vector2 positionV = handler.bodies.get(i).getPosition();
                handler.bodiesInUI.get(i).setLayoutX(positionV.getX());
                handler.bodiesInUI.get(i).setLayoutY(positionV.getY());
            }
        });
    }
}
