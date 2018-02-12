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
    private static final int MAX_ROOMS = 5;

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
        // initialize costGrid with perlin noise to make generated paths less
        // optimal
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
    }

    private void generateRooms() {
        RoomGenerator roomGenerator = new RoomGenerator();
        Room room = roomGenerator.createRoom(rand.nextLong());

        while (rooms.size() < MAX_ROOMS && setRoomPosition(room)) {
            rooms.add(room);
            room = roomGenerator.createRoom(rand.nextLong());
        }
    }

    private boolean setRoomPosition(Room room) {
        List<Point> candidates = new ArrayList<>();
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
                // costGrid[x][y] = Double.MAX_VALUE;
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

            while (room.getNumConnections() < Room.MIN_CONNECTIONS) {
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
                    grid[x][y] = new BrickTile();
                }
            }
        }
    }

    private void makeSymY() {
        Room rightmost = rooms.get(0);
        Room secondRightmost = rooms.get(0);
        for (Room room : rooms) {
            if (room.getCentre().getY() > rightmost.getCentre().getY()) {
                rightmost = room;
            }
        }
        for (Room room : rooms) {
            if (!room.equals(rightmost)) {
                if (room.getCentre().getY() > secondRightmost.getCentre().getY()) {
                    secondRightmost = room;
                }
            }
        }

        int width = grid.length;
        int height = grid[0].length;

        PathFinder pathFinder = new PathFinder(costGrid);
        Point start = rightmost.getCentre();
        Point goal = new Point(width - 1, rand.nextInt(height));
        List<Point> path = pathFinder.findPath(start, goal);
        placePath(path);
        start = secondRightmost.getCentre();
        goal = new Point(width - 1, rand.nextInt(height));
        path = pathFinder.findPath(start, goal);
        placePath(path);

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
        Map map = genr.createMap(48, 32, Environment.LIGHT_OUTDOORS);

        System.out.println("--------------");
        System.out.println(map.toString());
        System.out.println("Num rooms: " + genr.rooms.size());

    }
}
