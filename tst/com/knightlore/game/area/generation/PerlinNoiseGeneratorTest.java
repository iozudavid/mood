package com.knightlore.game.area.generation;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PerlinNoiseGeneratorTest {
    
    @Test
    public void createPerlinNoise() {
        // Given
        long seed = 835123908412L;
        
        // When
        PerlinNoiseGenerator generator = new PerlinNoiseGenerator(5, 5, seed);
        double[][] perlinNoise = generator.createPerlinNoise();
        
        double[][] properPerlinNoise = {
                {2.158474566649031, 1.8417896054879956, 2.1328604984152264, 2.30989242804799, 1.919777939329654},
                {1.9304948806862672, 1.9707168568656674, 2.00188882205037, 1.8446101735499183, 1.8058247339785019},
                {1.7623605662233024, 1.988576452188283, 2.0216189668157956, 1.7306713253695771, 2.0353210929764316},
                {1.946054235344647, 2.156059331448846, 1.8394298613439477, 2.0511512234648634, 2.166549762560484},
                {2.204283577968968, 2.235599622770705, 2.0760921619404593, 1.9152521054157425, 1.7654401009552783}
        };
        
        // Then
        assertThat(perlinNoise, is(properPerlinNoise));
    }
}