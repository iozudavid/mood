package com.knightlore.game.area.generation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import com.knightlore.game.area.Map;
import com.knightlore.game.area.Room;
import com.knightlore.game.area.RoomType;
import com.knightlore.game.tile.*;
import com.knightlore.utils.pathfinding.PathFinder;

public class MapGenerator extends ProceduralAreaGenerator {

    private static final int ROOM_COST_MODIFIER = 5;
    private static final int DOUBLE_PATH_COST_MODIFIER = 3;
    private static final int MIN_AREA_PER_ROOM = 100;

    private final List<RoomType> roomsToBuild = new LinkedList<>();

    private final List<Room> rooms = new LinkedList<>();
    private double[][] costGrid;

    private int maxRooms;
    private boolean symmetrical = true;
    private MapType mapType;

    public MapGenerator() {
    }

    public static void main(String[] arg) {
        MapGenerator mg = new MapGenerator();
        Map map = mg.createMap(40, 40, MapType.TDM, 40L);
        System.out.println(map.toDebugString());
    }

    /**
     * Returns a procedurally-generated map with the specified
     * width, height and map type. The seed is used to ensure
     * this process is deterministic and that the map will be
     * identical on server and clients alike
     *
     * @param width
     * @param height
     * @param mt
     * @param seed
     * @return
     * @author Thomas, Kacper
     */
    public Map createMap(int width, int height, MapType mt, long seed) {
        System.out.println("Creating " + mt + " map with seed: " + seed);
        mapType = mt;
        determineSymmetrical();
        rand = new Random(seed);
        if (symmetrical) {
            width = width / 2;
        }
        grid = new Tile[width][height];

        int mapArea = width * height;
        maxRooms = mapArea / MIN_AREA_PER_ROOM;

        PerlinNoiseGenerator perlinGenerator = new PerlinNoiseGenerator(width, height, seed);
        // Initialize costGrid with perlin noise to make generated paths less optimal
        costGrid = perlinGenerator.createPerlinNoise();
        fillGrid();
        return new Map(grid, seed);
    }
    
    private void determineSymmetrical() {
        switch (mapType) {
            case FFA:
                symmetrical = false;
                break;
            case TDM:
                symmetrical = true;
                break;
            case LAVA_SUBMAP:
                symmetrical = false;
                break;
        }
    }
    
    protected void fillGrid() {
        resetGrid();
        determineRoomsToBuild();
        generateRooms();
        generatePaths();
        if (symmetrical) {
            makeSymY();
        }
        fillUndecidedTiles();
    }
    
    private void determineRoomsToBuild() {

        if (mapType == MapType.LAVA_SUBMAP) {
            roomsToBuild.add(RoomType.LAVA_PLATFORM);
            roomsToBuild.add(RoomType.LAVA_PLATFORM);
            roomsToBuild.add(RoomType.LAVA_PLATFORM);
            return;
        }

        if (mapType == MapType.FFA) {
            roomsToBuild.add(RoomType.NORMAL);
            roomsToBuild.add(RoomType.NORMAL);
        } else {
            roomsToBuild.add(RoomType.SPAWN);
            roomsToBuild.add(RoomType.SPAWN);
        }

        if (mapType == MapType.TRAILER) {
            return;
        }

        // TODO: A switch statement
        if (symmetrical) {
            roomsToBuild.add(RoomType.MIDDLE);
            roomsToBuild.add(RoomType.MIDDLE);
        }

        if (grid.length > 20 && grid[0].length > 20) {
            roomsToBuild.add(RoomType.BIG_LAVA_ROOM);
        }

        while (roomsToBuild.size() <= maxRooms) {
            double randInt = rand.nextDouble();
            if (randInt >= 0.66) {
                roomsToBuild.add(RoomType.WEAPON);
            } else {
                roomsToBuild.add(RoomType.NORMAL);
            }
        }

    }

    private void generateRooms() {
        RoomGenerator roomGenerator = new RoomGenerator();

        for (RoomType rt : roomsToBuild) {
            Room room = roomGenerator.createRoom(rand.nextLong(), rt);
            if (setRoomPosition(room, rt)) {
                rooms.add(room);
            }
        }
    }

    private boolean setRoomPosition(Room room, RoomType rt) {
        int width = grid.length;
        int height = grid[0].length;
        switch (rt) {
            case SPAWN:
                int maxX = Math.max(width / 4, room.getWidth() + 1);
                return setRoomPosition(room, 0, 0, maxX, height);
            case WEAPON:
                return setRoomPosition(room, width / 4, height / 4, width * 3 / 4, height * 3 / 4);
            case MIDDLE:
                return setRoomPosition(room, width - (room.getWidth()), 0, width + 1, height);
            case LAVA_PLATFORM:
                return setRoomPosition(room, 2, 2, width - 3, height - 3);
            default:
                return setRoomPosition(room, 0, 0, width, height);
        }
    }

    private boolean setRoomPosition(Room room, int minX, int minY, int maxX, int maxY) {
        List<Point> candidates = new ArrayList<>();
        for (int x = minX; x < maxX - room.getWidth(); x++) {
            for (int y = minY; y < maxY - room.getHeight(); y++) {
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

        for (int x = leftWallX; x < rightWallX; x++) {
            for (int y = topWallY; y < bottomWallY; y++) {
                if (grid[x][y] != UndecidedTile.getInstance()) {
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
                grid[x][y] = r.getTile(x - xPos, y - yPos);
                costGrid[x][y] = costGrid[x][y] * ROOM_COST_MODIFIER;
            }
        }
    }

    private void generatePaths() {
        generateMinimumSpanningTree();
        satisfyMinimumRoomConnections();

        // place paths
        PathFinder pathFinder = new PathFinder(costGrid);
        pathFinder.setIsForMap(true);
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
            Tile currentTile = grid[p.x][p.y];
            if (currentTile.overiddenByPath()) {
                grid[p.x][p.y] = AirTile.getInstance();
                costGrid[p.x][p.y] = costGrid[p.x][p.y] * DOUBLE_PATH_COST_MODIFIER;
            }

        }
    }

    protected void fillUndecidedTiles() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] instanceof UndecidedTile) {
                    if (mapType == MapType.LAVA_SUBMAP) {
                        grid[x][y] = new LavaTile();
                    } else {
                        grid[x][y] = rand.nextDouble() < 0.66D ? new BrickTile() : new MossBrickTile();
                    }
                }
                if (grid[x][y] == PathTile.getInstance()) {
                    grid[x][y] = AirTile.getInstance();
                }
            }
        }
    }
    
    private void connectToY() {
        int width = grid.length;

        int numConnectToReflect = rand.nextInt(Math.max(1, rooms.size() / 3));
        numConnectToReflect = Math.max(1, numConnectToReflect);
        for (int i = 0; i < numConnectToReflect; i++) {
            Room rightmost = rooms.get(0);
            for (Room room : rooms) {
                if (room.getCentre().getX() > rightmost.getCentre().getX()) {
                    rightmost = room;
                }
            }

            // now that rightmost has been found, remove it
            rooms.remove(rightmost);
            // set up pathFinder
            PathFinder pathFinder = new PathFinder(costGrid);
            pathFinder.setIsForMap(true);

            Point start = rightmost.getCentre();
            int centreY = start.y;
            Point goal = new Point(width - 1, centreY - 2 + rand.nextInt(3));
            // have to set this manually so pathfinder doesn't complain
            grid[goal.x][goal.y] = PathTile.getInstance();
            // pathfinder can handle this goal
            goal = new Point(goal.x - 1, goal.y);
            List<Point> path = pathFinder.findPath(start, goal);
            placePath(path);
        }
    }
    
    private void makeSymY() {
        connectToY();
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
    
}
