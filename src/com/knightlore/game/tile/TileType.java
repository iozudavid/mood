package com.knightlore.game.tile;

public enum TileType {

    air, brick, bush, expect, spawn, turret, undecided;

    public static Tile fromTileType(TileType t) {
        switch (t) {
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
