package com.knightlore.game.area.generation;

import com.knightlore.game.Team;
import com.knightlore.game.area.Room;
import com.knightlore.game.area.RoomType;
import com.knightlore.game.entity.pickup.PickupType;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.LavaTile;
import com.knightlore.game.tile.PlayerSpawnTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.TurretTile;
import com.knightlore.game.tile.UndecidedTile;
import com.knightlore.game.tile.PickupTile;

import java.util.Random;

public class RoomGenerator extends ProceduralAreaGenerator {

    private static final int MIN_SIZE = 7;
    private static final int MAX_SIZE = 15;
    private static final int SPAWN_ROOM_MIN_CONNECTIONS = 1;
    private static final int SPAWN_ROOM_MAX_CONNECTIONS = 2;

    public Room createRoom(long seed, RoomType rt) {
        rand = new Random(seed);
        int height, width;
        if(rt == RoomType.spawn) {
            width = MIN_SIZE;
            height = width;
        }else {
            width = getGaussianNum(MIN_SIZE, MAX_SIZE);
            height = getGaussianNum(MIN_SIZE, MAX_SIZE);
        }
        grid = new Tile[width][height];
        resetGrid();
        fillGrid(rt);
        if(rt == RoomType.spawn) {
            return new Room(grid, SPAWN_ROOM_MIN_CONNECTIONS , SPAWN_ROOM_MAX_CONNECTIONS);
        }
        return new Room(grid, 2, 6);
    }

    @Override
    protected void fillGrid() {
        // TODO think about creating something more than just walls
        fillUndecidedTiles();
        addWalls();
    }
    
    protected void fillGrid(RoomType rt) {
        int width = grid.length;
        int height = grid[0].length;
        int midx = width/2;
        int midy = height/2;
        switch(rt) {
            case spawn : for(int i=midx-1 ; i<= midx+1; i++) {
                           for(int j=midy-1; j <=midy+1; j++) {
                               grid[i][j] = new PlayerSpawnTile(Team.blue);
                           }
                         }
                         //grid[3][3] = new TurretTile(Team.blue);
                         //grid[3][height-4] = new TurretTile(Team.blue);
                         //grid[width-4][3] = new TurretTile(Team.blue);
                         //grid[width-4][height-3] = new TurretTile(Team.blue);
                         break; 
            case pickup :
                        if(rand.nextDouble() < 0.5) {
                            grid[midx][midy] = new PickupTile(PickupType.health);
                        }else {
                            for(int i=0; i<width; i++) {
                                for(int j=0; j<height; j++) {
                                    grid[i][j] = new LavaTile();
                                }
                            }
                            grid[midx][midy] = new PickupTile(PickupType.shotgun);
                        }
                        break;
            default: break;
        }
        fillUndecidedTiles();
        addWalls();
    }

    private void addWalls() {
        int width = grid.length;
        int height = grid[0].length;
        for (int i = 0; i < width; i++) {
            grid[i][0] = new BrickTile();
            grid[i][height - 1] = new BrickTile();
        }

        for (int j = 0; j < height; j++) {
            grid[0][j] = new BrickTile();
            grid[width - 1][j] = new BrickTile();
        }
    }

    @Override
    protected void fillUndecidedTiles() {
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] == UndecidedTile.getInstance()) {
                    grid[x][y] = AirTile.getInstance();
                }
            }
        }
    }

    private int getGaussianNum(int min, int max) {
        int mean = (max + min) / 2;
        int std_dev = (mean - min) / 2;
        double gaussSize = min - 1;
        while (gaussSize < min || gaussSize > max) {
            gaussSize = (rand.nextGaussian() * std_dev) + mean;
        }

        return (int) Math.round(gaussSize);
    }
}
