package org.example.gravitysimulator.AstralBodies;

import org.example.gravitysimulator.Utility.Vector2;

public abstract class AstralBody {
    private double mass;
    private double radius;
    private Vector2 velocity;
    private Vector2 position;
    private int[] color;
    private double temperature;

    // Ticks remaining where this body is immune to collisions (used for fresh debris)
    private int spawnImmunityTicks;

    AstralBody(double mass, double radius, Vector2 velocity, Vector2 position){
        this.mass = mass;
        this.radius = radius;
        this.velocity = velocity;
        this.position = position;
        this.color = new int[]{128, 128, 128};
        this.spawnImmunityTicks = 0;
    }

    AstralBody(double mass, double radius, Vector2 velocity, Vector2 position, int[] color) {
        this.mass = mass;
        this.radius = radius;
        this.velocity = velocity;
        this.position = position;
        this.color = color;
        this.spawnImmunityTicks = 0;
    }

    public void updateVelocity(Vector2 acc, double deltaTime){
        this.velocity = this.velocity.addVector(Vector2.constMul(acc, deltaTime));
    }

    public void updatePosition(double deltaTime){
        this.position = this.position.addVector(Vector2.constMul(velocity, deltaTime));
    }

    /** Call once per simulation tick. Returns true while still immune. */
    public boolean tickImmunity() {
        if (spawnImmunityTicks > 0) {
            spawnImmunityTicks--;
            return true;
        }
        return false;
    }

    public boolean isImmune() {
        return spawnImmunityTicks > 0;
    }

    public void setSpawnImmunityTicks(int ticks) {
        this.spawnImmunityTicks = ticks;
    }

    public double getMass() { return mass; }
    public void setMass(double mass) { this.mass = mass; }

    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }

    public Vector2 getVelocity() { return velocity; }
    public void setVelocity(Vector2 velocity) { this.velocity = velocity; }

    public Vector2 getPosition() { return position; }
    public void setPosition(Vector2 position) { this.position = position; }

    public int[] getColor() { return color; }
    public void setColor(int[] color) { this.color = color; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
}