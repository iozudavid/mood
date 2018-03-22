package com.knightlore.game.area.generation;

import com.knightlore.game.Team;
import com.knightlore.game.area.Map;
import com.knightlore.game.area.Room;
import com.knightlore.game.area.RoomType;
import com.knightlore.game.entity.pickup.PickupType;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.LavaTile;
import com.knightlore.game.tile.PlayerSpawnTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.UndecidedTile;
import com.knightlore.utils.Vector2D;
import com.knightlore.game.tile.PickupTile;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomGenerator extends ProceduralAreaGenerator {
    private static final int MIN_SIZE = 7;
    private static final int MAX_SIZE = 15;

    private static final int DEFAULT_MIN_CONNECTIONS = 2;
    private static final int DEFAULT_MAX_CONNECTIONS = 6;
    private static final int SPAWN_ROOM_MIN_CONNECTIONS = 1;
    private static final int SPAWN_ROOM_MAX_CONNECTIONS = 2;

    private static final int MAX_DOMINANT_INTERNAL_WALLS = 4;
    private static final int MAX_SUBDOMINANT_INTERNAL_WALLS = 1;
    private static final double INTERNAL_WALL_PROBABILITY = 0.3;

    private long seed;
    private RoomType roomType = RoomType.NORMAL;
    
    /**
     * Creates a room using the given seed, and according
     * to the provided room type.
     * @param seed
     * @param rt
     * @return a room
     * @author Thomas, Kacper
     */
    public Room createRoom(long seed, RoomType rt) {
        this.seed = seed;
        roomType = rt;
        rand = new Random(seed);
        determineRoomSize();
        resetGrid();
        fillGrid();
        if (rt == RoomType.SPAWN) {
            return new Room(grid, SPAWN_ROOM_MIN_CONNECTIONS, SPAWN_ROOM_MAX_CONNECTIONS);
        }
        return new Room(grid, DEFAULT_MIN_CONNECTIONS, DEFAULT_MAX_CONNECTIONS);
    }

    private void determineRoomSize() {
        int width, height;

        switch (roomType) {
            case SPAWN:
                width = MIN_SIZE;
                height = width;
                break;
            case BIG_LAVA_ROOM:
                width = getGaussianNum(MAX_SIZE, MAX_SIZE * 2);
                height = getGaussianNum(MAX_SIZE - 3, MAX_SIZE);
                break;
            case LAVA_PLATFORM:
                width = 2;
                height = width;
                break;
            default:
                width = getGaussianNum(MIN_SIZE, MAX_SIZE);
                height = getGaussianNum(MIN_SIZE, MAX_SIZE);
        }
        grid = new Tile[width][height];
    }

    @Override
    protected void fillGrid() {
        int width = grid.length;
        int height = grid[0].length;
        int midx = width / 2;
        int midy = height / 2;
        switch (roomType) {
            case SPAWN:
                for (int i = midx - 1; i <= midx + 1; i++) {
                    grid[i][midy - 1] = new PlayerSpawnTile(Team.BLUE, Vector2D.DOWN);
                }
                grid[midx - 1][midy] = new PlayerSpawnTile(Team.BLUE, Vector2D.LEFT);
                grid[midx][midy] = new PlayerSpawnTile(Team.BLUE);
                grid[midx + 1][midy] = new PlayerSpawnTile(Team.BLUE, Vector2D.RIGHT);
                for (int i = midx - 1; i <= midx + 1; i++) {
                    grid[i][midy + 1] = new PlayerSpawnTile(Team.BLUE, Vector2D.UP);
                }
                fillUndecidedTiles();
                break;
            case WEAPON:
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        grid[i][j] = new LavaTile();
                    }
                }
                grid[midx][midy] = new PickupTile(PickupType.SHOTGUN);
                fillUndecidedTiles();
                break;
            case MIDDLE:
                if (rand.nextDouble() > 0.5) {
                    roomType = RoomType.WEAPON;
                    fillGrid();
                } else {
                    roomType = RoomType.NORMAL;
                    fillGrid();
                }
                removeRightWall();
                fillUndecidedTiles();
                return; // does not use addWalls
            case BIG_LAVA_ROOM:
                MapGenerator mg = new MapGenerator();
                Map subMap = mg.createMap(width, height, MapType.LAVA_SUBMAP, seed);
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        // HAVE I GOT TO USE .copy()
                        grid[i][j] = subMap.getTile(i, j);
                    }
                }
                fillUndecidedTiles();
                break;
            case LAVA_PLATFORM:
                if (rand.nextDouble() > 0.2) {
                    grid[midx][midy] = new PickupTile(randomPickupType());
                }
                fillUndecidedTiles();
                return; // does not use addWalls
            case NORMAL:
                addInternalWalls();
                addRandomPickup();
                fillUndecidedTiles();
        }
        addWalls();
    }

    private void addInternalWalls() {

        int width = grid.length;
        int height = grid[0].length;

        int numHorizontalInternalWalls = 0;
        int numVerticalInternalWalls = 0;

        int maxHorizontalInternalWalls = MAX_SUBDOMINANT_INTERNAL_WALLS;
        int maxVerticalInternalWalls = MAX_SUBDOMINANT_INTERNAL_WALLS;

        if (width > height) {
            maxVerticalInternalWalls = MAX_DOMINANT_INTERNAL_WALLS;
        } else {
            maxHorizontalInternalWalls = MAX_DOMINANT_INTERNAL_WALLS;
        }

        for (int j = 2; j < height - 3; j++) {
            if (numHorizontalInternalWalls == maxHorizontalInternalWalls) {
                break;
            }
            ;
            if (rand.nextDouble() <= INTERNAL_WALL_PROBABILITY) {
                addHorizontalInternalWall(j);
                numHorizontalInternalWalls++;
                j++; // skip next iteration
            }
        }

        for (int i = 2; i < width - 3; i++) {
            if (numVerticalInternalWalls == maxVerticalInternalWalls) {
                break;
            }
            ;
            if (rand.nextDouble() <= INTERNAL_WALL_PROBABILITY) {
                addVerticalInternalWall(i);
                numVerticalInternalWalls++;
                i++; // skip next iteration
            }
        }
    }

    private void addHorizontalInternalWall(int y) {
        int width = grid.length;
        int distanceIntoRoom = 2 + rand.nextInt(width / 4);
        int start = distanceIntoRoom;
        int end = (width - 1) - distanceIntoRoom;
        // place straight wall
        for (int i = start; i < end; i++) {
            grid[i][y] = new BrickTile();
            grid[i][y].setPathable(false);
        }
    }

    private void addVerticalInternalWall(int x) {
        int height = grid[0].length;
        int distanceIntoRoom = 2 + rand.nextInt(height / 4);
        int start = distanceIntoRoom;
        int end = (height - 1) - distanceIntoRoom;
        // place straight wall
        for (int j = start; j < end; j++) {
            grid[x][j] = new BrickTile();
            grid[x][j].setPathable(false);
        }
    }

    private void addWalls() {
        int width = grid.length;
        int height = grid[0].length;
        for (int i = 0; i < width; i++) {
            grid[i][0] = BrickTile.getInstance();
            grid[i][height - 1] = BrickTile.getInstance();
        }

        for (int j = 0; j < height; j++) {
            grid[0][j] = BrickTile.getInstance();
            grid[width - 1][j] = BrickTile.getInstance();
        }
    }

    private int neighbouringAirTiles(int x, int y) {
        int numOfAir = 0;
        int width = grid.length;
        int height = grid[0].length;
        if (x <= 2) {
            return 0;
        }
        if (y <= 2) {
            return 0;
        }
        if (x >= width - 2) {
            return 0;
        }
        if (y >= height - 2) {
            return 0;
        }

        for (int i = x - 1; i < x + 2; i++) {
            for (int j = y - 1; j < y + 2; j++) {
                if (i == x && j == y) {
                    continue;
                }
                if (grid[i][j] instanceof AirTile) {
                    numOfAir++;
                }
            }
        }

        return numOfAir;
    }

    private PickupType randomPickupType() {
        int randInt = rand.nextInt(2);
        if (randInt == 0) {
            return PickupType.SHOTGUN;
        } else {
            return PickupType.HEALTH;
        }
    }

    private List<Point> possiblePickupLocations() {
        List<Point> candidateLocations = new ArrayList<Point>();
        int minAirTiles = 8;
        for (int i = 0; i < grid.length - 1; i++) {
            for (int j = 0; j < grid[0].length - 1; j++) {
                if (neighbouringAirTiles(i, j) >= minAirTiles) {
                    candidateLocations.add(new Point(i, j));
                }
            }
        }
        return candidateLocations;
    }

    private void addRandomPickup() {
        List<Point> candidateLocations = possiblePickupLocations();
        if (candidateLocations.isEmpty()) {
            return;
        }

        int index = rand.nextInt(candidateLocations.size());
        Point placeLocation = candidateLocations.get(index);

        PickupTile pTile = new PickupTile(randomPickupType());

        grid[placeLocation.x][placeLocation.y] = pTile;
    }

    @Override
    protected void fillUndecidedTiles() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] == UndecidedTile.getInstance()) {
                    grid[x][y] = AirTile.getInstance();
                }
            }
        }
    }

    private void removeRightWall() {
        for (int y = 1; y < grid[0].length - 1; y++) {
            grid[grid.length - 1][y] = new LavaTile();
        }
    }

    private int getGaussianNum(int min, int max) {
        int mean = (max + min) / 2;
        int std_dev = (mean - min) / 2;
        double gaussSize = min - 1;
        while (gaussSize < min || gaussSize > max) {
            gaussSize = (rand.nextGaussian() * std_dev) + mean;
        }

        return (int) Math.round(gaussSize);
    }

}
