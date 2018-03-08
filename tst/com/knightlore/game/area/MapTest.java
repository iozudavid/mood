package com.knightlore.game.area;

import com.knightlore.game.tile.AirTile;
import com.knightlore.game.tile.BrickTile;
import com.knightlore.game.tile.Tile;
import org.junit.Test;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class MapTest {

    @Test
    public void getRandomSpawnPoint() {
        // Given
        Random rand = new Random();
        long seed = rand.nextLong();

        Tile[][] grid = new Tile[5][5];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = new BrickTile();
            }
        }

        List<Point> possibleSpawnPoints = Arrays.asList(
                new Point(1, 1), new Point(1, 2), new Point(2, 2),
                new Point(2, 3), new Point(3, 3), new Point(3, 2)
        );
        for (Point p: possibleSpawnPoints) {
            grid[p.x][p.y] = AirTile.getInstance();
        }


        // When
        Map map = new Map(grid, seed);
        Point spawn = map.getRandomSpawnPoint().toPoint();

        // Then
        assertThat(possibleSpawnPoints, hasItem(spawn));
    }
}