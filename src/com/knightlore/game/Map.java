package com.knightlore.game;

import java.util.*;

public class Map {

	/*
	 * TODO: read in maps from files/procedurally generate.
	 */

	private int height;
	private int width;
	
	public int[][] map =
		  { { 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2 }, 
			{ 1, 0, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 2 },
			{ 1, 0, 3, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 0, 2 }, 
			{ 1, 0, 3, 0, 0, 0, 3, 0, 2, 2, 2, 0, 2, 2, 2 },
			{ 1, 0, 3, 0, 0, 0, 3, 0, 2, 0, 0, 0, 0, 0, 2 }, 
			{ 1, 0, 3, 3, 0, 3, 3, 0, 2, 0, 0, 0, 0, 0, 2 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2 }, 
			{ 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 0, 4, 4, 4 },
			{ 1, 0, 0, 0, 0, 0, 1, 4, 0, 0, 0, 0, 0, 0, 4 }, 
			{ 1, 0, 0, 0, 0, 0, 1, 4, 0, 0, 0, 0, 0, 0, 4 },
			{ 1, 0, 0, 2, 0, 0, 1, 4, 0, 3, 3, 3, 3, 0, 4 }, 
			{ 1, 0, 0, 0, 0, 0, 1, 4, 0, 3, 3, 3, 3, 0, 4 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4 }, 
			{ 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4, 4, 4, 4, 4 } };
			
	
	public int[][] getMapArray() {
		return map;
	}

	public Map(){
		
	}
	
	public Map(int w, int h){
		width = w;
		height = h;
		map = new int[w][h];
	}
	
	public void addWalls(){
		for(int i=0; i<width; i++){
			map[i][0]   = 1;
			map[i][height-1] = 1;
		}
		for(int j=0; j<height; j++){
			map[0][j]   = 1;
			map[width-1][j] = 1;
		}
	}
	
	public static Map randMap(){
		Random rand = new Random();
		int w = 10 + rand.nextInt(20);
		int h = 10 + rand.nextInt(20);
		Map m = new Map(w,h);
		
		// make everything a wall
		for(int i=0; i<w; i++){
			for(int j=0; j<h; j++){
				m.map[i][j] = 1;
			}
		}
		
		int r = 0;
		// make horizontal rows of free space
		for(int i=0; i<w; i++){
			r = rand.nextInt(100);
			if (r > 80){
				for(int j=0; j<h; j++){
					m.map[i][j] = 0;
				}
			}else if (r > 40){
				for(int j=0; j<(h/2); j++){
					m.map[i][j] = 0;
				}
			}
		}
		
		// make vertical rows of free space
		for(int j=0; j<h; j++){
			r = rand.nextInt(100);
			if (rand.nextInt(100) > 70){
				for(int i=0; i<w; i++){
					m.map[i][j] = 0;
				}
			}else if (r > 40){
				for(int i=0; i<(w/2); i++){
					m.map[i][j] = 0;
				}
			}
		}
		
		m.makeSym();
		m.addWalls();
		return m;
	}
	
	// reflection in x-axis
	private void makeSym(){
		int[][] symMap = new int[width][height*2];
		for(int j=0; j<height; j++){
			for(int i=0; i<width; i++){
				symMap[i][j] = map[i][j];
				symMap[i][(height*2 -1) - j] = map[i][j];
			}
		}
		height = height * 2;
		map = symMap;
	}
	
	public String toString(){
		
		String s = "MAP\n" + 
		"WIDTH = " + width + "\n" + 
		"HEIGHT = " + height + "\n";
		for(int j=0; j<height; j++){
			for(int i=0; i<width; i++){
				if(map[i][j] == 0)
					s = s += " ";
				else if (map[i][j] == 3)
					s = s + "Y";
				else 
					s = s + "X";
			}
			s = s + "\n";
		}
		
		return s;
	}
	
	//dumb test
	public static void main(String args[]){
		Map m = randMap();
		System.out.println(m.toString());
	}
	
}
