package com.knightlore.engine;

public class Vector2 {
	
	public double x;
	public double y;
	
	public final static Vector2 ZERO = new Vector2(0,0);
	public final static Vector2 UP = new Vector2(1,0);
	public final static Vector2 DOWN = new Vector2(0,-1);
	public final static Vector2 LEFT = new Vector2(-1,0);
	public final static Vector2 RIGHT = new Vector2(1,0);
	public final static Vector2 ONE = new Vector2(1,1);
	
	public Vector2(double _x, double _y){
		x = _x;
		y = _y;
	}
	
	
	public void addInPlace(Vector2 v){
		x = x+v.x;
		y= y+v.y;
	}
	
	public void subtractInPlace(Vector2 v){
		x = x-v.x;
		y= y-v.y;
	}
	
	public double dotInPlace(Vector2 v){
		return (x*v.x) + (y * v.y); 
	}
	
	// TODO
	// extend this with methods when we need them
	
}
