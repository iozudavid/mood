package com.knightlore.game.area.generation;

import com.knightlore.game.area.Room;

public class RoomConnection implements Comparable<RoomConnection>{
	public Room room1;
	public Room room2;
	public double distance;
	
	public RoomConnection(Room r1, Room r2) {
		room1 = r1;
		room2 = r2;
		distance = Point.euclideanDistance(r1.getCentre(), r2.getCentre());
	}

	public int compareTo(RoomConnection oth) {
		if (distance < oth.distance)
			return -1;
		if (distance > oth.distance)
			return 1;
		return 0;
	}
	
}
