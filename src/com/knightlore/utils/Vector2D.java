package com.knightlore.utils;

import java.awt.Point;

/**
 * A representation of 2D double precision coordinates in our game
 * 
 * @authors James, Joe
 *
 */
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
    
    /**
     * Adds two vectors together.
     * 
     * @param a
     * @param b
     * @returns a new Vector2D with the result
     */
    public static Vector2D add(Vector2D a, Vector2D b) {
        return new Vector2D(a.x + b.x, a.y + b.y);
    }
    
    /**
     * Pairwise multiplies two vectors together.
     * 
     * @param a
     * @param b
     * @returns a new Vector2D with the result
     */
    public static Vector2D mul(Vector2D a, Vector2D b) {
        return new Vector2D(a.x * b.x, a.y * b.y);
    }
    
    /**
     * Multiplies a vector by a scalar
     * 
     * @param a
     * @param b
     * @returns a new Vector2D with the result
     */
    public static Vector2D mul(Vector2D a, double b) {
        return new Vector2D(a.x * b, a.y * b);
    }
    
    /**
     * Subtracts two vectors.
     * 
     * @param a
     * @param b
     * @returns a new Vector2D with the result
     */
    public static Vector2D sub(Vector2D a, Vector2D b) {
        return new Vector2D(a.x - b.x, a.y - b.y);
    }
    
    public Vector2D(Point p) {
        this((double) p.x, (double) p.y);
    }
    
    public Vector2D(double _x, double _y) {
        x = _x;
        y = _y;
    }
    
    /**
     * Adds another vector to this one
     * 
     * @param a
     * @param b
     * @returns a new Vector2D with the result
     */
    public Vector2D add(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }
    
    /**
     * Subtracts another vector from this one
     * 
     * @param a
     * @param b
     * @returns a new Vector2D with the result
     */
    public Vector2D subtract(Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }
    
    /**
     * Pairwise multiplies another vector with this one
     * 
     * @param a
     * @param b
     * @returns a new Vector2D with the result
     */
    public Vector2D mul(Vector2D v) {
        return new Vector2D(x * v.x, y * v.y);
    }
    
    /**
     * @returns a new Vector2D that is perpendicular to the current vector
     */
    public Vector2D perpendicular() {
        return new Vector2D(y, -x);
    }
    
    /**
     * Computes the cross product of another vector <code>v</code> with this
     * one.
     * 
     * @param v
     * @returns the cross product of the two vectors
     */
    public double cross(Vector2D v) {
        return x * v.y - y * v.x;
    }
    
    /**
     * Computes the dot product of another vector <code>v</code> with this one.
     * 
     * @param v
     * @returns the dot product of the two vectors
     */
    public double dot(Vector2D v) {
        return (x * v.x) + (y * v.y);
    }
    
    /**
     * Computes the distance between another vector <code>v</code> with this
     * one.
     * 
     * Note: this requires a square root, this may cause performance issues if
     * used in excess
     * 
     * @param v
     * @returns the absolute distance between the two vectors
     */
    public double distance(Vector2D v) {
        double xDist = Math.abs(x - v.x);
        double yDist = Math.abs(y - v.y);
        return Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
    }
    
    /**
     * Computes the length of this vector
     * 
     * Note: this requires a square root, this may cause performance issues if
     * used in excess
     * 
     * @returns the absolute length of this vector
     */
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
        return new Vector2D(x / mag, y / mag);
    }
    
    /**
     * Note: this is a lossy conversion
     * 
     * @returns a new Point representation of this coordinate
     */
    public Point toPoint() {
        return new Point((int) Math.floor(x), (int) Math.floor(y));
    }
    
    @Override
    public String toString() {
        return String.format("(%.20f, %.20f)", x, y);
    }
    
    /**
     * 
     * @param point
     * @returns a new Vector2D at the centre of the location specified by
     *          <code> point </code>
     */
    public static Vector2D fromTilePoint(Point point) {
        return new Vector2D(point.x + 0.5, point.y + 0.5);
    }
    
    /**
     * 
     * @param x
     * @param y
     * @returns a new Vector2D at the centre of this location
     */
    public static Vector2D fromGridRef(int x, int y) {
        return new Vector2D(x + 0.5, y + 0.5);
    }
    
    /**
     * 
     * Note: no correction for the centre of the tile is aplied here.
     * 
     * @param point
     * @returns a new Vector2D representation of the point
     */
    public static Vector2D fromPoint(Point point) {
        return new Vector2D(point.x, point.y);
    }
    
    @Override
    public boolean equals(Object v) {
        if (v == null) {
            return false;
        }
        
        if (!(v instanceof Vector2D)) {
            return false;
        }
        
        Vector2D vector = (Vector2D) v;
        return equals(vector, 0);
    }
    
    public boolean equals(Vector2D v, double epsilon) {
        return v != null && Math.abs(x - v.x) <= epsilon && Math.abs(y - v.y) <= epsilon;
    }
}
