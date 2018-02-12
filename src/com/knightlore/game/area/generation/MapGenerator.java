package com.knightlore.game.area.generation;

import com.knightlore.game.area.Map;
import com.knightlore.game.area.Room;
import com.knightlore.game.tile.*;
import com.knightlore.render.Environment;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.pathfinding.PathFinder;

import java.awt.Point;
import java.util.*;

public class MapGenerator extends ProceduralAreaGenerator {
    private final List<Room> rooms = new LinkedList<>();
    private double[][] costGrid;
    
    public MapGenerator() {
    }

    public Map createMap(int width, int height, Environment env) {
        Random rand = new Random();
        return createMap(width, height, env, rand.nextLong());
    }

    public Map createMap(int width, int height, Environment env, long seed) {
        rand = new Random(seed);
        grid = new Tile[width][height];
        PerlinNoiseGenerator perlinGenerator = new PerlinNoiseGenerator(width, height, seed);
        // initialize costGrid with perlin noise to make generated paths less optimal
        costGrid = perlinGenerator.createPerlinNoise();
        fillGrid();
        return new Map(grid, env, seed);
    }

    @Override
    protected void fillGrid() {
        resetGrid();
        generateRooms();
        generatePaths();
        fillUndecidedTiles();
        makeSymY();
        addWalls(); // TODO delete
    }
    
    private void generateRooms() {
        RoomGenerator roomGenerator = new RoomGenerator();
        Room room = roomGenerator.createRoom(rand.nextLong());

        int MAX_ROOM_NUM = 5;
        
        while (setRoomPosition(room) && rooms.size() < MAX_ROOM_NUM) {
            rooms.add(room);
            room = roomGenerator.createRoom(rand.nextLong());
        }
        System.out.println("Number of rooms generated: " + rooms.size());
    }

    private boolean setRoomPosition(Room room) {
        // TODO make something like x += rand and y += rand instead of x++ and y++?
    	
    	ArrayList<Point> candidates = new ArrayList<Point>();
    	
    	for (int x = 0; x < grid.length - room.getWidth(); x++) {
            for (int y = 0; y < grid[0].length - room.getHeight(); y++) {
                room.setRoomPosition(new Point(x, y));
                if (canBePlaced(room)) {
                	candidates.add(new Point(x,y));
                    //placeRoom(room);
                    //return true;
                }
            }
        }
		
    	if(candidates.size() == 0) {
    		return false;
    	}else {
    		int index = rand.nextInt(candidates.size());
    		room.setRoomPosition(candidates.get(index));
    		placeRoom(room);
    		return true;
    	}
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
                //costGrid[x][y] = Double.MAX_VALUE;
            }
        }
    }

    private void generatePaths() {
        // TODO implement
    	
    	// We'll do Prim's
    	ArrayList<Room> toAdd = new ArrayList<Room>();
    	ArrayList<RoomConnection> possibleConnections = new ArrayList<RoomConnection>();

    	toAdd.addAll(rooms);
    	for(int i=0; i< rooms.size(); i++) {
    		toAdd.add(rooms.get(i));
    	}
    	
    	Room lastAdded = rooms.get(0);
    	
    	while(toAdd.size() > 0) {
    		//remove lastAdded to prevent cycles
    		toAdd.remove(lastAdded);
    		for(int i=0; i<toAdd.size(); i++) {
    			Room target = toAdd.get(i);
    			possibleConnections.add(new RoomConnection(lastAdded, target));
    		}
    		Collections.sort(possibleConnections);
    		//attempt to add minimal path
    		//while ensuring a room does not
    		//exceed its maximum connections
    		boolean success = false;
    		while(!success) {
    			RoomConnection connection = possibleConnections.get(0);
    			if(Room.addConnection(connection)) {
    				success = true;
    				toAdd.remove(connection.target);
    				lastAdded = connection.target;
    			}else {
    				possibleConnections.remove(0);
    				if(possibleConnections.isEmpty()) {
    					// out of options
    					// now consider loops
    					for(int i=0; i<rooms.size(); i++) {
    						Room target = rooms.get(i);
    						possibleConnections.add(new RoomConnection(lastAdded,target));
    					}
    					// sort them so our overhead isn't that bad
    					Collections.sort(possibleConnections);
    				}
    			}
    		}
    		// remove possible connections
    		// those whose target equals last added
    		for(int i=0; i<possibleConnections.size(); i++) {
    			RoomConnection connection = possibleConnections.get(i);
    			if(connection.target.equals(lastAdded)) {
    				possibleConnections.remove(i);
    			}
    		}
    		
    	}
    	
    	//minimal-ish spanning tree has been generated
    	//all rooms should have a number of connections below
    	//the allowed maximum
    	//now we need to ensure they meet their minimum
    	for(int i=0; i<rooms.size(); i++) {
    		//clear all possibleConnections
        	possibleConnections.clear();
        	
    		Room room = rooms.get(i);
    		System.out.println("ROOM: " + room.getPosition());
    		int numConnections = room.getNumConnections();
    		System.out.println("CONNECTIONS: " + numConnections);
    		if(numConnections < Room.MIN_CONNECTIONS) {
    			// consider all candidate connections
    			for(int j=0; j<rooms.size(); j++) {
    				Room target = rooms.get(j);
    				possibleConnections.add(new RoomConnection(room,target));
    			}
    			while(numConnections <Room.MIN_CONNECTIONS) {
    				//DEBUG
    				System.out.println(numConnections);
	    			boolean success = false;
	    			while(!success) {
	    				RoomConnection connection = possibleConnections.get(0);
	    				if(Room.addConnection(connection)) {
	    					System.out.println(connection.toString());
	    					numConnections++;
	    					success = true;
	    				}
	    				possibleConnections.remove(0);
	    			}
    			}
    		}
    	}
    	
    	// do some debugging here
    	for(int i=0; i<rooms.size(); i++) {
    		Room room = rooms.get(i);
    		System.out.println(room.getPosition());
    		List<Room> neighbours = room.getConnections();
    		for(int j=0; j<neighbours.size(); j++) {
    			room = neighbours.get(j);
    			System.out.println("--" + room.getPosition());
    		}
    	}
    	
    	// now actually place paths...
    	PathFinder pathFinder = new PathFinder(costGrid);
    	
    	// generate all necessary roomConnections
    	// (excluding reflexive connections)
    	for(int i=0; i<rooms.size();i++) {
    		Room source = rooms.get(i);
    		for(int j=i; j<rooms.size();j++) {
    			Room target = rooms.get(j);
    			if (source.getConnections().contains(target)) {
    				List<Point> path = pathFinder.findPath(source.getCentre(), target.getCentre());
    				placePath(path);
    			}
    		}
    	}
    	
    }
    
    private void placePath(List<Point> path) {
        for (Point p: path) {
        	if (!(grid[p.x][p.y] == AirTile.getInstance()))
        		grid[p.x][p.y] = AirTile.getInstance();
        }
    }
    
    @Override
    protected void fillUndecidedTiles() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if(grid[x][y] == UndecidedTile.getInstance()) {
                    grid[x][y] = new BrickTile();
                }
            }
        }
    }

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
    	
    	System.out.println("--------------");
    	System.out.println(map.toString());
    	System.out.println("Num rooms: " + genr.rooms.size());
    	
    }

	public Vector2D getDemoSpawnPos() {
		Point p = rooms.get(0).getCentre();
		return new Vector2D(p.x,p.y);
	}
	
    
}
