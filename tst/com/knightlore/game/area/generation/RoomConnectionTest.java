package com.knightlore.game.area.generation;

import com.knightlore.game.area.Room;
import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.Tile;
import org.junit.Test;

import java.awt.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RoomConnectionTest {

    @Test
    public void euclideanDistance() {
        // Given
        Tile[][] grid = new Tile[1][1];
        grid[0][0] = AirTile.getInstance();
        Room room1 = new Room(grid);
        Room room2 = new Room(grid);
        RoomConnection connection = new RoomConnection(room1, room2);

        Point start = new Point(5, 8);
        Point end = new Point(9, 11);

        // When
        double distance = connection.euclideanDistance(start, end);

        // Then
        assertThat(distance, is(5.0));
    }

    @Test
    public void euclideanDistance_samePoints() {
        // Given
        Tile[][] grid = new Tile[1][1];
        grid[0][0] = AirTile.getInstance();
        Room room1 = new Room(grid);
        Room room2 = new Room(grid);
        RoomConnection connection = new RoomConnection(room1, room2);

        Point start = new Point(9, 11);
        Point end = new Point(9, 11);

        // When
        double distance = connection.euclideanDistance(start, end);

        // Then
        assertThat(distance, is(0.0));
    }
}