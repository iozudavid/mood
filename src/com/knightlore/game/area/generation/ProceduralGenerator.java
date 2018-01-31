package com.knightlore.game.area.generation;

import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.Tile;

abstract class ProceduralGenerator {
    Tile[][] grid;

    protected abstract void fillGrid(long seed);

    protected void resetGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = AirTile.getInstance(); // TODO change to some block
            }
        }
    }
}
