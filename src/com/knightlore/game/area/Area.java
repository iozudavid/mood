package com.knightlore.game.area;

import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.Tile;

public abstract class Area {
    protected final int width, height;
    private final Tile[][] grid;

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
    
    //this is used to render other players
    //added for prototype presentation
    //as a quick solution
    public void setTile(int x, int y, Tile t) {
        grid[x][y] = t;
    }

    public Tile getTile(int x, int y) {
        if(x < 0 || x >= width || y < 0 || y >= height) {
            return AirTile.getInstance();
        }
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
