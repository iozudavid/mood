package com.knightlore.game.area.generation;

import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.UndecidedTile;

import java.util.Random;

abstract class ProceduralGenerator {
    protected Random rand;
    protected Tile[][] grid;

    protected abstract void fillGrid();

    protected void resetGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = UndecidedTile.getInstance(); // TODO change to some block
            }
        }
    }
}
