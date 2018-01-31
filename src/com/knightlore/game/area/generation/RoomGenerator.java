package com.knightlore.game.area.generation;

import com.knightlore.game.area.Room;
import com.knightlore.game.tile.Tile;

public class RoomGenerator extends ProceduralGenerator {
    public Room createRoom(int width, int height, long seed) {
        grid = new Tile[width][height];
        resetGrid();
        fillGrid(seed);
        return new Room(grid);
    }

    @Override
    protected void fillGrid(long seed) {
        // TODO implement
    }
}
