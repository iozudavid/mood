package com.knightlore.utils;


import org.junit.Before;
import org.junit.Test;

import java.awt.Point;

import static com.knightlore.CustomMatchers.isCloseTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class Vector2DTest {
    private double constant;
    private Vector2D vector1;
    private Vector2D vector2;
    
    @Before
    public void setUp() {
        // Given
        constant = 13.623;
        vector1 = new Vector2D(2.135, 5.1211);
        vector2 = new Vector2D(12.581, -3.111);
    }
    
    @Test
    public void fromTilePoint() {
        // Given
        Point point = new Point(-3, 0);
        
        // When
        Vector2D vector = Vector2D.fromTilePoint(point);
        Vector2D expectedVector = new Vector2D(-2.5, 0.5);
        
        assertThat(vector, is(expectedVector));
    }
    
    @Test
    public void fromGridRef() {
        // Given
        int x = 12;
        int y = -23;
        
        // When
        Vector2D vector = Vector2D.fromGridRef(x, y);
        Vector2D expectedVector = new Vector2D(12.5, -22.5);
        
        // Then
        assertThat(vector, is(expectedVector));
    }
    
    @Test
    public void fromPoint() {
        // Given
        Point point = new Point(-3, 0);
    
        // When
        Vector2D vector = Vector2D.fromPoint(point);
        Vector2D expectedVector = new Vector2D(-3, 0);
    
        assertThat(vector, is(expectedVector));
    }
    
    @Test
    public void staticAdd() {
        // When
        Vector2D sum = Vector2D.add(vector1, vector2);
        Vector2D expectedSum = new Vector2D(14.716, 2.0101);
        
        // Then
        assertThat(sum, is(expectedSum));
    }
    
    @Test
    public void staticMul() {
        // When
        Vector2D product1 = Vector2D.mul(vector1, constant);
        Vector2D product2 = Vector2D.mul(vector2, constant);
        Vector2D expectedProduct1 = new Vector2D(29.085105, 69.7647453);
        Vector2D expectedProduct2 = new Vector2D(171.390963, -42.381153);
    
        // Then
        assertThat(product1, isCloseTo(expectedProduct1, 0.00001));
        assertThat(product2, isCloseTo(expectedProduct2, 0.00001));
    }
    
    @Test
    public void staticSub() {
        // When
        Vector2D difference = Vector2D.sub(vector1, vector2);
        Vector2D expectedDifference = new Vector2D(-10.446, 8.2321);
    
        // Then
        assertThat(difference, is(expectedDifference));
    }
    
    @Test
    public void add() {
        // When
        Vector2D sum = vector1.add(vector2);
        Vector2D expectedSum = new Vector2D(14.716, 2.0101);
    
        // Then
        assertThat(sum, is(expectedSum));
    }
    
    @Test
    public void subtract() {
        // When
        Vector2D difference = vector1.subtract(vector2);
        Vector2D expectedDifference = new Vector2D(-10.446, 8.2321);
    
        // Then
        assertThat(difference, is(expectedDifference));
    }
    
    @Test
    public void perpendicular() {
        // When
        Vector2D perpendicular1 = vector1.perpendicular();
        Vector2D perpendicular2 = vector2.perpendicular();
        Vector2D expectedPerpendicular1 = new Vector2D(5.1211, -2.135);
        Vector2D expectedPerpendicular2 = new Vector2D( -3.111, -12.581);
    
        // Then
        assertThat(perpendicular1, is(expectedPerpendicular1));
        assertThat(perpendicular2, is(expectedPerpendicular2));
    }
    
    @Test
    public void cross() {
        // When
        Double cross = vector1.cross(vector2);
        Double expectedCross = -71.0705441;
    
        // Then
        assertThat(cross, isCloseTo(expectedCross, 0.00001));
    }
    
    @Test
    public void dot() {
        // When
        double dot = vector1.dot(vector2);
        double expectedDot = 10.9286929;
    
        // Then
        assertThat(dot, isCloseTo(expectedDot, 0.00001));
    }
    
    @Test
    public void distance() {
        // When
        double distance = vector1.distance(vector2);
        double expectedDistance = 13.2998641501;
    
        // Then
        assertThat(distance, isCloseTo(expectedDistance, 0.00001));
    }
    
    @Test
    public void magnitude() {
        // When
        double magnitude1 = vector1.magnitude();
        double expectedMagnitude1 = 5.54832318904;
        double magnitude2 = vector2.magnitude();
        double expectedMagnitude2 = 12.959933719;
    
        // Then
        assertThat(magnitude1, isCloseTo(expectedMagnitude1, 0.00001));
        assertThat(magnitude2, isCloseTo(expectedMagnitude2, 0.00001));
    }
    
    @Test
    public void sqrMagnitude() {
        // When
        double magnitude1 = vector1.sqrMagnitude();
        double expectedMagnitude1 = 30.78389021;
        double magnitude2 = vector2.sqrMagnitude();
        double expectedMagnitude2 = 167.959882;
    
        // Then
        assertThat(magnitude1, isCloseTo(expectedMagnitude1, 0.00001));
        assertThat(magnitude2, isCloseTo(expectedMagnitude2, 0.00001));
    }
    
    @Test
    public void magnitudeAndSqrMagnitudeMatch() {
        // When
        double magnitude1 = vector1.magnitude();
        double sqrMagnitude1 = vector1.sqrMagnitude();
        double magnitude2 = vector2.magnitude();
        double sqrMagnitude2 = vector2.sqrMagnitude();
    
        // Then
        assertThat(magnitude1 * magnitude1, isCloseTo(sqrMagnitude1, 0.00001));
        assertThat(magnitude2 * magnitude2, isCloseTo(sqrMagnitude2, 0.00001));
    }
    
    @Test
    public void sqrDistTo() {
        // When
        double distance = vector1.sqrDistTo(vector2);
        double expectedDistance = 176.886386411;
    
        // Then
        assertThat(distance, isCloseTo(expectedDistance, 0.00001));
    }
    
    @Test
    public void distanceAndSqrDistanceToMatch() {
        // When
        double distance1 = vector1.distance(vector2);
        double sqrDistanceTo1 = vector1.sqrDistTo(vector2);
    
        // Then
        assertThat(distance1 * distance1, isCloseTo(sqrDistanceTo1, 0.00001));
    }
    
    @Test
    public void normalised() {
        // When
        Vector2D normalised1 = vector1.normalised();
        Vector2D normalised2 = vector2.normalised();
        Vector2D expectedNormalised1 = new Vector2D(0.38481, 0.923);
        Vector2D expectedNormalised2 = new Vector2D(0.970761, -0.240048);
        
        // Then
        assertThat(normalised1, isCloseTo(expectedNormalised1, 0.00001));
        assertThat(normalised2, isCloseTo(expectedNormalised2, 0.00001));
        assertThat(normalised1.magnitude(), isCloseTo(1, 0.00001));
    }
    
    @Test
    public void toPoint() {
        // When
        Point point1 = vector1.toPoint();
        Point point2 = vector2.toPoint();
        Point expectedPoint1 = new Point(2, 5);
        Point expectedPoint2 = new Point(12, -4);
    
        // Then
        assertThat(point1, is(expectedPoint1));
        assertThat(point2, is(expectedPoint2));
    }
    
    @Test
    public void equals_withoutEpsilon() {
        // When
        boolean areEqual1 = vector1.equals(new Vector2D(vector1));
        boolean areEqual2 = vector2.equals(new Vector2D(vector2));
        boolean areEqual3 = vector1.equals(vector2);
        
        // Then
        assertThat(areEqual1, is(true));
        assertThat(areEqual2, is(true));
        assertThat(areEqual3, is(false));
    }
    
    @Test
    public void equals_withCorrectEpsilon() {
        // When
        boolean areEqual1 = vector1.equals(vector2, 1);
        boolean areEqual2 = vector1.equals(vector2, 10.6);
        
        // Then
        assertThat(areEqual1, is(false));
        assertThat(areEqual2, is(true));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void equals_incorrectEpsilon() {
        // When
        vector1.equals(vector2, -0.001);
    }
}