package com.knightlore.game.area.generation;

import com.knightlore.game.area.Map;
import com.knightlore.game.area.Room;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.render.Environment;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
