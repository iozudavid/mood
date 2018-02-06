package com.knightlore.game.area;

import com.knightlore.game.tile.Tile;

public abstract class Area {
    protected final int width, height;
    protected final Tile[][] grid;

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
    }

    public Tile getTile(int x, int y) {
        return grid[x][y];
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
