package com.knightlore.game.area;

import com.knightlore.game.tile.Tile;

import java.util.LinkedList;
import java.util.List;

public class Room extends Area {
    public static final int MIN_CONNECTIONS = 2;
    public static final int MAX_CONNECTIONS = 6;

    private int xPosition, yPosition;
    private final List<Room> connections = new LinkedList<>();

    public Room(Tile[][] grid) {
        super(grid);
    }

    public void setRoomPosition(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    public boolean addConnection(Room r) {
        if (connections.size() < MAX_CONNECTIONS) {
            connections.add(r);
            return true;
        }

        return false;
    }

    public List<Room> getConnections() {
        return connections;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }
}
