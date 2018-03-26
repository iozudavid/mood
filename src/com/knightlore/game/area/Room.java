package com.knightlore.game.area;

import com.knightlore.game.area.generation.RoomConnection;
import com.knightlore.game.tile.Tile;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Room extends Area {
    
    private final List<Room> connections = new LinkedList<>();
    private int minConnections = 2;
    private int maxConnections = 6;
    private Point position = new Point(0, 0);
    
    public Room(Tile[][] grid) {
        super(grid);
    }
    
    public Room(Tile[][] grid, int minCon, int maxCon) {
        super(grid);
        minConnections = minCon;
        maxConnections = maxCon;
    }
    
    public static boolean addConnection(RoomConnection connection) {
        Room source = connection.getSource();
        Room target = connection.getTarget();
        return addConnection(source, target);
    }
    
    public static boolean addConnection(Room r1, Room r2) {
        if (r1.connections.size() < r1.getMaxConnections() && r2.connections.size() < r2.getMaxConnections()) {
            r1.connections.add(r2);
            r2.connections.add(r1);
            return true;
        }
        
        return false;
    }
    
    public void setRoomPosition(Point position) {
        this.position = position;
    }
    
    public int getMinConnections() {
        return minConnections;
    }
    
    public int getMaxConnections() {
        return maxConnections;
    }
    
    public int getNumConnections() {
        return connections.size();
    }
    
    public List<Room> getConnections() {
        return connections;
    }
    
    public Point getPosition() {
        return position;
    }
    
    public Point getCentre() {
        return new Point(position.x + width / 2, position.y + height / 2);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (o == null) {
            return false;
        }
        
        if (!(o instanceof Room)) {
            return false;
        }
        
        Room r = (Room)o;
        if (!getPosition().equals(r.getPosition())) {
            return false;
        }
        
        if (getHeight() != r.getHeight() || getWidth() != r.getWidth()) {
            return false;
        }
        
        for (int i = 0; i < this.getWidth(); i++) {
            for (int j = 0; j < this.getHeight(); j++) {
                if (!this.getTile(i, j).equals(r.getTile(i, j))) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(width * height);
        
        for (int i = 0; i < this.getWidth(); i++) {
            for (int j = 0; j < this.getHeight(); j++) {
                builder.append(getTile(i, j).toChar());
            }
            
            builder.append('\n');
        }
        
        return builder.toString();
    }
}
