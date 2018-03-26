package com.knightlore.game.area.generation;

import com.knightlore.game.Team;
import com.knightlore.game.area.Room;
import com.knightlore.game.area.RoomType;
import com.knightlore.game.tile.*;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RoomGeneratorTest {
    
    @Test
    public void createRoom_normalRoom() {
        // Given
        long seed = 890312312355623L;
        
        // When
        RoomGenerator generator = new RoomGenerator();
        Room room = generator.createRoom(seed, RoomType.NORMAL);
        
        
        // Then
        Tile[][] expectedGrid = new Tile[10][8];
        for (int i = 0; i < expectedGrid.length; i++) {
            for (int j = 0; j < expectedGrid[0].length; j++) {
                if ((i == 0 || i == expectedGrid.length - 1 || j == 0 || j == expectedGrid[0].length - 1)
                        || (j == 2 && i > 1 && i < 7)) {
                    expectedGrid[i][j] = new BrickTile();
                } else {
                    expectedGrid[i][j] = AirTile.getInstance();
                }
            }
        }
        Room expectedRoom = new Room(expectedGrid);
        
        assertThat(room, is(expectedRoom));
    }
    
    @Test
    public void createRoom_spawnRoom() {
        // Given
        long seed = 890312312355623L;
        
        // When
        RoomGenerator generator = new RoomGenerator();
        Room room = generator.createRoom(seed, RoomType.SPAWN);
        
        // Then
        Tile[][] expectedGrid = new Tile[7][7];
        for (int i = 0; i < expectedGrid.length; i++) {
            for (int j = 0; j < expectedGrid[0].length; j++) {
                if ((i == 0 || i == expectedGrid.length - 1 || j == 0 || j == expectedGrid[0].length - 1)) {
                    expectedGrid[i][j] = new BrickTile();
                } else if (i > 1 && i < 5 && j > 1 && j < 5) {
                    expectedGrid[i][j] = new PlayerSpawnTile(Team.BLUE);
                } else {
                    expectedGrid[i][j] = AirTile.getInstance();
                }
            }
        }
        Room expectedRoom = new Room(expectedGrid);
        
        assertThat(room, is(expectedRoom));
    }
}