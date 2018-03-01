package com.knightlore.game.area;

import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.TileType;

public class MapSerializer {

    public static String mapToString(Map map) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%d %d\n", map.getWidth(), map.getHeight()));
        for (int y = 0; y < map.height; y++) {
            for (int x = 0; x < map.width; x++) {
                Tile theTile = map.getTile(x, y);
                builder.append(String.format("%s,", theTile.getTileType().toString()));
            }
        }
        return builder.toString();
    }

    public static Map mapFromString(String str) {
        String[] lines = str.split("\n");
        String[] sizing = lines[0].split(" ");

        int width = Integer.parseInt(sizing[0]);
        int height = Integer.parseInt(sizing[1]);

        Tile[][] grid = new Tile[width][height];

        int x = 0, y = 0;
        for (String c : lines[1].split(",")) {
            grid[x++][y] = TileType.fromTileType(TileType.valueOf(c));
            if (x >= width) {
                x = 0;
                y++;
            }
        }

        return new Map(grid, 0L);
    }

}
