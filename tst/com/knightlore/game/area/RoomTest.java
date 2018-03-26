package com.knightlore.game.area;

import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.Tile;
import org.junit.Test;

import java.awt.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RoomTest {
    
    @Test
    public void addConnection_successfulAddition() {
        // Given
        Tile[][] grid = new Tile[4][4];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = AirTile.getInstance();
            }
        }
        Room room1 = new Room(grid);
        Room room2 = new Room(grid);
        
        // When
        boolean success = Room.addConnection(room1, room2);
        
        // Then
        assertThat(success, is(true));
        assertThat(room1.getNumConnections(), is(1));
        assertThat(room1.getConnections().get(0), is(room2));
        assertThat(room2.getNumConnections(), is(1));
        assertThat(room2.getConnections().get(0), is(room1));
    }
    
    @Test
    public void addConnection_exceedsLimit() {
        // Given
        Tile[][] grid = new Tile[4][4];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = AirTile.getInstance();
            }
        }
        Room room1 = new Room(grid, 0, 0);
        Room room2 = new Room(grid);
        
        // When
        boolean success = Room.addConnection(room1, room2);
        
        // Then
        assertThat(success, is(false));
        assertThat(room1.getNumConnections(), is(0));
        assertThat(room2.getNumConnections(), is(0));
    }
    
    @Test
    public void getCentre_defaultPosition() {
        // Given
        Tile[][] grid = new Tile[4][4];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = AirTile.getInstance();
            }
        }
        Room room = new Room(grid);
        
        // When
        Point expectedCentre = new Point(2, 2);
        
        // Then
        assertThat(room.getCentre(), is(expectedCentre));
    }
    
    @Test
    public void getCentre_setPosition() {
        // Given
        Tile[][] grid = new Tile[4][4];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = AirTile.getInstance();
            }
        }
        Room room = new Room(grid);
        room.setRoomPosition(new Point(2, 1));
        
        // When
        Point expectedCentre = new Point(4, 3);
        
        // Then
        assertThat(room.getCentre(), is(expectedCentre));
    }
    
    @Test
    public void equals_sameRoom() {
        // Given
        Tile[][] grid = new Tile[4][4];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = AirTile.getInstance();
            }
        }
        Room room1 = new Room(grid);
        Room room2 = new Room(grid);
        
        // When
        boolean areEqual = room1.equals(room2);
        
        // Then
        assertThat(areEqual, is(true));
    }
    
    @Test
    public void equals_differentPosition() {
        // Given
        Tile[][] grid = new Tile[4][4];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = AirTile.getInstance();
            }
        }
        Room room1 = new Room(grid);
        room1.setRoomPosition(new Point(1, 1));
        Room room2 = new Room(grid);
        
        // When
        boolean areEqual = room1.equals(room2);
        
        // Then
        assertThat(areEqual, is(false));
    }
    
    @Test
    public void equals_differentGrid() {
        // Given
        Tile[][] grid1 = new Tile[4][4];
        Tile[][] grid2 = new Tile[4][4];
        for (int i = 0; i < grid1.length; i++) {
            for (int j = 0; j < grid1[0].length; j++) {
                grid1[i][j] = AirTile.getInstance();
                grid2[i][j] = AirTile.getInstance();
            }
        }
        Room room1 = new Room(grid1);
        grid2[1][3] = new BrickTile();
        Room room2 = new Room(grid2);
        
        // When
        boolean areEqual = room1.equals(room2);
        
        // Then
        assertThat(areEqual, is(false));
    }
}
