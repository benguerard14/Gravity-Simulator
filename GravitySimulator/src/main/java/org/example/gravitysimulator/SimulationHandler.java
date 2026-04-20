package org.example.gravitysimulator;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.example.gravitysimulator.AstralBodies.AstralBody;
import org.example.gravitysimulator.AstralBodies.*;
import org.example.gravitysimulator.Utility.*;

import java.util.*;

public class SimulationHandler {

    ArrayList<AstralBody> bodies     = new ArrayList<>();
    ArrayList<Circle>     bodiesInUI = new ArrayList<>();

    private static final double GRAVITATIONALCONSTANT = 6.6743e-11;

    // How many ticks fresh debris is immune to collisions
    private static final int DEBRIS_IMMUNITY_TICKS = 10;

    private double timeScale = 10;
    private MainApp GUI;

    private final List<AstralBody> pendingBodies  = new ArrayList<>();
    private final List<Circle>     pendingCircles = new ArrayList<>();

    public SimulationHandler(MainApp GUI) {
        this.GUI = GUI;
    }

    public SimulationHandler(MainApp GUI, double updateRatePerSecond, double timeScale) {
        this.GUI = GUI;
        this.timeScale = timeScale;
    }

    public ArrayList<AstralBody> getBodies() { return bodies; }

    public void addBody(AstralBody body, Circle circle) {
        bodies.add(body);
        bodiesInUI.add(circle);
    }

    public void removeBody(AstralBody body) {
        int idx = bodies.indexOf(body);
        if (idx == -1) return;
        bodies.remove(idx);
        Circle circle = bodiesInUI.remove(idx);
        javafx.application.Platform.runLater(() ->
                Sandbox.spaceForPlanets.getChildren().remove(circle));
    }

    public void setTimeScale(double timeScale) { this.timeScale = timeScale; }
    public double getTimeScale() { return timeScale; }

    public void updatePositions(double deltaTime) {
        List<Vector2> accArr = new ArrayList<>();

        for (int i = 0; i < bodies.size(); i++) {
            AstralBody body1 = bodies.get(i);
            Vector2 accNet = new Vector2();

            for (int j = 0; j < bodies.size(); j++) {
                if (i == j) continue;
                AstralBody body2 = bodies.get(j);
                Vector2 r = Vector2.subtractVector(body2.getPosition(), body1.getPosition());
                double accConstant = (GRAVITATIONALCONSTANT * body2.getMass())
                        / Math.pow(r.getNorm() + 0.00001, 2);
                accNet = accNet.addVector(Vector2.constMul(Vector2.normalize(r), accConstant));
            }
            accArr.add(accNet);
        }

        for (int i = 0; i < accArr.size(); i++) {
            bodies.get(i).updateVelocity(accArr.get(i), deltaTime);
            bodies.get(i).updatePosition(deltaTime);
        }

        // Count down immunity on every physics step
        for (AstralBody body : bodies) {
            body.tickImmunity();
        }
    }

    public void checkCollisions() {
        Set<AstralBody> toRemove = new HashSet<>();

        for (int i = 0; i < bodies.size(); i++) {
            for (int j = i + 1; j < bodies.size(); j++) {
                AstralBody body1 = bodies.get(i);
                AstralBody body2 = bodies.get(j);

                // Skip immune (fresh debris) or already-dead bodies
                if (body1.isImmune() || body2.isImmune()) continue;
                if (toRemove.contains(body1) || toRemove.contains(body2)) continue;

                double dist = Vector2.subtractVector(body2.getPosition(), body1.getPosition()).getNorm();
                if (dist <= body1.getRadius() + body2.getRadius()) {
                    resolveCollision(body1, body2, toRemove);
                }
            }
        }

        for (AstralBody body : toRemove) {
            removeBody(body);
        }

        // Flush staged debris into the live lists
        for (int i = 0; i < pendingBodies.size(); i++) {
            bodies.add(pendingBodies.get(i));
            bodiesInUI.add(pendingCircles.get(i));
        }
        if (!pendingBodies.isEmpty()) {
            List<Circle> circlesToAdd = new ArrayList<>(pendingCircles);
            javafx.application.Platform.runLater(() ->
                    Sandbox.spaceForPlanets.getChildren().addAll(circlesToAdd));
        }
        pendingBodies.clear();
        pendingCircles.clear();
    }

    private void resolveCollision(AstralBody body1, AstralBody body2, Set<AstralBody> toRemove) {

        // Shared inelastic merge velocity: vF = (m1*v1 + m2*v2) / (m1+m2)
        Vector2 mergedVelocity = Vector2.constMul(
                Vector2.addVector(
                        Vector2.constMul(body1.getVelocity(), body1.getMass()),
                        Vector2.constMul(body2.getVelocity(), body2.getMass())),
                1.0 / (body1.getMass() + body2.getMass()));

        // ── BlackHole wins against everything ─────────────────────────────
        if (body1 instanceof BlackHole || body2 instanceof BlackHole) {
            AstralBody hole  = (body1 instanceof BlackHole) ? body1 : body2;
            AstralBody other = (hole == body1) ? body2 : body1;
            hole.setMass(hole.getMass() + other.getMass());
            hole.setVelocity(mergedVelocity);
            toRemove.add(other);
            return;
        }

        // ── Star absorbs non-stars; two stars → bigger survives ───────────
        if (body1 instanceof Star || body2 instanceof Star) {
            AstralBody survivor = (body1 instanceof Star) ? body1 : body2;
            AstralBody other    = (survivor == body1) ? body2 : body1;
            if (body1 instanceof Star && body2 instanceof Star) {
                survivor = (body1.getRadius() >= body2.getRadius()) ? body1 : body2;
                other    = (survivor == body1) ? body2 : body1;
            }
            survivor.setMass(survivor.getMass() + other.getMass());
            survivor.setVelocity(mergedVelocity);
            toRemove.add(other);
            return;
        }

        // ── Asteroid + Asteroid → simple merge, NO debris ─────────────────
        // This is the critical rule that prevents the exponential chain.
        if (body1 instanceof Asteroid && body2 instanceof Asteroid) {
            AstralBody survivor = (body1.getRadius() >= body2.getRadius()) ? body1 : body2;
            AstralBody other    = (survivor == body1) ? body2 : body1;
            double newRadius = Math.cbrt(
                    Math.pow(body1.getRadius(), 2) + Math.pow(body2.getRadius(), 2));
            survivor.setMass(survivor.getMass() + other.getMass());
            survivor.setRadius(newRadius);
            survivor.setVelocity(mergedVelocity);
            toRemove.add(other);
            int idx = bodies.indexOf(survivor);
            if (idx != -1) {
                Circle c = bodiesInUI.get(idx);
                javafx.application.Platform.runLater(() -> c.setRadius(newRadius));
            }
            return;
        }

        // ── Planet + Asteroid → planet absorbs, NO debris ─────────────────
        if (body1 instanceof Asteroid || body2 instanceof Asteroid) {
            AstralBody planet   = (body1 instanceof Asteroid) ? body2 : body1;
            AstralBody asteroid = (planet == body1) ? body2 : body1;
            double newRadius = Math.cbrt(
                    Math.pow(planet.getRadius(), 3) + Math.pow(asteroid.getRadius(), 3));
            planet.setMass(planet.getMass() + asteroid.getMass());
            planet.setRadius(newRadius);
            planet.setVelocity(mergedVelocity);
            toRemove.add(asteroid);
            int idx = bodies.indexOf(planet);
            if (idx != -1) {
                Circle c = bodiesInUI.get(idx);
                javafx.application.Platform.runLater(() -> c.setRadius(newRadius));
            }
            return;
        }

        // ── Planet + Planet → fragmentation with debris ────────────────────
        double totalMass   = body1.getMass() + body2.getMass();
        double massDebris  = totalMass * (0.03 + Math.random() * 0.05);
        double massLeft    = totalMass - massDebris;

        Vector2 center = Vector2.constMul(
                Vector2.addVector(
                        Vector2.constMul(body1.getPosition(), body1.getMass()),
                        Vector2.constMul(body2.getPosition(), body2.getMass())),
                1.0 / totalMass);

        double mergedRadius     = Math.max(body1.getRadius(), body2.getRadius()) * (1.02 + 0.03 * Math.random());
        double baseDebrisSpeed  = mergedVelocity.getNorm();
        double baseDebrisRadius = (body1.getRadius() + body2.getRadius()) / 2.0;

        Vector2 debrisMomentumSum = new Vector2();
        int n = (int)(Math.random() * 5 + 5);

        for (int i = 0; i < n; i++) {
            double  iMass     = massDebris / n;
            double  angle     = Math.random() * 2 * Math.PI;
            Vector2 dir       = new Vector2(Math.cos(angle), Math.sin(angle));
            double massFactor = Math.cbrt(totalMass);
            double speed = baseDebrisSpeed * (1.5 + 0.00015 * massFactor * (1 + Math.random()*0.2));



            Vector2 iVelocity = Vector2.constMul(dir, speed);

            debrisMomentumSum = debrisMomentumSum.addVector(Vector2.constMul(iVelocity, iMass));

            double  debrisRadius = baseDebrisRadius * (0.2 + 0.4 * Math.random());
            double  spawnDist    = mergedRadius + debrisRadius + 2.0 + Math.random() * 5;
            Vector2 iPosition    = Vector2.addVector(center, Vector2.constMul(dir, spawnDist));

            Asteroid debris = new Asteroid(iMass, debrisRadius, iVelocity, iPosition);
            debris.setSpawnImmunityTicks(DEBRIS_IMMUNITY_TICKS); // immune on arrival

            Circle debrisCircle = new Circle(debrisRadius);
            debrisCircle.setFill(Color.GRAY);
            debrisCircle.setLayoutX(iPosition.getX());
            debrisCircle.setLayoutY(iPosition.getY());

            pendingBodies.add(debris);
            pendingCircles.add(debrisCircle);
        }

        Vector2 vF = mergedVelocity.subtractVector(
                Vector2.constMul(debrisMomentumSum, 1.0 / massLeft));

        body1.setMass(massLeft);
        body1.setVelocity(vF);
        body1.setRadius(mergedRadius);
        body1.setPosition(center);
        toRemove.add(body2);
    }

    public void spawnAsteroids(AstralBody body) { }
}