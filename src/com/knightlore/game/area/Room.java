package com.knightlore.game.area;

import com.knightlore.game.tile.Tile;

import java.util.LinkedList;
import java.util.List;

public class Room extends Area {
    private int xPosition, yPosition;
    private final List<Room> connections = new LinkedList<>();

    public Room(Tile[][] grid) {
        super(grid);
    }

    public void placeOnMap(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }
}
