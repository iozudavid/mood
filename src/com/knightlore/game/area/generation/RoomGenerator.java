package com.knightlore.game.area.generation;

import com.knightlore.game.area.Room;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.Tile;

import java.util.Random;

public class RoomGenerator extends ProceduralGenerator {
    private static final int MIN_SIZE = 4;
    private static final int MAX_SIZE = 32;
    private static final float STD_DEV = 6;

    public Room createRoom(long seed) {
        rand = new Random(seed);

        int width = getRandomSize();
        int height = getRandomSize();
        grid = new Tile[width][height];

        resetGrid();
        fillGrid();
        return new Room(grid);
    }

    @Override
    protected void fillGrid() {
        // TODO think about creating something more than just walls
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                grid[x][y] = AirTile.getInstance();
            }
        }

        addWalls();
    }

    private int getRandomSize() {
        int meanSize = (MIN_SIZE + MAX_SIZE);
        double gaussSize = -1;
        while (gaussSize < MIN_SIZE || gaussSize > MAX_SIZE) {
            gaussSize = (rand.nextGaussian() * STD_DEV) + meanSize;
        }

        return (int)Math.round(gaussSize);
    }

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
