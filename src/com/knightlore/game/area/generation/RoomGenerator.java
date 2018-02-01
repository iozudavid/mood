package com.knightlore.game.area.generation;

import com.knightlore.game.area.Room;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.UndecidedTile;
import com.knightlore.utils.Direction;

import java.util.Random;

public class RoomGenerator extends ProceduralGenerator {
    private static final int MIN_SIZE = 4;
    private static final int MAX_SIZE = 16;
    private static final int MIN_ENTRANCES_NUM = 2;
    private static final int MAX_ENTRANCES_NUM = 6;

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
        addEntrances();
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

    private void addEntrances() {
        int entrancesNum = getGaussianNum(MIN_ENTRANCES_NUM, MAX_ENTRANCES_NUM);
        for (int i = 0; i < entrancesNum; i++) {
            addEntrance();
        }
    }

    private void addEntrance() {
        int xPos = rand.nextInt(grid.length);
        int yPos = rand.nextInt(grid[0].length);
        Direction wall = Direction.values()[rand.nextInt(Direction.values().length)];
        switch (wall) {
            case NORTH:
                yPos = 0;
                break;
            case WEST:
                xPos = 0;
                break;
            case SOUTH:
                yPos = grid[0].length - 1;
                break;
            case EAST:
                xPos = grid.length - 1;
                break;
        }

        // TODO remember somehow where entrances occur?
        // TODO make it possible to decied what tile is at entrance
        grid[xPos][yPos] = AirTile.getInstance();
    }

    @Override
    protected void fillUndecidedTiles() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if(grid[x][y] == UndecidedTile.getInstance()) {
                    grid[x][y] = AirTile.getInstance();
                }
            }
        }
    }

    private int getGaussianNum(int min, int max) {
        int mean = (min - max) / 2;
        int std_dev = (mean - min) / 2;
        double gaussSize = min - 1;
        while (gaussSize < min || gaussSize > max) {
            gaussSize = (rand.nextGaussian() * std_dev) + mean;
        }

        return (int) Math.round(gaussSize);
    }
}
