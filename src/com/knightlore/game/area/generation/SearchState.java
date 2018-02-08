package com.knightlore.game.area.generation;

import java.util.ArrayList;

import com.knightlore.game.tile.Tile;

public class SearchState implements Comparable<SearchState>{

	private Position pos;
	private Position goal;
	private Tile[][] grid;
	private double[][] perlinNoise;
	
	private double h; //heuristic
	private double g; //cost so far (distorted by Perlin Noise)
	private double f; //f = g + h
	private SearchState pred;
	
	private static double gWeight = 4.5f;
	
	public SearchState(Position pos, Position goal, Tile[][] grid, 
			double[][] perlinNoise) {
		this.pos = pos;
		this.goal = goal;
		this.grid = grid;
		this.perlinNoise = perlinNoise;
		h = calcH();
		g = 0;
		f = g + h;
	}
	
	private SearchState(Position pos, Position goal, Tile[][] grid,
			double[][] perlinNoise, SearchState pred) {
		this.pos = pos;
		this.goal = goal;
		this.grid = grid;
		this.perlinNoise = perlinNoise;
		this.pred = pred;
		g = calcG();
		h= calcH();
		f= g + h;
	}
	
	public boolean isGoal() {
		if(pos.equals(goal))
			return true;
		return false;
	}
	
	private double calcH() {
		int xDiff = pos.getX() - goal.getX();
		int yDiff = pos.getY() - goal.getY();
		// Euclidean distance
		return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
		
		//Manhattan distance
		//return Math.abs(xDiff) + Math.abs(yDiff);
	}
	
	public double getG() {
		return g;
	}
	
	public Position getPosition() {
		return pos;
	}
	
	public double getF() {
		return f;
	}
	
	public static void setGWeight(double w) {
		gWeight = Math.abs(w);
	}
	
	// try and weight g more???
	private double calcG() {
		double predG = pred.getG();
		Position predPos = pred.getPosition();
		double predPerl = perlinNoise[predPos.getX()][ predPos.getY()];
		double thisPerl = perlinNoise[pos.getX()][ pos.getY()];
		//return predG + (thisPerl = predPerl);
		return predG + gWeight * Math.abs( (thisPerl - predPerl) );
	}
	
	public boolean isValid(Position p) {
		
		// ensure is within confines of grid
		if(p.getX() >= grid.length || p.getY() >= grid[0].length ||
				p.getX() < 0 || p.getY() < 0)
			return false;
		
		// ensure is undecided tile
		Tile tile = grid[p.getX()][p.getY()];
		if(tile.toChar() != '?' && tile.toChar() != 'P')
			return false;
		
		return true;
	}
	
	public SearchState getPred() {
		return pred;
	}
	
	public ArrayList<SearchState> getSuccessors(){
		ArrayList<SearchState> successors = new ArrayList<SearchState>();
		Position[] neighbours = {pos.up(), pos.down(), pos.right(), pos.left()};
		
		for(int i=0; i<4; i++) {
			Position nextPos = neighbours[i];
			if(isValid(nextPos))
				successors.add(new SearchState(nextPos, goal, grid, perlinNoise, this));
		}
		
		return successors;
	}

	@Override
	public int compareTo(SearchState oth) {
		if(f == oth.getF())
			return 0;
		if(f < oth.getF())
			return -1;
		return 1;
	}
	
}
