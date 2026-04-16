package org.example.gravitysimulator.Utility;

public class Vector2 {

    private double x;
    private double y;

    public Vector2() {
        this(0, 0);
    }

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


    public Vector2 addVector(Vector2 v) {
        return new Vector2(this.x + v.x, this.y + v.y);
    }

    public Vector2 subtractVector(Vector2 v) {
        return new Vector2(this.x - v.x, this.y - v.y);
    }

    public Vector2 constMul(double c) {
        return new Vector2(this.x * c, this.y * c);
    }

    //Incase
    public static Vector2 directConstMul(Vector2 v, double c) {
        return new Vector2(v.x * c, v.y * c);
    }

    public double getNorm() {
        return Math.sqrt(x * x + y * y);
    }

    public static Vector2 addVector(Vector2 v1, Vector2 v2) {
        return new Vector2(v1.x + v2.x, v1.y + v2.y);
    }

    public static Vector2 subtractVector(Vector2 v1, Vector2 v2) {
        return new Vector2(v1.x - v2.x, v1.y - v2.y);
    }

    public static double getNorm(Vector2 v) {
        return v.getNorm();
    }

    // Safe scalar multiply (immutable)
    public static Vector2 constMul(Vector2 v, double c) {
        return new Vector2(v.x * c, v.y * c);
    }

    // Safe normalize (immutable)
    public static Vector2 normalize(Vector2 v) {
        double n = v.getNorm();
        if (n == 0) return new Vector2(0, 0);
        return new Vector2(v.x / n, v.y / n);
    }
}
