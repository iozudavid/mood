package com.knightlore.render;

public class RaycasterUtils {

    public static double getWallHitPosition(Camera camera, double rayX, double rayY, int mapX, int mapY, boolean side,
            int stepX, int stepY) {
        double wallX;
        if (side) {// If its a y-axis wall
            wallX = (camera.getxPos() + ((mapY - camera.getyPos() + (1 - stepY) / 2) / rayY) * rayX);
        } else {// X-axis wall
            wallX = (camera.getyPos() + ((mapX - camera.getxPos() + (1 - stepX) / 2) / rayX) * rayY);
        }
        wallX -= Math.floor(wallX);
        return wallX;
    }

    public static int getDrawHeight(final int screenHeight, double distanceToWall) {
        int lineHeight;
        if (distanceToWall > 0) {
            lineHeight = Math.abs((int) (screenHeight / distanceToWall));
        } else {
            lineHeight = screenHeight;
        }
        return lineHeight;
    }

    public static double getImpactDistance(Camera camera, double rayX, double rayY, int mapX, int mapY, boolean side,
            int stepX, int stepY) {
        double distanceToWall;
        // Calculate distance to the point of impact
        if (!side) {
            distanceToWall = Math.abs((mapX - camera.getxPos() + (1 - stepX) / 2) / (rayX));
        } else {
            distanceToWall = Math.abs((mapY - camera.getyPos() + (1 - stepY) / 2) / (rayY));
        }
        return distanceToWall;
    }

}
