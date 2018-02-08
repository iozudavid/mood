package com.knightlore.game.area.generation;

import java.awt.*;
import java.util.ArrayList;

import com.knightlore.game.tile.Tile;

public class SearchState implements Comparable<SearchState>{

	private Point pos;
	private Point goal;
	private Tile[][] grid;
	private double[][] perlinNoise;
	
	private double h; //heuristic
	private double g; //cost so far (distorted by Perlin Noise)
	private double f; //f = g + h
	private SearchState pred;
	
	private static double gWeight = 4.5f;
	
	public SearchState(Point pos, Point goal, Tile[][] grid,
			double[][] perlinNoise) {
		this.pos = pos;
		this.goal = goal;
		this.grid = grid;
		this.perlinNoise = perlinNoise;
		h = calcH();
		g = 0;
		f = g + h;
	}
	
	private SearchState(Point pos, Point goal, Tile[][] grid,
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
		return pos.distance(goal);
	}
	
	public double getG() {
		return g;
	}
	
	public Point getPosition() {
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
		Point predPos = pred.getPosition();
		double predPerl = perlinNoise[predPos.x][ predPos.y];
		double thisPerl = perlinNoise[pos.x][ pos.y];
		//return predG + (thisPerl = predPerl);
		return predG + gWeight * Math.abs( (thisPerl - predPerl) );
	}
	
	public boolean isValid(Point p) {
		
		// ensure is within confines of grid
		if(p.getX() >= grid.length || p.getY() >= grid[0].length ||
				p.getX() < 0 || p.getY() < 0)
			return false;
		
		// ensure is undecided tile
		Tile tile = grid[p.x][p.y];
		if(tile.toChar() != '?' && tile.toChar() != 'P')
			return false;
		
		return true;
	}
	
	public SearchState getPred() {
		return pred;
	}
	
	public ArrayList<SearchState> getSuccessors(){
		ArrayList<SearchState> successors = new ArrayList<SearchState>();

		Point up = new Point(pos.x, pos.y + 1);
		if (isValid(up)) {
			successors.add(new SearchState(up, goal, grid, perlinNoise, this));
		}

		Point left = new Point(pos.x - 1, pos.y);
		if (isValid(left)) {
			successors.add(new SearchState(left, goal, grid, perlinNoise, this));
		}

		Point down = new Point(pos.x, pos.y - 1);
		if (isValid(down)) {
			successors.add(new SearchState(down, goal, grid, perlinNoise, this));
		}

		Point right = new Point(pos.x + 1, pos.y);
		if (isValid(right)) {
			successors.add(new SearchState(right, goal, grid, perlinNoise, this));
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
