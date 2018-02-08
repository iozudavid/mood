package com.knightlore.game.area.generation;

import com.knightlore.game.area.Map;
import com.knightlore.game.area.Room;
import com.knightlore.game.tile.*;
import com.knightlore.render.Environment;

import java.awt.Point;
import java.util.*;

public class MapGenerator extends ProceduralAreaGenerator {
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
        PerlinNoiseGenerator perlinGenerator = new PerlinNoiseGenerator(width, height, seed);
        perlinNoise = perlinGenerator.createPerlinNoise();
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

    private void placePath(List<Point> path) {
        for (Point p: path) {
            grid[p.x][p.y] = new PathTile();
        }
    }

    private List<Point> findPath(Point start, Point goal, double[][] costGrid) {
        if (!isInBounds(start, costGrid)) {
            throw new IndexOutOfBoundsException("Starting point is out of bound of the costGrid");
        }

        if (!isInBounds(goal, costGrid)) {
            throw new IndexOutOfBoundsException("Goal is out of bound of the costGrid");
        }

        Set<Point> checkedPoints = new HashSet<>();
        Queue<SearchNode> nodesQueue = new PriorityQueue<>();
    	nodesQueue.add(new SearchNode(start, goal));
    	while (true) {
    		SearchNode currNode = nodesQueue.poll();
            System.out.println(currNode.getPosition());
    		if (goal.equals(currNode.getPosition())) {
                return extractBestPathTo(currNode);
    		} else {
    		    List<SearchNode> neighbours = getNeighbouringNodes(currNode, goal, costGrid, checkedPoints);
                nodesQueue.addAll(neighbours);
    		}
    	}
    }

    private List<Point> extractBestPathTo(SearchNode node) {
        LinkedList<Point> path = new LinkedList<>();
        path.addFirst(node.getPosition());
        while (node.getPredecessor().isPresent()) {
            node = node.getPredecessor().get();
            path.addFirst(node.getPosition());
        }

        return path;
    }

    private List<SearchNode> getNeighbouringNodes(SearchNode node, Point goal,
                                                  double[][] costGrid, Set<Point> checkedPoints){
        List<SearchNode> neighbours = new LinkedList<>();
        Point up = new Point(node.getPosition().x, node.getPosition().y - 1);
        if (isInBounds(up, costGrid) && !checkedPoints.contains(up)) {
            checkedPoints.add(up);
            neighbours.add(new SearchNode(up, goal, costGrid[up.x][up.y], node));
        }

        Point left = new Point(node.getPosition().x - 1, node.getPosition().y);
        if (isInBounds(left, costGrid) && !checkedPoints.contains(left)) {
            checkedPoints.add(left);
            neighbours.add(new SearchNode(left, goal, costGrid[left.x][left.y], node));
        }

        Point down = new Point(node.getPosition().x, node.getPosition().y + 1);
        if (isInBounds(down, costGrid) && !checkedPoints.contains(down)) {
            checkedPoints.add(down);
            neighbours.add(new SearchNode(down, goal, costGrid[down.x][down.y], node));
        }

        Point right = new Point(node.getPosition().x + 1, node.getPosition().y);
        if (isInBounds(right, costGrid) && !checkedPoints.contains(right)) {
            checkedPoints.add(right);
            neighbours.add(new SearchNode(right, goal, costGrid[right.x][right.y], node));
        }

        return neighbours;
    }

    private boolean isInBounds(Point p, double[][] grid) {
        return p.x >= 0 && p.x < grid.length && p.y >= 0 && p.y < grid[0].length;
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
