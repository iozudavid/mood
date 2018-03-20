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
import com.knightlore.utils.Vector2D;
import com.knightlore.game.tile.PickupTile;

import java.util.Random;

public class RoomGenerator extends ProceduralAreaGenerator {

    private static final int MIN_SIZE = 7;
    private static final int MAX_SIZE = 15;

    private static final int SPAWN_ROOM_MIN_CONNECTIONS = 1;
    private static final int SPAWN_ROOM_MAX_CONNECTIONS = 2;
    
    private static final int MAX_INTERNAL_WALLS = 6;
    private static final double INTERNAL_WALL_PROBABILITY = 0.5;
    private static final int MINIMUM_SPLIT_LENGTH = 3;

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
        // Not called here
    }
    
    protected void fillGrid(RoomType rt) {
        int width = grid.length;
        int height = grid[0].length;
        int midx = width/2;
        int midy = height/2;
        switch(rt) {
            case spawn :
                for(int i=midx-1; i <= midx+1; i++){
                    grid[i][midy-1] = new PlayerSpawnTile(Team.BLUE, Vector2D.DOWN);
                }
                grid[midx-1][midy] = new PlayerSpawnTile(Team.BLUE, Vector2D.LEFT);
                grid[midx][midy] = new PlayerSpawnTile(Team.BLUE);
                grid[midx+1][midy] = new PlayerSpawnTile(Team.BLUE, Vector2D.RIGHT);
                for(int i=midx-1 ; i<= midx+1; i++) {
                    grid[i][midy+1] = new PlayerSpawnTile(Team.BLUE, Vector2D.UP);
                }
                break; 
            case health :
                grid[midx][midy] = new PickupTile(PickupType.health);
                break;
            case weapon :
                for(int i=0; i<width; i++) {
                    for(int j=0; j<height; j++) {
                        grid[i][j] = new LavaTile();
                    }
                }
                grid[midx][midy] = new PickupTile(PickupType.shotgun);
                break;
            case middle :
                if(rand.nextDouble() > 0.5) {
                    fillGrid(RoomType.weapon);
                }else {
                    fillGrid(RoomType.normal);
                }
                removeRightWall();
                return;
            case normal :
                addInternalWalls(BrickTile.class);
        }
        fillUndecidedTiles();
        addWalls();
    }

    private void addInternalWalls(Class roomClass) {
        try {
            int width = grid.length;
            int height = grid[0].length;
            int numInternalWalls = 0;
            if(width > height) {
                for(int i=2; i<width-3; i++) {
                    if(numInternalWalls == MAX_INTERNAL_WALLS) { return; };
                    if(rand.nextDouble() <= INTERNAL_WALL_PROBABILITY) {
                        addVerticalInternalWall(i, roomClass);
                        i++; // skip next iteration
                    }
                }
            }else {
                for(int j=2; j<height-3; j++) {
                    if(numInternalWalls == MAX_INTERNAL_WALLS) { return; };
                    if(rand.nextDouble() <= INTERNAL_WALL_PROBABILITY) {
                        addHorizontalInternalWall(j, roomClass);
                        j++; // skip next iteration
                    }
                }
            }
            
        }catch(InstantiationException e) {
            System.out.println("Could not instantiate given class");
        }catch(IllegalAccessException e) {
            System.out.println("Could not cast class to Tile");
        }
    }
    
    private void addHorizontalInternalWall(int y, Class roomClass) throws InstantiationException, IllegalAccessException {
        int width = grid.length;
        int distanceIntoRoom = 2 + rand.nextInt(width/4);
        int start = distanceIntoRoom;
        int end = (width - 1) - distanceIntoRoom;
        // place straight wall
        for(int i=start; i < end; i++) {
            grid[i][y] = (Tile) roomClass.newInstance();
        }
        // now put in gaps
        addHorizontalGaps(y , start, end);
    }
    
    private void addVerticalInternalWall(int x, Class roomClass) throws InstantiationException, IllegalAccessException {
        int height = grid[0].length;
        int distanceIntoRoom = 2 + rand.nextInt(height/4);
        int start = distanceIntoRoom;
        int end = (height - 1) - distanceIntoRoom;
        // place straight wall
        for(int j=start; j < end; j++) {
            grid[x][j] = (Tile) roomClass.newInstance();
        }
        // now put in gaps
        addVerticalGaps(x, start, end);
    }
    
    private void addHorizontalGaps(int y, int startx, int endx) {
        int midx = startx + ( (endx - startx) / 2);
        //TODO: Non-recursive definition
        int gapApps = 0;
        for(int i=0; i<gapApps; i++) {
            int x = rand.nextInt(midx+1);
            grid[x][y] = UndecidedTile.getInstance();
            grid[midx + (midx-x)][y] = UndecidedTile.getInstance();
        }
    }
    
    private void addVerticalGaps(int x, int starty, int endy) {
        int midy = starty + ( (endy - starty) / 2);
        //TODO: Non-recursive definition
        int gapApps = 0;
        for(int j=0; j<gapApps; j++) {
            int y = rand.nextInt(midy+1);
            grid[x][y] = UndecidedTile.getInstance();
            grid[x][midy + (midy-y)] = UndecidedTile.getInstance();
        }
    }
    
    private void addWalls() {
        int width = grid.length;
        int height = grid[0].length;
        for (int i = 0; i < width; i++) {
            grid[i][0] = BrickTile.getInstance();
            grid[i][height - 1] = BrickTile.getInstance();
        }

        for (int j = 0; j < height; j++) {
            grid[0][j] = BrickTile.getInstance();
            grid[width - 1][j] = BrickTile.getInstance();
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

    private void removeRightWall() {
        for(int y=1; y < grid[0].length -1 ; y++) {
            grid[grid.length-1][y] = new LavaTile();
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
