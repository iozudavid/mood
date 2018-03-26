package com.knightlore.utils.pathfinding;

import java.awt.Point;
import java.util.Optional;

class SearchNode implements Comparable<SearchNode> {
    private final Point position;
    private final double heuristic;
    private final double costSoFar;
    private final Optional<SearchNode> predecessor;
    
    SearchNode(Point position, Point goal) {
        this.position = position;
        this.heuristic = position.distance(goal);
        this.costSoFar = 0;
        this.predecessor = Optional.empty();
    }
    
    SearchNode(Point position, Point goal, double stateCost, SearchNode predecessor) {
        this.position = position;
        this.heuristic = position.distance(goal); // use manhattan heuristic
        this.costSoFar = predecessor.getCostSoFar() + stateCost;
        this.predecessor = Optional.of(predecessor);
    }
    
    double getCostSoFar() {
        return costSoFar;
    }
    
    Point getPosition() {
        return position;
    }
    
    Optional<SearchNode> getPredecessor() {
        return predecessor;
    }
    
    @Override
    public int compareTo(SearchNode searchNode) {
        return Double.compare(costSoFar + heuristic, searchNode.costSoFar + searchNode.heuristic);
    }
}
