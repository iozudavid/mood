package com.knightlore.utils.physics;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.utils.Rect;
import com.knightlore.utils.Vector2D;

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
    public static boolean pointInCircleTest(Vector2D point, Vector2D center, double radius) {
        
        return point.subtract(center).sqrMagnitude() < radius * radius;
        
    }
    
    public static double sqrDistPointToLine(Vector2D p, Vector2D a, Vector2D b) {
        // some maths here, may be a faster way... i hate square roots
        Vector2D ab = b.subtract(a);
        Vector2D ap = p.subtract(a);
        
        double t = ap.dot(ab) / ab.magnitude();
        Vector2D x = Vector2D.mul(ap, t);
        
        Vector2D xp = x.subtract(p);
        return xp.sqrMagnitude();
    }
    
    /**
     * 
     * @param start1
     * @param end1
     * @param start2
     * @param end2
     * @return TRUE if the two lines intersect
     */
    public static boolean lineIntersect(Vector2D start1, Vector2D end1, Vector2D start2, Vector2D end2) {
        // convert to parametric
        Vector2D p1 = start1;
        Vector2D d1 = end1.subtract(start1);
        Vector2D p2 = start2;
        Vector2D d2 = end2.subtract(start2);
        
        System.out.println("Intersecting " + p1 + "+s." + d1 + "  with  " + p2 + "+t." + d2);
        
        double x1 = p1.getX();
        double y1 = p1.getY();
        
        double x2 = p2.getX();
        double y2 = p2.getY();
        
        // argh... maths
        // Pa + s*Da = Pb + t*Db
        
        // p1.x + s*d1.x = p2.x + t*d2.x
        // p1.y + s*d1.y = p2.y + t*d2.y
        
        // formula for 's'
        // s = (p2.x + t*d2.x - p1.x) / d1.x
        // s = (p2.y + t*d2.y - p1.y) / d1.y
        
        // assume intersection with 's'
        // d1.y*(p2.x + t*d2.x - p1.x) = d1.x*(p2.y + t*d2.y - p1.y)
        // d1.y*(p2.x - p1.x) + d1.x*(p1.y - p2.y) = t*(d1.x*d2.y - d1.y*d2.x)
        
        // compute 't' the hard way
        
        // first numerator
        double tNumer = d1.getY() * (x2 - x1) + d1.getX() * (y1 - y2);
        // then denominator
        double tDenom = d2.getY() * d1.getX() - d1.getY() * d2.getX();
        if (tDenom == 0) {
            System.out.println("Impossible denominator, parallel?");
            // impossible!, can't divide by 0
            return false;
        }
        // TODO: maybe micro-optimise this even more? no need to divide :P
        double t = tNumer / tDenom;
        if (t < 0 || t > 1) {
            return false;
        }
        
        // now compute 's'
        if (d1.getX() == 0) {
            System.out.println("Impossible denominator x, parallel?");
            // impossible!, can't divide by 0
            return false;
        }
        double s = (x2 - x1) + t * d2.getX() / d1.getX();
        
        if (s < 0 || s > 1) {
            return false;
        }
        
        return true;
    }
    
    /**
     * LINECASTS AGAINST THE WORLD MAP
     * Checks a line by doing point in rectangle tests
     * Note: This is inaccurate, and will miss some intersections
     * 
     * @param start:
     *            the start point of the line
     * @param end:
     *            the end point of the line
     * @param segments:
     *            how many segments to check
     * @return TRUE if there is something in the way
     */
    public static boolean linecastQuick(Vector2D start, Vector2D end, int segments) {
        
        if(segments < 1) {
            return false;
        }
        
        Map m = GameEngine.getSingleton().getWorld().getMap();
        Vector2D diff = end.subtract(start);
        diff = Vector2D.mul(diff, 1d/segments);
        double px = start.getX();
        double py = start.getY();
        int x, y;
        
        for (int i = 0; i < segments; i++) {
            x = (int) px;
            y = (int) py;
            if (m.getTile(x, y).blockLOS()) {
                return true;
            }
            px += diff.getX();
            py += diff.getY();
        }
        
        return false;
    }
    
    /**
     * THIS METHOD DOESN'T WORK PROPERLY
     * DEBUG PLS
     * @param start
     * @param end
     * @return TRUE if there is an obstacle in the way
     */
    public static boolean linecast(Vector2D start, Vector2D end) {
        int minX, minY, maxX, maxY;
        if (start.getX() < end.getX()) {
            minX = (int) start.getX();
            maxX = (int) end.getX();
        } else {
            minX = (int) end.getX();
            maxX = (int) start.getX();
        }
        
        if (start.getY() < end.getY()) {
            minY = (int) start.getY();
            maxY = (int) end.getY();
        } else {
            minY = (int) end.getY();
            maxY = (int) start.getY();
        }
        
        Map m = GameEngine.getSingleton().getWorld().getMap();
        
        // check against all tiles in bounding box
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Tile tile = m.getTile(x, y);
                if (!tile.blockLOS()) {
                    continue;
                }
                
                Vector2D lowerLeft, upperLeft, lowerRight, upperRight;
                lowerLeft = new Vector2D(x, y);
                upperLeft = new Vector2D(x, y + 1);
                lowerRight = new Vector2D(x + 1, y);
                upperRight = new Vector2D(x + 1, y + 1);
                // a line can intersect a tile in 4 ways, so check all 4
                // intersections!
                if (lineIntersect(start, end, lowerLeft, upperLeft)) {
                    return true;
                }
                
                if (lineIntersect(start, end, lowerLeft, lowerRight)) {
                    return true;
                }
                
                if (lineIntersect(start, end, lowerRight, upperRight)) {
                    return true;
                }
                
                if (lineIntersect(start, end, upperLeft, upperRight)) {
                    return true;
                }
                System.out.println("testing "+x+" "+y);
                
            }
            
        }
        
        return false;
    }

    public static boolean pointInRectangleDoubleTest(Vector2D p, Rectangle2D.Double boundingRectangle) {
        return boundingRectangle.contains(p.getX(), p.getY());
    }
    
}
