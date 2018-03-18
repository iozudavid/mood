package com.knightlore.game.area.generation;

import java.awt.Point;

import com.knightlore.game.area.Room;

public class RoomConnection implements Comparable<RoomConnection> {
    private final Room source;
    private final Room target;
    private final double distance;

    RoomConnection(Room r1, Room r2) {
        this.source = r1;
        this.target = r2;
        this.distance = euclideanDistance(r1.getCentre(), r2.getCentre());
    }

    double euclideanDistance(Point p, Point q) {
        int xDiff = p.x - q.x;
        int yDiff = p.y - q.y;
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    public Room getSource() {
        return source;
    }

    public Room getTarget() {
        return target;
    }

    @Override
    public int compareTo(RoomConnection c) {
        return Double.compare(distance, c.distance);
    }

    @Override
    public String toString() {
        return source.getPosition() + " --> " + target.getPosition();
    }
}
