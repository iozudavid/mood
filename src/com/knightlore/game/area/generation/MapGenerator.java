package com.knightlore.game.area.generation;

import com.knightlore.game.area.Map;
import com.knightlore.game.area.Room;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.render.Environment;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class MapGenerator extends ProceduralGenerator {
    private double[][] perlinNoise;
    private final List<Room> rooms = new LinkedList<>();

    public MapGenerator() {
    }

    public Map createMap(int width, int height, Environment env) {
        Random rand = new Random();
        long seed = rand.nextLong();
        return createMap(width, height, env, seed);
    }

    public Map createMap(int width, int height, Environment env, long seed) {
        grid = new Tile[width][height];
        fillGrid(seed);
        return new Map(grid, env, seed);
    }

    @Override
    protected void fillGrid(long seed) {
        resetGrid();
        createPerliNoiseForGrid(seed);
        generateRooms(seed);
        placeRooms(seed);
        generatePaths(seed);
        makeSymY();
        addWalls(); // TODO delete
    }

    private void createPerliNoiseForGrid(long seed) {
        // TODO implement
    	// need a predictable way of getting same
    	// pseudo-random vector for tile "corner"

    }

    private double perlin(double x, double y){
    	
    	double xm = x % 1; // map to coordinates in unit square
    	double ym = y % 1;
    	
    	double x0 = x - xm; // get coordinates of square points surrounding 
    	double y0 = y - ym; // x any y
    	double x1 = x0 + 1;
    	double y1 = y0 + 1;
    	
    	//  (x0,y1)------(x1,y1)
    	//      |           |
    	//      |  (mx,ym)  |
    	//      |           |
    	//  (x0,y0)------(x1,y0)
    	
    	// get pseudo-random gradient vectors
    	// should be predictable given x0, y0, x1, y1
    	// 1
    	// 2
    	// 3
    	// 4
    	
    	// generate distance vectors
    	Vector<Double> d1,d2,d3,d4;
    	// 1 (x0,y1) --> (x,y)
    	d1 = new Vector<Double>(2);
    	d1.add(0, x);
    	d1.add(1, y - 1);
    	// 2 (x1,y1) --> (x,y)
    	d2 = new Vector<Double>(2);
    	d2.add(0, x - 1);
    	d2.add(1, y - 1);
    	// 3 (x0,y0) --> (x,y)
    	d3 = new Vector<Double>(2);
    	d3.add(0, x);
    	d3.add(0, y);
    	// 4 (x1,y0) --> (x,y)
    	d4 = new Vector<Double>(2);
    	d4.add(0, x - 1);
    	d4.add(0, y);
    	
    	// dot product gradient vectors with corresponding
    	// distance vectors, generating the influence vectors
    	// 1
    	// 2
    	// 3
    	// 4
    	
    	// apply fade function (ease curve)
    	
    	// "interpolate" influence vectors
    	
    	return 0.0;
    }
    
    private void generateRooms(long seed) {
        // TODO implement
    }

    private void placeRooms(long seed) {
        // TODO implement
    }

    private void generatePaths(long seed) {
        // TODO implement
    }

    // reflection in y-axis
    private void makeSymY() {
        int width = grid.length;
        int height = grid[0].length;
        Tile[][] symMap = new Tile[width * 2][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Tile reflectTile = grid[i][j].reflectTileY();
                symMap[i][j] = grid[i][j];
                symMap[(width * 2 - 1) - i][j] = reflectTile;
            }
        }

        grid = symMap;
    }

    // TODO delete
    private void addWalls() {
        int width = grid.length;
        int height = grid[0].length;
        for (int i = 0; i < width; i++) {
            grid[i][0] = new BrickTile();
            grid[i][height - 1] = new BrickTile();
        }

        for (int j = 0; j < height; j++) {
            grid[0][j] = new BrickTile();
            grid[width - 1][j] = new BrickTile();
        }
    }

}
