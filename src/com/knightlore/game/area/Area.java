package com.knightlore.game.area;

import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.LavaTile;
import com.knightlore.game.tile.PlayerSpawnTile;
import com.knightlore.game.tile.Tile;

import java.io.Serializable;

public abstract class Area implements Serializable {
    protected final int width, height;
    private final Tile[][] grid;
    private final double[][] costGrid;
    private final double LAVA_TILE_COST = 20d;

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
                } else if(tile instanceof PlayerSpawnTile) {
                    costGrid[i][j] = Double.POSITIVE_INFINITY;
                }else if(tile instanceof LavaTile){
                    costGrid[i][j] = LAVA_TILE_COST;
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
    
    public void setTile(Tile tile, int x, int y) {
        this.grid[x][y] = tile;
    }

    public double[][] getCostGrid() {
        return costGrid;
    }

    public int getWidth() {
        return grid.length;
        //return width;
    }

    public int getHeight() {
        return grid[0].length;
        //return height;
    }

    public String toDebugString() {
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
