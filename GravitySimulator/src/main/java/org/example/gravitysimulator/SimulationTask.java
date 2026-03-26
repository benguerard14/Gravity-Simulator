package org.example.gravitysimulator;

import javafx.scene.shape.*;
import org.example.gravitysimulator.AstralBodies.*;

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
        double deltaTime = (currentTime - previousTime)/1000;
        previousTime = currentTime;

        //Scaling time
        deltaTime = deltaTime*handler.getTimeScale();

        //Calculate acceleration
        handler.calculateAcc(deltaTime);

        //
    }
}
