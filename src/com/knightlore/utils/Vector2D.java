package com.knightlore.utils;

public final class Vector2D {
	private double x;
	private double y;
	
	public static final Vector2D ZERO = new Vector2D(0, 0);
	public static final Vector2D UP = new Vector2D(1, 0);
	public static final Vector2D DOWN = new Vector2D(0, -1);
	public static final Vector2D LEFT = new Vector2D(-1, 0);
	public static final Vector2D RIGHT = new Vector2D(1, 0);
	public static final Vector2D ONE = new Vector2D(1, 1);
	
	public Vector2D(double _x, double _y){
		x = _x;
		y = _y;
	}
	
	public void addInPlace(Vector2D v){
		x = x + v.x;
		y = y + v.y;
	}
	
	public Vector2D add(Vector2D v){
		return new Vector2D(x+v.x,y+v.y);
	}
	
	public void subtractInPlace(Vector2D v){
		x = x - v.x;
		y = y - v.y;
	}
	
	public Vector2D subtract(Vector2D v){
		return new Vector2D(x+v.x,y+v.y);
	}
	
	public double dotInPlace(Vector2D v){
		return (x * v.x) + (y * v.y);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public double sqrMagnitude(){
		return (x*x) + (y*y);
	}
}
