package com.knightlore.game.area;

import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.Tile;

public abstract class Area {
    protected final int width, height;
    private final Tile[][] grid;
    private final double[][] costGrid;

    public Area(Tile[][] grid) {
        this.width = grid.length;
        if (width < 1) {
            throw new IllegalArgumentException("Area cannot have width of 0");
        }

        this.height = grid[0].length;
        if (height < 1) {
            throw new IllegalArgumentException("Area cannot have height of 0");
        }

        this.grid = grid;
        this.costGrid = initCostGrid();
    }

    private double[][] initCostGrid() {
        double[][] costGrid = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Tile tile = grid[i][j];
                if (tile.getSolidity() >= 1) {
                    costGrid[i][j] = Double.POSITIVE_INFINITY;
                } else {
                    costGrid[i][j] = 1 / (1 - tile.getSolidity());
                }
            }
        }

        return costGrid;
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return AirTile.getInstance();
        }
        return grid[x][y];
    }

    public double[][] getCostGrid() {
        return costGrid;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String toString() {
        StringBuilder sBuilder = new StringBuilder("AREA\n" + "WIDTH = " + width + "\n" + "HEIGHT = " + height + "\n");
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                sBuilder.append(grid[i][j].toChar());
            }
            sBuilder.append("\n");
        }

        return sBuilder.toString();
    }

}
