package com.knightlore.game.manager;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.knightlore.game.area.Map;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.pathfinding.PathFinder;
import com.knightlore.utils.physics.Physics;

/**
 * 
 * @authors James Adey, Kacper Kielak
 *
 */
public class AIManager {
    static final int PATH_SMOOTH_ACCURACY = 100;
    
    private final Map map;
    private final PathFinder pathFinder;
    
    public AIManager(Map map) {
        this.map = map;
        this.pathFinder = new PathFinder(map.getCostGrid());
    }
    
    /**
     * Finds a path through the map from <code> startPos </code> to
     * <code> endPos </code>. This uses A* to work out the path across world map
     * tiles tiles.
     * 
     * @param startPos
     *            - starting location
     * @param endPos
     *            - ending location
     * @returns a list of Points containing the path from start to end
     */
    public List<Point> findRawPath(Vector2D startPos, Vector2D endPos) {
        pathFinder.setCostGrid(map.getCostGrid());
        return pathFinder.findPath(startPos.toPoint(), endPos.toPoint());
    }
    
    /**
     * Given a list of points, this method attempts to perform path smoothing on
     * them, by checking line of sight along the path.
     * 
     * @param path
     * @returns the list of points without unnecessary nodes
     */
    public List<Point> pruneUnnecessaryNodes(List<Point> path) {
        Optional<Point> visiblePointOpt = findLatestVisiblePoint(path);
        if (!visiblePointOpt.isPresent()) {
            return path;
        }
        
        Point visiblePoint = visiblePointOpt.get();
        Iterator<Point> it = path.iterator();
        while (it.next() != visiblePoint) {
            it.remove();
        }
        
        return path;
    }
    
    private Optional<Point> findLatestVisiblePoint(List<Point> path) {
        if (path.isEmpty()) {
            return Optional.empty();
        }
        
        Point startPoint = path.get(0);
        for (Point p : Lists.reverse(path)) {
            if (!Physics.linecastQuick(Vector2D.fromPoint(startPoint), Vector2D.fromPoint(p), PATH_SMOOTH_ACCURACY)) {
                return Optional.of(p);
            }
        }
        
        return Optional.of(startPoint);
    }
}
