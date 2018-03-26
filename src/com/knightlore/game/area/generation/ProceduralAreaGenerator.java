package com.knightlore.game.area.generation;

import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.UndecidedTile;

import java.util.Random;

abstract class ProceduralAreaGenerator {
    protected Random rand;
    protected Tile[][] grid;
    
    protected void resetGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = UndecidedTile.getInstance();
            }
        }
    }
}
