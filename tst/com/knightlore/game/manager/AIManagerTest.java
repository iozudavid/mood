package com.knightlore.game.manager;

import com.knightlore.game.area.Map;
import com.knightlore.game.manager.AIManager;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.physics.Physics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Physics.class)
public class AIManagerTest {

    @Test
    public void findRawPath() {
        // Given
        Map map = mock(Map.class);
        when(map.getCostGrid()).thenReturn(new double[][] {
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
        List<Point> path = manager.findRawPath(start, end);

        // Then
        assertThat(path, is(expectedPath));
    }

    @Test
    public void pruneUnnecessaryNodes_prunablePath() throws Exception {
        // Given
        Vector2D start = new Vector2D(3, 3);
        Vector2D end = new Vector2D(5, 8);
        Vector2D visibleFromStart = new Vector2D(5, 7);
        List<Point> rawPath = new LinkedList<>(Arrays.asList(
                start.toPoint(), new Point(3, 4), new Point(3, 5), new Point(4, 5),
                new Point(4, 6), new Point(4, 7), visibleFromStart.toPoint(), end.toPoint()
        ));

        // When
        Map map = mock(Map.class);
        when(map.getCostGrid()).thenReturn(new double[][]{{0}});

        mockStatic(Physics.class);
        when(Physics.linecastQuick(any(Vector2D.class), any(Vector2D.class), eq(AIManager.PATH_SMOOTH_ACCURACY)))
                .thenThrow(new IllegalStateException("This method shouldn't be called, AIManager should alread return"));
        doReturn(true).when(Physics.class, "linecastQuick",
                eq(start), eq(end), eq(AIManager.PATH_SMOOTH_ACCURACY));
        doReturn(false).when(Physics.class, "linecastQuick",
                eq(start), eq(visibleFromStart), eq(AIManager.PATH_SMOOTH_ACCURACY));

        AIManager aiManager = new AIManager(map);

        List<Point> prunedPath = aiManager.pruneUnnecessaryNodes(new LinkedList<>(rawPath));

        // Then
        List<Point> expectedPath = Arrays.asList( visibleFromStart.toPoint(), end.toPoint());
        assertThat(prunedPath, is(expectedPath));
    }

    @Test
    public void pruneUnnecessaryNodes_unprunablePath() {
        // Given
        Vector2D start = new Vector2D(3, 3);
        Vector2D end = new Vector2D(5, 8);
        List<Point> rawPath = new LinkedList<>(Arrays.asList(
                start.toPoint(), new Point(3, 4), new Point(3, 5), new Point(4, 5),
                new Point(4, 6), new Point(4, 7), new Point(5, 7), end.toPoint()
        ));

        // When
        Map map = mock(Map.class);
        when(map.getCostGrid()).thenReturn(new double[][]{{0}});

        mockStatic(Physics.class);
        when(Physics.linecastQuick(any(Vector2D.class), any(Vector2D.class), eq(AIManager.PATH_SMOOTH_ACCURACY)))
                .thenReturn(true);

        AIManager aiManager = new AIManager(map);

        List<Point> prunedPath = aiManager.pruneUnnecessaryNodes(new LinkedList<>(rawPath));
        
        // Then
        assertThat(prunedPath, is(rawPath));
    }
}