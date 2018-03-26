package com.knightlore.utils.pathfinding;

import com.knightlore.utils.Vector2D;
import org.junit.Test;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PathFinderTest {
    
    @Test
    public void findPath_pointUniversalCostGrid() {
        // Given
        double[][] costGrid = new double[18][7];
        for (int i = 0; i < costGrid.length; i++) {
            for (int j = 0; j < costGrid[0].length; j++) {
                costGrid[i][j] = 1;
            }
        }
        
        Point start = new Point(13, 2);
        Point end = new Point(13, 6);
        
        // When
        PathFinder finder = new PathFinder(costGrid);
        List<Point> path = finder.findPath(start, end);
        
        // Then
        List<Point> properPath = Arrays.asList(new Point(13, 2), new Point(13, 3), new Point(13, 4),
                new Point(13, 5), new Point(13, 6));
        assertThat(path, is(properPath));
    }
    
    @Test
    public void findPath_vector2DUniversalCostGrid() {
        // Given
        double[][] costGrid = new double[18][7];
        for (int i = 0; i < costGrid.length; i++) {
            for (int j = 0; j < costGrid[0].length; j++) {
                costGrid[i][j] = 1;
            }
        }
        
        Vector2D start = new Vector2D(13.24, 2.999);
        Vector2D end = new Vector2D(13.001233123, 6.88);
        
        // When
        PathFinder finder = new PathFinder(costGrid);
        List<Point> path = finder.findPath(start, end);
        
        // Then
        List<Point> properPath = Arrays.asList(new Point(13, 2), new Point(13, 3), new Point(13, 4),
                new Point(13, 5), new Point(13, 6));
        assertThat(path, is(properPath));
    }
    
    @Test
    public void findPath_vector2DVaryingCostGrid() {
        // Given
        double[][] costGrid = new double[18][7];
        for (int i = 0; i < costGrid.length; i++) {
            for (int j = 0; j < costGrid[0].length; j++) {
                costGrid[i][j] = 1;
            }
        }
        
        costGrid[13][4] = 10;
        costGrid[12][3] = 2;
        costGrid[14][6] = 2;
        Vector2D start = new Vector2D(13.24, 2.79);
        Vector2D end = new Vector2D(13.01952, 6.991);
        
        // When
        PathFinder finder = new PathFinder(costGrid);
        List<Point> path = finder.findPath(start, end);
        
        // Then
        List<Point> properPath = Arrays.asList(new Point(13, 2), new Point(13, 3), new Point(14, 3),
                new Point(14, 4), new Point(14, 5), new Point(13, 5), new Point(13, 6));
        assertThat(path, is(properPath));
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void findPath_outOfBound() {
        // Given
        double[][] costGrid = new double[18][7];
        for (int i = 0; i < costGrid.length; i++) {
            for (int j = 0; j < costGrid[0].length; j++) {
                costGrid[i][j] = 1;
            }
        }
        
        Point start = new Point(13, 2);
        Point end = new Point(13, 7);
        
        // When
        PathFinder finder = new PathFinder(costGrid);
        finder.findPath(start, end);
    }
}