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

public class AIManager {
    static final int PATH_SMOOTH_ACCURACY = 100;

    private final Map map;
    private final PathFinder pathFinder;
    
    public AIManager(Map map) {
        this.map = map;
        this.pathFinder = new PathFinder(map.getCostGrid());
    }
    
    public List<Point> findRawPath(Vector2D startPos, Vector2D endPos) {
        pathFinder.setCostGrid(map.getCostGrid());
        return pathFinder.findPath(startPos.toPoint(), endPos.toPoint());
    }

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
        for (Point p: Lists.reverse(path)) {
            if (!Physics.linecastQuick(Vector2D.fromPoint(startPoint), Vector2D.fromPoint(p), PATH_SMOOTH_ACCURACY)) {
                return Optional.of(p);
            }
        }

        return Optional.of(startPoint);
//        throw new IllegalStateException("This part of code should never be reached, if path is not empty at least " +
//                "start point is the latest visible point");
    }
}
