package com.knightlore.game.area.generation;

import com.knightlore.game.area.Map;
import com.knightlore.game.area.Room;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.UndecidedTile;
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

    private void createPerliNoiseForGrid() {
    	// need a predictable way of getting same
    	// pseudo-random vector for tile "corner"
    	// store some random values into an array
    	// that can be referenced at will
    	PerlinVector[][] gradVectors = 
    			new PerlinVector[grid.length + 1][grid[0].length + 1];
    	for(int i=0; i < gradVectors.length; i++){
    		for(int j=0; j < gradVectors[0].length; j++){
    			double r1 = rand.nextDouble();
    			double r2 = rand.nextDouble();
    			gradVectors[i][j] = new PerlinVector(r1,r2);
    		}
    	}
    	
    }

    private double perlin(double x, double y) {
    	
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
                room.setRoomPosition(x, y);
                if (canBePlaced(room)) {
                    placeRoom(room);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean canBePlaced(Room room) {
        int leftWallX = room.getXPosition();
        int rightWallX = room.getXPosition() + room.getWidth();
        int topWallY = room.getYPosition();
        int bottomWallY = room.getYPosition() + room.getHeight();
        return grid[leftWallX][topWallY] == UndecidedTile.getInstance()
            && grid[leftWallX][bottomWallY] == UndecidedTile.getInstance()
            && grid[rightWallX][topWallY] == UndecidedTile.getInstance()
            && grid[rightWallX][topWallY] == UndecidedTile.getInstance();
    }

    private void placeRoom(Room r) {
        int xPos = r.getXPosition();
        int yPos = r.getYPosition();
        for (int x = xPos; x < xPos + r.getWidth(); x++) {
            for (int y = yPos; y < yPos + r.getHeight(); y++) {
                grid[x][y] = r.getTile(x - xPos, y - yPos);
            }
        }
    }

    private void generatePaths() {
        // TODO implement
    }

    @Override
    protected void fillUndecidedTiles() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if(grid[x][y] == UndecidedTile.getInstance()) {
                    grid[x][y] = AirTile.getInstance();
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

}
