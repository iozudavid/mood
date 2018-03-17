package com.knightlore.utils.pathfinding;

import com.knightlore.utils.Vector2D;

import java.awt.Point;
import java.util.*;

public class PathFinder {
    private double[][] costGrid;

    public PathFinder(double[][] costGrid) {
        this.costGrid = costGrid;
    }

    public List<Point> findPath(Vector2D start, Vector2D goal) {
        return findPath(start.toPoint(), goal.toPoint());
    }

    public List<Point> findPath(Point start, Point goal) {
        if (!isInBounds(start, costGrid)) {
            throw new IndexOutOfBoundsException("Starting point is out of bound of the costGrid");
        }

        if (!isInBounds(goal, costGrid)) {
            throw new IndexOutOfBoundsException("Goal is out of bound of the costGrid");
        }

        Set<Point> checkedPoints = new HashSet<>();
        Queue<SearchNode> nodesQueue = new PriorityQueue<>();
        nodesQueue.add(new SearchNode(start, goal));
        while (true) {
            SearchNode currNode = nodesQueue.poll();
            if (goal.equals(currNode.getPosition())) {
                return extractBestPathTo(currNode);
            } else {
                List<SearchNode> neighbours = getNeighbouringNodes(currNode, goal, checkedPoints);
                nodesQueue.addAll(neighbours);
            }
        }
    }

    private List<Point> extractBestPathTo(SearchNode node) {
        LinkedList<Point> path = new LinkedList<>();
        path.addFirst(node.getPosition());
        while (node.getPredecessor().isPresent()) {
            node = node.getPredecessor().get();
            path.addFirst(node.getPosition());
        }

        return path;
    }

    private List<SearchNode> getNeighbouringNodes(SearchNode node, Point goal, Set<Point> checkedPoints) {
        List<SearchNode> neighbours = new LinkedList<>();
        Point up = new Point(node.getPosition().x, node.getPosition().y - 1);
        if (isInBounds(up, costGrid) && !checkedPoints.contains(up)) {
            checkedPoints.add(up);
            neighbours.add(new SearchNode(up, goal, costGrid[up.x][up.y], node));
        }

        Point left = new Point(node.getPosition().x - 1, node.getPosition().y);
        if (isInBounds(left, costGrid) && !checkedPoints.contains(left)) {
            checkedPoints.add(left);
            neighbours.add(new SearchNode(left, goal, costGrid[left.x][left.y], node));
        }

        Point down = new Point(node.getPosition().x, node.getPosition().y + 1);
        if (isInBounds(down, costGrid) && !checkedPoints.contains(down)) {
            checkedPoints.add(down);
            neighbours.add(new SearchNode(down, goal, costGrid[down.x][down.y], node));
        }

        Point right = new Point(node.getPosition().x + 1, node.getPosition().y);
        if (isInBounds(right, costGrid) && !checkedPoints.contains(right)) {
            checkedPoints.add(right);
            neighbours.add(new SearchNode(right, goal, costGrid[right.x][right.y], node));
        }

        return neighbours;
    }

    private boolean isInBounds(Point p, double[][] grid) {
        // modifying this because we don't want our path to
        // be on the very edge of the map
        // return p.x >= 0 && p.x < grid.length && p.y >= 0 && p.y <
        // grid[0].length;
        return p.x >= 0 && p.x < grid.length && p.y >= 0 && p.y < grid[0].length;
    }

    public void setCostGrid(double[][] costGrid) {
        this.costGrid = costGrid;
    }
}
