package com.knightlore.utils;

import java.awt.Rectangle;

public class Physics {

    // for testing UI
    public static Boolean pointInAWTRectangleTest(Vector2D point, Rectangle rect) {
        return rect.contains(point.getX(), point.getY());
    }

    // for use with physics
    public static Boolean pointInRectTest(Vector2D point, Rect rect) {

        if (point.getX() < rect.x) {
            return false;
        }
        if (point.getX() > rect.x + rect.width) {
            return false;
        }
        if (point.getY() < rect.y) {
            return false;
        }
        if (point.getY() > rect.y + rect.height) {
            return false;
        }
        return true;
    }

    // for use with physics
    public static Boolean pointInCircleTest(Vector2D point, Vector2D center, double radius) {

        return point.subtract(center).sqrMagnitude() < radius * radius;

    }

}
