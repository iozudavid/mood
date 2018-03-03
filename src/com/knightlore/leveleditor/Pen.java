package com.knightlore.leveleditor;

import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.BushTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.TileType;

public class Pen {

    protected TileType stroke;

    public Pen(TileType stroke) {
        this.stroke = stroke;
    }

    public Tile getTile() {
        return getTile(stroke);
    }

    public Tile getTile(TileType e) {
        switch (stroke) {
        case air:
            return AirTile.getInstance();
        case brick:
            return new BrickTile();
        case bush:
            return new BushTile();
        default:
            return AirTile.getInstance();
        }
    }

}
