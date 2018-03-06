package com.knightlore.game.area.generation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import com.knightlore.game.Team;
import com.knightlore.game.area.Map;
import com.knightlore.game.area.Room;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.MossBrickTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.UndecidedTile;
import com.knightlore.utils.pathfinding.PathFinder;

public class MapGenerator extends ProceduralAreaGenerator {
    private static final int MAX_ROOMS = 5;

    private final List<Room> rooms = new LinkedList<>();
    private double[][] costGrid;

    public MapGenerator() {
    }

    public Map createMap(int width, int height) {
        Random rand = new Random();
        return createMap(width, height, rand.nextLong());
    }

    public Map createMap(int width, int height, long seed) {
        System.out.println("Creating map with seed: " + seed);
        rand = new Random(seed);
        grid = new Tile[width][height];
        PerlinNoiseGenerator perlinGenerator = new PerlinNoiseGenerator(width, height, seed);
        // Initialize costGrid with perlin noise to make generated paths less optimal
        costGrid = perlinGenerator.createPerlinNoise();
        fillGrid();
        return new Map(grid, seed);
    }

    @Override
    protected void fillGrid() {
        resetGrid();
        generateRooms();
        generatePaths();
        fillUndecidedTiles();
        makeSymY();
    }

    private void generateRooms() {
        RoomGenerator roomGenerator = new RoomGenerator();
        // place spawn room first
        Room room = roomGenerator.createRoom(rand.nextLong(), Team.blue);
        setRoomPosition(room , grid.length/8, grid[0].length/8);
        rooms.add(room);
        
        room = roomGenerator.createRoom(rand.nextLong(),Team.none);
        
        while (rooms.size() < MAX_ROOMS && setRoomPosition(room)) {
            rooms.add(room);
            room = roomGenerator.createRoom(rand.nextLong(),Team.none);
        }
    }
    
    private boolean setRoomPosition(Room room) {
        List<Point> candidates = new ArrayList<Point>();
        for (int x = 0; x < grid.length - room.getWidth(); x++) {
            for (int y = 0; y < grid[0].length - room.getHeight(); y++) {
                room.setRoomPosition(new Point(x, y));
                if (canBePlaced(room)) {
                    candidates.add(new Point(x, y));
                }
            }
        }

        if (candidates.isEmpty()) {
            return false;
        } else {
            int index = rand.nextInt(candidates.size());
            room.setRoomPosition(candidates.get(index));
            placeRoom(room);
            return true;
        }
    }
    
    private boolean setRoomPosition(Room room, int maxX, int maxY) {
        List<Point> candidates = new ArrayList<Point>();
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                room.setRoomPosition(new Point(x, y));
                if (canBePlaced(room)) {
                    candidates.add(new Point(x, y));
                }
            }
        }

        if (candidates.isEmpty()) {
            return false;
        } else {
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
        for(int i=leftWallX; i < rightWallX; i++) {
            for(int j=topWallY; j < bottomWallY; j++) {
                //if(grid[i][j] != UndecidedTile.getInstance()) {
                if (grid[i][j] != UndecidedTile.getInstance()) {
                    return false;
                }
            }
        }

        return true;
    }

    private void placeRoom(Room r) {
        int xPos = r.getPosition().x;
        int yPos = r.getPosition().y;
        for (int x = xPos; x < xPos + r.getWidth(); x++) {
            for (int y = yPos; y < yPos + r.getHeight(); y++) {
                // place appropriate room tile
                grid[x][y] = r.getTile(x - xPos, y - yPos);
                // modify cost grid
                //costGrid[x][y] = Double.MAX_VALUE;
                costGrid[x][y] = costGrid[x][y] * 5; // arbitrary value of 5
            }
        }
    }

    private void generatePaths() {
        generateMinimumSpanningTree();
        satisfyMinimumRoomConnections();

        // place paths
        PathFinder pathFinder = new PathFinder(costGrid);
        for (Room source : rooms) {
            for (Room target : source.getConnections()) {
                List<Point> path = pathFinder.findPath(source.getCentre(), target.getCentre());
                placePath(path);
            }
        }
    }

    private void generateMinimumSpanningTree() {
        Queue<Room> toAdd = new LinkedList<>(rooms);
        Queue<RoomConnection> possibleConnections = new PriorityQueue<>();
        // start with the first room as lastAdded
        Room lastAdded = toAdd.poll();
        while (!toAdd.isEmpty()) {
            for (Room target : toAdd) {
                possibleConnections.add(new RoomConnection(lastAdded, target));
            }

            RoomConnection connection = possibleConnections.poll();
            // don't target lastAdded connection to avoid loops
            while (connection.getTarget() == lastAdded || !Room.addConnection(connection)) {
                if (possibleConnections.isEmpty()) {
                    // consider loops if it's out of other viable options
                    for (Room target : rooms) {
                        possibleConnections.add(new RoomConnection(lastAdded, target));
                    }
                }

                connection = possibleConnections.poll();
            }
            toAdd.remove(connection.getTarget());
            lastAdded = connection.getTarget();
        }
    }

    private void satisfyMinimumRoomConnections() {
        Queue<RoomConnection> possibleConnections = new PriorityQueue<>();
        for (Room room : rooms) {
            for (Room target : rooms) {
                possibleConnections.add(new RoomConnection(room, target));
            }

            while (room.getNumConnections() < room.getMinConnections()) {
                RoomConnection connection = possibleConnections.poll();
                Room.addConnection(connection);
            }

            possibleConnections.clear();
        }
    }

    private void placePath(List<Point> path) {
        for (Point p : path) {
            grid[p.x][p.y] = AirTile.getInstance();
        }
    }

    @Override
    protected void fillUndecidedTiles() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] == UndecidedTile.getInstance()) {
                    grid[x][y] = Math.random() < 0.75 ? new BrickTile() : new MossBrickTile();
                }
            }
        }
    }

    private void makeSymY() {
        Room rightmost = rooms.get(0);
        Room secondRightmost = rooms.get(0);
        for (Room room : rooms) {
            if (room.getCentre().getX() > rightmost.getCentre().getX()) {
                rightmost = room;
            }
        }

        for (Room room : rooms) {
            if (!room.equals(rightmost) && room.getCentre().getX() > secondRightmost.getCentre().getX()) {
                secondRightmost = room;
            }
        }

        int width = grid.length;
        int height = grid[0].length;

        PathFinder pathFinder = new PathFinder(costGrid);
        
        Point start = rightmost.getCentre();
        int centreY = start.y;
        Point goal = new Point(width - 1, centreY - 2 + rand.nextInt(3));
        // have to set this manually so pathfinder doesn't complain
        grid[goal.x][goal.y] = AirTile.getInstance();
        // pathfinder can handle this goal
        goal = new Point(goal.x - 1, goal.y);
        List<Point> path = pathFinder.findPath(start, goal);
        placePath(path);
        
        start = secondRightmost.getCentre();
        centreY = start.y;
        goal = new Point(width - 1, centreY - 2 + rand.nextInt(3));
        // have to set this manually so pathfinder doesn't complain
        grid[goal.x][goal.y] = AirTile.getInstance();
        // pathfinder can handle this goal
        goal = new Point(goal.x - 1, goal.y);
        path = pathFinder.findPath(start, goal);
        placePath(path);

        // now flip
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
    public static void main(String[] args) {
        MapGenerator genr = new MapGenerator();
        Map map = genr.createMap(48, 32);

        System.out.println("--------------");
        System.out.println(map.toString());
        System.out.println("Num rooms: " + genr.rooms.size());
    }
}
