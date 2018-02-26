package com.knightlore.ai;

import java.awt.Point;
import java.util.List;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.pathfinding.PathFinder;

public class AIManager {
    
    private static PathFinder pathFinder;

    public AIManager(Map map) {
        
        double[][] costGrid = new double[map.getWidth()][map.getHeight()];
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                Tile tile = map.getTile(i, j);
                if (tile.getSolidity() == 1) {
                    costGrid[i][j] = Double.POSITIVE_INFINITY; // neat
                } else {
                    costGrid[i][j] = tile.getSolidity() + 1;
                }
            }
        }
        pathFinder = new PathFinder(costGrid);
    }
    
    public static List<Point> findPath(Vector2D startPos, Vector2D endPos) {
        if (pathFinder == null) {
            return null;
        }
        
        return pathFinder.findPath(startPos.toPoint(), endPos.toPoint());
        
    }
    
}
