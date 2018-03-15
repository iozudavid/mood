package com.knightlore.game.area.generation;

import com.knightlore.game.Team;
import com.knightlore.game.area.Room;
import com.knightlore.game.tile.*;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RoomGeneratorTest {

    @Test
    public void createRoom_defaultRoom() {
        // Given
        long seed = 890312312355623L;

        // When
        RoomGenerator generator = new RoomGenerator();
        Room room = generator.createRoom(seed, Team.none);


        // Then
        Tile[][] expectedGrid = new Tile[8][10];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 || i == 7 || j == 0 || j == 9) {
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
        Room blueRoom = generator.createRoom(seed, Team.blue);
        Room redRoom = generator.createRoom(seed, Team.red);

        // Then
        Tile[][] expectedGrid = new Tile[8][10];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 || i == 7 || j == 0 || j == 9) {
                    expectedGrid[i][j] = new BrickTile();
                } else {
                    expectedGrid[i][j] = AirTile.getInstance();
                }
            }
        }
        expectedGrid[2][5] = new TurretTile(Team.none);
        expectedGrid[4][5] = new PlayerSpawnTile(Team.none);
        expectedGrid[6][5] = new TurretTile(Team.none);
        Room expectedRoom = new Room(expectedGrid);

        assertThat(blueRoom, is(redRoom));
        assertThat(redRoom, is(expectedRoom));
    }
}