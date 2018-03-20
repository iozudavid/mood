package com.knightlore.ai;

import com.knightlore.game.area.Map;
import com.knightlore.utils.Vector2D;
import org.junit.Test;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AIManagerTest {

    @Test
    public void findPath() {
        // Given
        Map map = mock(Map.class);
        when(map.getCostGrid()).thenReturn(new double[][]{
                {5, 5, 1, 1, 1, 5, 5},
                {5, 1, 1, 5, 1, 1, 5},
                {1, 1, 5, 5, 5, 1, 5},
                {5, 5, 5, 5, 5, 5, 5}
        });

        Vector2D start = new Vector2D(2.41, 0.99);
        Vector2D end = new Vector2D(2.54, 6.45);
        AIManager manager = new AIManager(map);

        // When
        List<Point> expectedPath = Arrays.asList(
                new Point(2, 0), new Point(2, 1), new Point(1, 1), new Point(1, 2),
                new Point(0, 2), new Point(0, 3), new Point(0, 4), new Point(1, 4),
                new Point(1, 5), new Point(2, 5), new Point(2,6)
        );
        List<Point> path = manager.findPath(start, end);

        // Then
        assertThat(path, is(expectedPath));
    }
}