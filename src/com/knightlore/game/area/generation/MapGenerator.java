package com.knightlore.game.area.generation;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

import com.knightlore.game.Team;
import com.knightlore.game.area.Map;
import com.knightlore.game.area.Room;
import com.knightlore.game.area.RoomType;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BreakibleTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.MossBrickTile;
import com.knightlore.game.tile.PathTile;
import com.knightlore.game.tile.PlayerSpawnTile;
import com.knightlore.game.tile.LavaTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.TurretTile;
import com.knightlore.game.tile.UndecidedTile;
import com.knightlore.game.tile.PickupTile;
import com.knightlore.utils.pathfinding.PathFinder;

public class MapGenerator extends ProceduralAreaGenerator {

    private static final int ROOM_RANGE_MIN = 4;
    private static final int ROOM_RANGE_MAX = 8;
    private int maxRooms;
    
    private List<RoomType> roomsToBuild = new LinkedList<>();
    private static final int ROOM_COST_MODIFIER = 5;

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
        // TODO: maybe make this correspond to map size
        maxRooms = ROOM_RANGE_MIN + rand.nextInt(ROOM_RANGE_MAX - ROOM_RANGE_MIN);
        PerlinNoiseGenerator perlinGenerator = new PerlinNoiseGenerator(width, height, seed);
        // Initialize costGrid with perlin noise to make generated paths less optimal
        costGrid = perlinGenerator.createPerlinNoise();
        fillGrid();
        return new Map(grid, seed);
    }

    @Override
    protected void fillGrid() {
        resetGrid();
        determineRoomsToBuild();
        generateRooms();
        generatePaths();
        makeSymY();
        fillUndecidedTiles();
    }

    private void determineRoomsToBuild() {
        // TODO: Ensure a more interesting mix of rooms
        
        // TODO: Expand this if we need other types of
        // of room
        roomsToBuild.add(RoomType.spawn);
        roomsToBuild.add(RoomType.weapon);
        roomsToBuild.add(RoomType.middle);
        roomsToBuild.add(RoomType.middle);
        
        for(int i=1; i < maxRooms; i++) {
            double randInt = rand.nextDouble();
            if(randInt >= 0.66) {
                roomsToBuild.add(RoomType.health);
            }else {
                roomsToBuild.add(RoomType.normal);
            }
        }
    }
    
    private void generateRooms() {
        RoomGenerator roomGenerator = new RoomGenerator();

        for(RoomType rt : roomsToBuild) {
            Room room = roomGenerator.createRoom(rand.nextLong(), rt);
            // TODO: potentially have different setRoomPositions
            if(setRoomPosition(room, rt)) {
                rooms.add(room);
            }
        }
    }

    private boolean setRoomPosition(Room room, RoomType rt) {
        // TODO: modify this for different rooms types
        int width = grid.length;
        int height = grid[0].length;
        switch(rt){
            case spawn :
                return setRoomPosition(room,0,0, width/4, height);
            case weapon :
                return setRoomPosition(room, width/4, height/4, width*3/4 , height*3/4);
            case health:
                return setRoomPosition(room, width/4, height/4, width, height);
            case middle :
                return setRoomPosition(room, width-(room.getWidth()), 0, width+1, height);
            default : return setRoomPosition(room, 0,0, width, height);
        }
    }

    private boolean setRoomPosition(Room room, int minX, int minY ,
                                    int maxX, int maxY) {
        List<Point> candidates = new ArrayList<Point>();
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
        // TODO: give a room a way to give us potential starting points for a path
        // (allowing us to populate rooms with interesting features)
        
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
            if(currentTile == AirTile.getInstance()) {
                continue;
            }
            
            if(currentTile instanceof PlayerSpawnTile) {
                continue;
            }
            
            if(currentTile instanceof PickupTile) {
                continue;
            }
            
            if(currentTile == BreakibleTile.getInstance()) {
                continue;
            }
            
            if(currentTile instanceof TurretTile) {
                continue;
            }
            
            grid[p.x][p.y] = AirTile.getInstance();
        }
    }

    @Override
    protected void fillUndecidedTiles() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] == UndecidedTile.getInstance()) {
                    grid[x][y] = rand.nextDouble() < 0.66D ? new BrickTile() : new MossBrickTile();
                }
                if (grid[x][y] == PathTile.getInstance()) {
                    grid[x][y] = AirTile.getInstance();
                }
            }
        }
    }

    private void makeSymY() {
        
        int width = grid.length;
        int height = grid[0].length;
        
        int numConnectToReflect = rand.nextInt(rooms.size() / 3);
        numConnectToReflect = Math.max(1, numConnectToReflect);
        
        for(int i=0; i < numConnectToReflect; i++) {
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

    public static void main(String args[]) {
        Random r = new Random();
        MapGenerator mg = new MapGenerator();
        Map map = mg.createMap(40, 40, r.nextInt(1000));
        System.out.println(map.toDebugString());
    }
    
}
