package com.knightlore.game.area;

import com.knightlore.game.area.generation.RoomConnection;
import com.knightlore.game.tile.Tile;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Room extends Area {
    public static final int MIN_CONNECTIONS = 2;
    public static final int MAX_CONNECTIONS = 6;

    private Point position;
    private final List<Room> connections = new LinkedList<>();

    public Room(Tile[][] grid) {
        super(grid);
    }

    public void setRoomPosition(Point position) {
        this.position = position;
    }

    public static boolean addConnection(RoomConnection con) {
    	Room source = con.source;
    	Room target = con.target;
    	return addConnection(source,target);
    }
    
    public static boolean addConnection(Room r1, Room r2) {
    	if (r1.connections.size() < MAX_CONNECTIONS &&
        	r2.connections.size() < MAX_CONNECTIONS) {
            r1.connections.add(r2);
            r2.connections.add(r1);
            return true;
        }
        return false;
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
    	return new Point(position.x + width/2, position.y + height/2);
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
    	
    	Room r = (Room) o;
    	return position.equals(r.position);
    }
    
}
