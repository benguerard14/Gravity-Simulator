package org.example.gravitysimulator.Utility;

public class Vector2 {

    private double x;
    private double y;

    public Vector2() {}

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector2 addVector(Vector2 vector) {
        newX += vector.getX();
        newY += vector.getY();
        return Vector2(newX,newY);
    }
    public void subtractVector(Vector2 vector) {
        newX -= vector.getX();
        newY -= vector.getY();
        return Vector2(newX,newY);
    }

    public void constMul(double c){
        this.x *= c;
        this.y *= c;
    }

    public double getNorm() {
        return Math.sqrt((x*x)+(y*y));
    }

    public static void addVector(Vector2 vector1, Vector2 vector2) {
        vector1.addVector(vector2);
    }

    public static void subtractVector(Vector2 vector1, Vector2 vector2) {
        vector1.subtractVector(vector2);
    }

    public static double getNorm(Vector2 vector){
        return vector.getNorm();
    }

    public static Vector2 constMul(Vector2 vector, double c){
        vector.constMul(c);
        return vector;
    }
}
