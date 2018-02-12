package com.knightlore.game.area.generation;

import java.awt.Point;

import com.knightlore.game.area.Room;

public class RoomConnection implements Comparable<RoomConnection>{
	public Room source;
	public Room target;
	public double distance;
	
	public RoomConnection(Room r1, Room r2) {
		source = r1;
		target = r2;
		distance = euclideanDistance(r1.getCentre(), r2.getCentre());
	}

	public double euclideanDistance(Point p, Point q) {
		int xDiff = p.x - q.x;
		int yDiff = p.y - q.y;
		return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
	}
	
	public int compareTo(RoomConnection oth) {
		if (distance == oth.distance) {
			return 0;
		}
		else if (distance > oth.distance) {
			return 1;
		}
		else {
			return -1;
		}
	}
	
	public String toString() {
		return source.getPosition() + " --> " + target.getPosition();
	}
	
}
