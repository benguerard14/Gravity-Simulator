package org.example.gravitysimulator;

import javafx.scene.shape.Circle;
import org.example.gravitysimulator.AstralBodies.AstralBody;
import org.example.gravitysimulator.AstralBodies.*;
import org.example.gravitysimulator.Utility.*;

import java.util.*;
import java.util.concurrent.*;

public class SimulationHandler {
    //Variables
    ArrayList<AstralBody> bodies = new ArrayList<>();
    ArrayList<Circle> bodiesInUI = new ArrayList<>();
    private static final double GRAVITATIONALCONSTANT = 6.6743 * Math.pow(10,-11);
    private boolean isRunning = false;
    private double timeScale = 1;
    private MainApp GUI;

    //Constructors
    public SimulationHandler(MainApp GUI) {
        this.GUI = GUI;
    }

    public SimulationHandler(MainApp GUI, double updateRatePerSecond, double timeScale) {
        this.GUI = GUI;
        this.timeScale = timeScale;
    }

    public void addBody(AstralBody body) {
        bodies.add(body);
        Circle circle = new Circle(body.getRadius());

        bodiesInUI.add(circle);
    }

    public void removeBody(AstralBody body) {
        bodies.remove(body);
    }

    public void setTimeScale(double timeScale) {
        this.timeScale = timeScale;
    }

    public double getTimeScale() {
        return timeScale;
    }

    //Need to patch errors related to data types
    public void updatePositions(double deltaTime) {
        List<Vector2> accArr = new ArrayList<>();
        for(int i = 0; i < bodies.size(); i++ ) {
            AstralBody body1 = bodies.get(i);

            Vector2 accNet = new Vector2();
            for(int j = 0; j < bodies.size(); j++ ) {
                if(i==j) continue;

                AstralBody body2 = bodies.get(j);

                Vector2 r = Vector2.subtractVector(body2.getPosition(), body1.getPosition());

                double accConstant = (GRAVITATIONALCONSTANT * body2.getMass())/(Math.pow(r.getNorm() + 0.00001, 2));

                accNet.addVector(Vector2.constMul(Vector2.normalize(r), accConstant));

            }

            accArr.add(i, accNet);
        }


        for(int i = 0; i < accArr.size(); i++){
            bodies.get(i).updateVelocity(accArr.get(i), deltaTime);
            bodies.get(i).updatePosition(deltaTime);
        }
    }

    public void checkCollisions() {
        //MIGHT HAVE TO REDO -> Speed to fast and radius too small might cause planets to phase through each other

        //MIGHT ALSO BE AN ISSUE NOT KNOWING WHEN IN BETWEEN PLANETS COLLIDED

        for (int i = 0; i < bodies.size(); i++){
            for(int j = 0; j < bodies.size(); j++){
                if(i == j) continue;

                AstralBody body1 = bodies.get(i);
                AstralBody body2 = bodies.get(j);

                double r = Vector2.subtractVector(body2.getPosition(), body1.getPosition()).getNorm();
                if(r <= (body1.getRadius() + body2.getRadius())){
                    resolveCollision(body1, body2);
                }
            }
        }
    }

    public void resolveCollision(AstralBody body1, AstralBody body2) {

        if((body1 instanceof Star && body2 instanceof Star) || (body1 instanceof BlackHole && body2 instanceof BlackHole)) {

            Vector2 vF = new Vector2();

            double bottomConstant = 1/(body1.getMass()+body2.getMass());

            vF.addVector(Vector2.constMul(((Vector2.constMul(body1.getVelocity(),body1.getMass())).addVector(
                    (Vector2.constMul(body2.getVelocity(),body2.getMass())))
            ),bottomConstant));

            if(body1.getRadius() <= body2.getRadius()) {
                body1.setVelocity(vF);
                removeBody(body2);
            }
            else{
                body2.setVelocity(vF);
                removeBody(body1);
            }

        }

        else if(body1 instanceof Star && body2 instanceof BlackHole) {
            Vector2 vF = new Vector2();

            double bottomConstant = 1/(body1.getMass()+body2.getMass());

            vF.addVector(Vector2.constMul(((Vector2.constMul(body1.getVelocity(),body1.getMass())).addVector(
                    (Vector2.constMul(body2.getVelocity(),body2.getMass())))
            ),bottomConstant));


            body2.setVelocity(vF);
            removeBody(body1);


        }

        else if(body1 instanceof BlackHole && body2 instanceof Star) {
            Vector2 vF = new Vector2();

            double bottomConstant = 1/(body1.getMass()+body2.getMass());

            vF.addVector(Vector2.constMul(((Vector2.constMul(body1.getVelocity(),body1.getMass())).addVector(
                    (Vector2.constMul(body2.getVelocity(),body2.getMass())))
            ),bottomConstant));


            body1.setVelocity(vF);
            removeBody(body2);

        }

        else if(body1 instanceof Star || body2 instanceof Star) {
            Vector2 vF = new Vector2();

            double bottomConstant = 1/(body1.getMass()+body2.getMass());

            vF.addVector(Vector2.constMul(((Vector2.constMul(body1.getVelocity(),body1.getMass())).addVector(
                    (Vector2.constMul(body2.getVelocity(),body2.getMass())))
            ),bottomConstant));

            if(body1 instanceof Star) {
                body1.setVelocity(vF);
                removeBody(body2);
            }
            else{
                body2.setVelocity(vF);
                removeBody(body1);
            }

        }

        else if(body1 instanceof BlackHole || body2 instanceof BlackHole) {
            Vector2 vF = new Vector2();

            double bottomConstant = 1/(body1.getMass()+body2.getMass());

            vF.addVector(Vector2.constMul(((Vector2.constMul(body1.getVelocity(),body1.getMass())).addVector(
                    (Vector2.constMul(body2.getVelocity(),body2.getMass())))
            ),bottomConstant));

            if(body1 instanceof BlackHole) {
                body1.setVelocity(vF);
                removeBody(body2);
            }
            else{
                body2.setVelocity(vF);
                removeBody(body1);
            }

        }

        else{

        }




    }

    public void spawnAsteroids(AstralBody body) {

    }

}

