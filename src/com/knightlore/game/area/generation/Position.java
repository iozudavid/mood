package com.knightlore.game.area.generation;

public class Position {
	//refers to discrete grid positions
	//for A* search
	private int x;
	private int y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Position up() {
		return new Position(x, y+1);
	}
	
	public Position down() {
		return new Position(x, y-1);
	}
	
	public Position right() {
		return new Position (x+1, y);
	}
	
	public Position left() {
		return new Position (x-1, y);
	}
	
	public boolean equals(Position oth) {
		if(x == oth.getX() && y == oth.getY())
			return true;
		
		return false;
	}
	
	public String toString() {
		return "POS (" + x + ", " + y + ")";
	}
	
}
