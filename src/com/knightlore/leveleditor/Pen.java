package com.knightlore.leveleditor;

import com.knightlore.game.tile.Tile;

public class Pen {
    
    protected Tile stroke;
    
    public Pen(Tile stroke) {
        this.stroke = stroke;
    }
    
    public Tile getTile() {
        return stroke.copy();
    }
    
    public Tile getTile(Tile tile) {
        return tile.copy();
    }
    
}
