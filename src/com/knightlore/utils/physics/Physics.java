package com.knightlore.utils.physics;

import java.awt.Rectangle;
import com.knightlore.engine.GameEngine;
import com.knightlore.game.area.Map;
import com.knightlore.utils.Vector2D;

public class Physics {
    
    /**
     * Tests if a point lies inside a given rectangle.
     * 
     * @param point
     *            the point to check inside the rectangle
     * @param rect
     *            the <code>java.awt</code> rectangle to check the point against
     * @returns TRUE if the point lies inside the rectangle, FALSE otherwise
     */
    public static boolean pointInAWTRectangleTest(Vector2D point, Rectangle rect) {
        return rect.contains(point.getX(), point.getY());
    }
    
    /**
     * Casts a line from <code>start</code> to <code>end</code>, checking
     * <code>segments</code> number of points along the line.
     * <p>
     * Note: This is inaccurate, and will miss some intersections.
     * 
     * @param start
     *            the start point of the line
     * @param end
     *            the end point of the line
     * @param segments
     *            how many segments to check
     * @return TRUE if there is something in the way, FALSE otherwise
     */
    public static boolean linecastQuick(Vector2D start, Vector2D end, int segments) {
        if (segments < 1) {
            throw new IllegalStateException("can't linecast with <= 0 segments");
        }
        
        Map m = GameEngine.getSingleton().getWorld().getMap();
        Vector2D diff = end.subtract(start);
        diff = Vector2D.mul(diff, 1d / segments);
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
    
}
