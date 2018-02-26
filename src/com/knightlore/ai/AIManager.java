package com.knightlore.ai;

import java.awt.Point;
import java.util.List;

import com.knightlore.game.area.Map;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.pathfinding.PathFinder;

public class AIManager {

    private final Map map;
    private final PathFinder pathFinder;
    
    public AIManager(Map map) {
        this.map = map;
        this.pathFinder = new PathFinder(map.getCostGrid());
    }
    
    public List<Point> findPath(Vector2D startPos, Vector2D endPos) {
        pathFinder.setCostGrid(map.getCostGrid());
        return pathFinder.findPath(startPos.toPoint(), endPos.toPoint());
    }
}
