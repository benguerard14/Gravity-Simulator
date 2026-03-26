package org.example.gravitysimulator.AstralBodies;

import org.example.gravitysimulator.Utility.Vector2;

public abstract class AstralBody {
    private double mass;
    private double radius;
    private Vector2 velocity;
    private Vector2 position;
    private int[] color;
    private double temperature;

    AstralBody(double mass, double radius, Vector2 velocity, Vector2 position){
        this.mass = mass;
        this.radius = radius;
        this.velocity = velocity;
        this.position = position;
        this.color = new int[]{128, 128, 128};
    }

    AstralBody(double mass, double radius, Vector2 velocity, Vector2 position, int[] color) {
        this.mass = mass;
        this.radius = radius;
        this.velocity = velocity;
        this.position = position;
        this.color = color;
    }

    public void updateVelocity(Vector2 acc, double deltaTime){
        this.velocity = this.velocity.addVector(Vector2.constMul(acc, deltaTime));
        updatePosition(velocity, deltaTime);
    }

    public void updatePosition(Vector2 velocity, double deltaTime){
        this.position = this.position.addVector(Vector2.constMul(velocity, deltaTime));
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public int[] getColor() {
        return color;
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
