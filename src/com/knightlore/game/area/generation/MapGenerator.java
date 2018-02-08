package com.knightlore.game.area.generation;

import com.knightlore.game.area.Map;
import com.knightlore.game.area.Room;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.PathTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.UndecidedTile;
import com.knightlore.render.Environment;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class MapGenerator extends ProceduralGenerator {
    private double[][] perlinNoise;
    private final List<Room> rooms = new LinkedList<>();
    private PerlinVector[][] gradVectors;
    
    public MapGenerator() {
    }

    public Map createMap(int width, int height, Environment env) {
        Random rand = new Random();
        return createMap(width, height, env, rand.nextLong());
    }

    public Map createMap(int width, int height, Environment env, long seed) {
        rand = new Random(seed);
        grid = new Tile[width][height];
        fillGrid();
        return new Map(grid, env, seed);
    }

    @Override
    protected void fillGrid() {
        resetGrid();
        createPerliNoiseForGrid();
        generateRooms();
        generatePaths();
        fillUndecidedTiles();
        makeSymY();
        addWalls(); // TODO delete
    }

    private int width() {
    	return grid.length;
    }
    
    private int height() {
    	if(width() == 0)
    		return 0;
    	return grid[0].length;
    }
    
    private void createPerliNoiseForGrid() {
    	System.out.println("Creating perlin noise");
    	// need a predictable way of getting same
    	// pseudo-random vector for tile "corner"
    	// store some random values into an array
    	// that can be referenced at will
    	
    	// I think there might be a better way to produce
    	// gradient vectors
    	// (ie. permutation, to prevent things averaging out
    	// kinda samey)
    	gradVectors = new PerlinVector[width() + 1][height() + 1];
    	for(int i=0; i < gradVectors.length; i++){
    		for(int j=0; j < gradVectors[0].length; j++){
    			double r1 = rand.nextDouble();
    			double r2 = rand.nextDouble();
    			gradVectors[i][j] = new PerlinVector(r1,r2);
    		}
    	}
    	
    	// populate perlinNoise grid
    	perlinNoise = new double[width()][height()];
    	
    	// perform multiple times
    	// taking a biased average on each increment
    	for(int k=0; k < 5; k++) {
	    	
	    	for(int i=0; i < perlinNoise.length; i++) {
	    		for(int j=0; j< perlinNoise[0].length; j++) {
	    			double x = i + rand.nextDouble();
	    			double y = j + rand.nextDouble();
	    			if(k==0)
	    				perlinNoise[i][j] = perlin(x,y);
	    			else
	    				perlinNoise[i][j] = ( perlinNoise[i][j] + perlin(x,y) ) / 2;
	    		}
	    	}
	    	
    	}
    }

    private double perlin(double x, double y) {
    	
    	double xm = x % 1; // map to coordinates in unit square
    	double ym = y % 1;
    	
    	int x0 = (int) (x - xm); // get coordinates of square points surrounding 
    	int y0 = (int) (y - ym); // x any y
    	int x1 = x0 + 1;
    	int y1 = y0 + 1;
    	
    	//  (x0,y1)------(x1,y1)
    	//      |           |
    	//      |  (mx,ym)  |
    	//      |           |
    	//  (x0,y0)------(x1,y0)
    	
    	// get pseudo-random gradient vectors
    	// should be predictable given x0, y0, x1, y1
    	PerlinVector g1,g2,g3,g4;
    	// 1 (x0,y1) -->
    	g1 = gradVectors[x0][y1];
    	// 2 (x1,y1) -->
    	g2 = gradVectors[x1][y1];
    	// 3 (x0,y0) -->
    	g3 = gradVectors[x0][y0];
    	// 4 (x1,y0) -->
    	g4 = gradVectors[x1][y0];
    	
    	// generate distance vectors
    	PerlinVector d1,d2,d3,d4;
    	// 1 (x0,y1) --> (x,y)
    	d1 = new PerlinVector(xm, ym - 1);
    	// 2 (x1,y1) --> (x,y)
    	d2 = new PerlinVector(xm - 1,ym - 1);
    	// 3 (x0,y0) --> (x,y)
    	d3 = new PerlinVector(xm, ym);
    	// 4 (x1,y0) --> (x,y)
    	d4 = new PerlinVector(xm - 1, ym);
    	
    	// dot product gradient vectors with corresponding
    	// distance vectors, generating the influence values
    	double i1, i2, i3, i4;
    	// 1
    	i1 = PerlinVector.dotProduct(g1, d1);
    	// 2
    	i2 = PerlinVector.dotProduct(g2, d2);
    	// 3
    	i3 = PerlinVector.dotProduct(g3, d3);
    	// 4
    	i4 = PerlinVector.dotProduct(g4, d4);
    	
    	// apply fade function (ease curve) on x and y
    	x = fade(xm);
    	y = fade(ym);
    	
    	// return "interpolation" of influence vectors
    	return ( (i1 + i2)/2 + (i3 + i4)/2 )/2;
    }

    private double fade(double t) {
    	// 6t^5 - 15t^4 + 10t^3
    	return 6 * (t*t*t*t*t) - 15 * (t*t*t*t) + 10 * (t*t*t);
    }
    
    private void generateRooms() {
        RoomGenerator roomGenerator = new RoomGenerator();
        Room room = roomGenerator.createRoom(rand.nextLong());
        while (setRoomPosition(room)) {
            rooms.add(room);
            room = roomGenerator.createRoom(rand.nextLong());
        }

        System.out.println("Number of rooms generated: " + rooms.size());
    }

    private boolean setRoomPosition(Room room) {
        // TODO make something like x += rand and y += rand instead of x++ and y++?
        for (int x = 0; x < grid.length - room.getWidth(); x++) {
            for (int y = 0; y < grid[0].length - room.getHeight(); y++) {
                room.setRoomPosition(new Point(x, y));
                if (canBePlaced(room)) {
                    placeRoom(room);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean canBePlaced(Room room) {
        int leftWallX = room.getPosition().x;
        int rightWallX = leftWallX + room.getWidth();
        int topWallY = room.getPosition().y;
        int bottomWallY = topWallY + room.getHeight();
        return grid[leftWallX][topWallY] == UndecidedTile.getInstance()
            && grid[leftWallX][bottomWallY] == UndecidedTile.getInstance()
            && grid[rightWallX][topWallY] == UndecidedTile.getInstance()
            && grid[rightWallX][topWallY] == UndecidedTile.getInstance();
    }

    private void placeRoom(Room r) {
        int xPos = r.getPosition().x;
        int yPos = r.getPosition().y;
        for (int x = xPos; x < xPos + r.getWidth(); x++) {
            for (int y = yPos; y < yPos + r.getHeight(); y++) {
                grid[x][y] = r.getTile(x - xPos, y - yPos);
            }
        }
    }

    private void generatePaths() {
        // TODO implement
    }

    private boolean addPath(Point start, Point end) {
    	// perform A* search
    	SearchState state = new SearchState(start,end,grid,perlinNoise);
    	if(! (state.isValid(start) && state.isValid(end)) )
    		return false;
    	
    	// set influence of perlin noise with g-weight
    	// (4.5 is my favourite value)
    	// if zero we just get A*
    	SearchState.setGWeight(4.5);
    	
    	ArrayList<SearchState> states = new ArrayList<SearchState>();
    	states.add(state);
    	// loop until return
    	while(true) {
    		//System.out.println(state.getPosition().toString());
    		state = states.get(0);
    		if(state.isGoal()) {
    			// modify grid
    			while(state != null) {
    				int x = state.getPosition().x;
    				int y = state.getPosition().y;
    				grid[x][y] = new PathTile();
    				state = state.getPred();
    			}
    			// then return
    			return true;
    		}else {
    			// add successors to list
    			states.addAll(state.getSuccessors());
    			// remove current state
    			states.remove(0);
    			// order list
    			Collections.sort(states);
    		}
    		
    	}
    	
    }
    
    @Override
    protected void fillUndecidedTiles() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if(grid[x][y] == UndecidedTile.getInstance()) {
                    grid[x][y] = AirTile.getInstance();
                }
                if(grid[x][y].toChar() == 'P') {
                	//grid[x][y] = AirTile.getInstance();
                }
            }
        }
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

    // TODO delete
    
    public static void main(String[] args) {
    	MapGenerator genr = new MapGenerator();
    	Map map = genr.createMap(48 , 32, Environment.LIGHT_OUTDOORS);
    	
    	double min = 1;
    	double max = -1;
    	
    	//double[][] p = genr.perlinNoise;
    	//for(int i=0; i< p.length; i++) {
    	//	System.out.print(i + "::: ");
    	//	for(int j=0; j < p[0].length; j++) {
    	//		if (p[i][j] > max) max = p[i][j];
    	//		if (p[i][j] < min) min = p[i][j];
    	//		System.out.print(j + ": " + p[i][j]);
    	//	}
    	//	System.out.println();
    	//}
    	System.out.println("MIN: " + min);
    	System.out.println("MAX: " + max);
    	
    	System.out.println("--------------");
    	System.out.println(map.toString());
    	
    	
    }
	
    
}
