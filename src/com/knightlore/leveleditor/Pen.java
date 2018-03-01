package com.knightlore.leveleditor;

import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.BushTile;
import com.knightlore.game.tile.Tile;

public class Pen {

    enum ETile {
        AIR, BRICK, BUSH
    }

    protected ETile stroke;

    public Pen(ETile stroke) {
        this.stroke = stroke;
    }

    public Tile getTile() {
        return getTile(stroke);
    }

    public Tile getTile(ETile e) {
        switch (stroke) {
        case AIR:
            return AirTile.getInstance();
        case BRICK:
            return new BrickTile();
        case BUSH:
            return new BushTile();
        default:
            return AirTile.getInstance();
        }
    }

}
