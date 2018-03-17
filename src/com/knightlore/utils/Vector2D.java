package com.knightlore.utils;

import java.awt.Point;

public final class Vector2D {
    
    private double x;
    private double y;
    
    public static final Vector2D ZERO = new Vector2D(0, 0);
    public static final Vector2D UP = new Vector2D(0, 1);
    public static final Vector2D DOWN = new Vector2D(0, -1);
    public static final Vector2D LEFT = new Vector2D(-1, 0);
    public static final Vector2D RIGHT = new Vector2D(1, 0);
    public static final Vector2D ONE = new Vector2D(1, 1);
    public static final Vector2D HALF = new Vector2D(0.5, 0.5);
    
    public static Vector2D add(Vector2D a, Vector2D b) {
        return new Vector2D(a.x + b.x, a.y + b.y);
    }
    
    public static Vector2D mul(Vector2D a, Vector2D b) {
        return new Vector2D(a.x * b.x, a.y * b.y);
    }
    
    public static Vector2D mul(Vector2D a, double b) {
        return new Vector2D(a.x * b, a.y * b);
    }
    
    public static Vector2D sub(Vector2D a, Vector2D b) {
        return new Vector2D(a.x - b.x, a.y - b.y);
    }
    
    public Vector2D(Point p) {
        this((double)p.x, (double)p.y);
    }
    
    public Vector2D(double _x, double _y) {
        x = _x;
        y = _y;
    }
    
    // EQUALITY
    public boolean isEqualTo(Vector2D v) {
        return v != null && x == v.x && y == v.y;
    }
    
    public boolean isEqualTo(Vector2D v, double epsilon) {
        return v != null && Math.abs(x - v.x) <= epsilon && Math.abs(y - v.y) <= epsilon;
    }
    
    // allocates a new vector, does not modify the original
    public Vector2D add(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }
    
    // allocates a new vector, does not modify the original
    public Vector2D subtract(Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }
    
    public Vector2D mul(Vector2D v) {
        return new Vector2D(x * v.x, y * v.y);
    }
    
    // allocates a new vector, does not modify the original
    public Vector2D perpendicular() {
        return new Vector2D(y, -x);
    }

    public double cross(Vector2D v) {
        return x * v.y - y * v.x;
    }
    
    public double dot(Vector2D v) {
        return (x * v.x) + (y * v.y);
    }
    
    public double distance(Vector2D v) {
        double xDist = Math.abs(x - v.x);
        double yDist = Math.abs(y - v.y);
        return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
    }
    
    public double magnitude() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
    
    public synchronized double getX() {
        return x;
    }
    
    public synchronized double getY() {
        return y;
    }
    
    public double sqrMagnitude() {
        return (x * x) + (y * y);
    }
    
    public double sqrDistTo(Vector2D v) {
        if (v == null) {
            return 0D;
        }
        return ((x - v.x) * (x - v.x)) + ((y - v.y) * (y - v.y));
    }
    
    /**
     * Makes the vector have length 1, calls magnitude first.
     */
    public Vector2D normalised() {
        double mag = magnitude();
        return new Vector2D(x/mag,y/mag);
    }
    
    /**
     * returns integer coordinate (lossy)
     * 
     * @return
     */
    public Point toPoint() {
        return new Point((int)Math.floor(x), (int)Math.floor(y));
    }
    
    @Override
    public String toString() {
        return String.format("(%.20f, %.20f)", x, y);
    }

    public static Vector2D fromTilePoint(Point point) {
        return new Vector2D(point.x+0.5,point.y+0.5);
    }
    
    public static Vector2D fromPoint(Point point) {
        return new Vector2D(point.x,point.y);
    }
    
}
