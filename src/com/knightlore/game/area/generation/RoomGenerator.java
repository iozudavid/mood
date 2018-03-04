package com.knightlore.game.area.generation;

import com.knightlore.game.Team;
import com.knightlore.game.area.Room;
import com.knightlore.game.area.RoomType;
import com.knightlore.game.entity.weapon.Shotgun;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BreakibleTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.LavaTile;
import com.knightlore.game.tile.PlayerSpawnTile;
import com.knightlore.game.tile.Tile;
import com.knightlore.game.tile.TurretTile;
import com.knightlore.game.tile.UndecidedTile;
import com.knightlore.game.tile.WeaponTile;

import java.util.Random;

public class RoomGenerator extends ProceduralAreaGenerator {
    private static final int MIN_SIZE = 7;
    private static final int MAX_SIZE = 15;

    public Room createRoom(long seed , Team team) {
        rand = new Random(seed);

        int height = getGaussianNum(MIN_SIZE, MAX_SIZE);
        int width = getGaussianNum(MIN_SIZE, MAX_SIZE);
        grid = new Tile[width][height];

        resetGrid();
        fillGrid();
        if(team != Team.none) {
            grid[width/2][height/2] = new PlayerSpawnTile(team);
            //turrets too!
            grid[2][height/2] = new TurretTile(team);
            grid[width-2][height/2] = new TurretTile(team);
            return new Room(grid, 1 , 2);
        }

        return new Room(grid, 2, 6);
    }
    
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
        switch(rt) {
            case spawn : grid[width/2][height/2] = new PlayerSpawnTile(Team.blue);
                         grid[2][2] = new TurretTile(Team.blue);
                         grid[2][height-3] = new TurretTile(Team.blue);
                         grid[width-3][2] = new TurretTile(Team.blue);
                         grid[width-3][height-3] = new TurretTile(Team.blue);
                         break;
            case weapon : 
                          for(int x=0; x < grid.length; x++) {
                              for(int y=0; y<grid[0].length; y++) {
                                  grid[x][y] = new LavaTile();
                              }
                          }
                          grid[2][2] = new TurretTile(Team.none);
                          grid[2][height-3] = new TurretTile(Team.none);
                          grid[width-3][2] = new TurretTile(Team.none);
                          grid[width-3][height-3] = new TurretTile(Team.none);
                          // surround weapon with breakible tiles
                          for(int x = width/2 -1; x<width/2 + 2; x++) {
                              for(int y = height/2 -1; y<height/2 + 2; y++) {
                                  grid[x][y] = new BreakibleTile();
                              }
                          }

                          // TODO: Pick a weapon in the weapon tile class
                          grid[width/2][height/2] = new WeaponTile(new Shotgun());                         
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
