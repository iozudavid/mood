package com.knightlore.render;

import com.knightlore.game.area.Map;

public class LighingPreprocessor {

    public static void process(Map map) {
        double[][] illum = new double[map.getWidth()][map.getHeight()];
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                illum[x][y] = map.getTile(x, y).getIllumination();
            }
        }

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if(illum[x][y] > 0) {
                    map.getTile(x + 1, y).setIllumination(illum[x][y] / 3);
                    map.getTile(x - 1, y).setIllumination(illum[x][y] / 3);
                    map.getTile(x, y + 1).setIllumination(illum[x][y] / 3);
                    map.getTile(x, y - 1).setIllumination(illum[x][y] / 3);
                }
            }
        }
    }

}
