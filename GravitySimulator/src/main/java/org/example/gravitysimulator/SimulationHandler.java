package org.example.gravitysimulator;

import org.example.gravitysimulator.AstralBodies.AstralBody;
import org.example.gravitysimulator.Utility.*;

import java.util.*;
import java.util.concurrent.*;

public class SimulationHandler {
    //Variables
    ArrayList<AstralBody> bodies = new ArrayList<>();
    private static double GRAVITATIONALCONSTANT = 6.6743 * Math.pow(10,-11);
    ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
    private double updateRatePerSecond = 60;
    private boolean isRunning = false;
    private double timeScale = 1;
    private MainApp GUI;

    //Constructors
    public SimulationHandler(MainApp GUI) {
        this.GUI = GUI;
    }

    public SimulationHandler(MainApp GUI, double updateRatePerSecond, double timeScale) {
        this.GUI = GUI;
        this.updateRatePerSecond = updateRatePerSecond;
        this.timeScale = timeScale;
    }

    public void start() {

    }

    public void close() {

    }

    public void pause() {

    }

    public void resume() {

    }

    public void addBody(AstralBody body) {

    }

    public void removeBody(AstralBody body) {

    }

    public void setTimeScale(double timeScale) {
        this.timeScale = timeScale;
    }

    public double getTimeScale() {
        return timeScale;
    }

    //Need to patch errors related to data types
    public void calculateForces(double deltaTime) {
        for( int i = 0; i < bodies.size(); i++ ) {
            Vector2 forceNet;
            for( int j = 0; j < bodies.size(); j++ ) {
                if(i==j) continue;

                double ri; //to do

                forceNet += (GRAVITATIONALCONSTANT*(bodies.get(j).getMass())*
                        (substractVector(bodies.get(j).getPosition(),bodies.get(i).getPosition())))/
                        (ri*ri*ri)


            }
        }
    }

    public void calculateVelocities(double deltaTime) {

    }

    public void calculatePositions(double deltaTime) {

    }

    public void checkCollisions() {

    }

    public void resolveCollision(AstralBody body1, AstralBody body2) {

    }

    public void spawnAsteroids(AstralBody body) {

    }

    public void simulation() {

    }

    
}
