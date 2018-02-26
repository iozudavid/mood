package com.knightlore.game.area.generation;

import com.knightlore.game.area.Room;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.UndecidedTile;

import java.util.Random;

public class RoomGenerator extends ProceduralAreaGenerator {
    private static final int MIN_SIZE = 4;
    private static final int MAX_SIZE = 16;

    public Room createRoom(long seed) {
        rand = new Random(seed);

        int width = getGaussianNum(MIN_SIZE, MAX_SIZE);
        int height = getGaussianNum(MIN_SIZE, MAX_SIZE);
        grid = new Tile[width][height];

        resetGrid();
        fillGrid();
        return new Room(grid);
    }

    @Override
    protected void fillGrid() {
        // TODO think about creating something more than just walls
        addWalls();
        fillUndecidedTiles();
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
