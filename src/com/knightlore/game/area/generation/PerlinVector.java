package com.knightlore.game.area.generation;

public class PerlinVector {
	//a simple, 2D non-generic vector class 
	// for implementation ease of Perlin Noise
	private double x;
	private double y;
	
	public PerlinVector(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public static double dotProduct(PerlinVector v, PerlinVector w){
		
		return (v.getX() * w.getX()) + (v.getY() * w.getY());
		
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
}
