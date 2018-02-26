package com.knightlore.game.area.generation;

import com.knightlore.utils.Vector2D;

import java.util.Random;

public class PerlinNoiseGenerator {
    private final double[][] perlinNoise;
    private final Vector2D[][] gradVectors;
    private final Random rand;

    public PerlinNoiseGenerator(int width, int height, long seed) {
        perlinNoise = new double[width][height];
        gradVectors = new Vector2D[width + 1][height + 1];
        rand = new Random(seed);
    }

    public double[][] createPerlinNoise() {
        for (int i = 0; i < gradVectors.length; i++) {
            for (int j = 0; j < gradVectors[0].length; j++) {
                double r1 = rand.nextDouble();
                double r2 = rand.nextDouble();
                gradVectors[i][j] = new Vector2D(r1, r2);
            }
        }

        // perform multiple times
        // taking a biased average on each increment
        for (int k = 0; k < 5; k++) {
            for (int i = 0; i < perlinNoise.length; i++) {
                for (int j = 0; j < perlinNoise[0].length; j++) {
                    double x = i + rand.nextDouble();
                    double y = j + rand.nextDouble();
                    if (k == 0) {
                        perlinNoise[i][j] = generatePerlinValue(x, y);
                    } else {
                        perlinNoise[i][j] = (perlinNoise[i][j] + generatePerlinValue(x, y)) / 2;
                    }
                }
            }
        }

        return perlinNoise;
    }

    private double generatePerlinValue(double x, double y) {
        double xm = x % 1; // map to coordinates in unit square
        double ym = y % 1;

        int x0 = (int) (x - xm); // get coordinates of square points surrounding
        int y0 = (int) (y - ym); // x any y
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        Vector2D gradientVector1 = gradVectors[x0][y1];
        Vector2D gradientVector2 = gradVectors[x1][y1];
        Vector2D gradientVector3 = gradVectors[x0][y0];
        Vector2D gradientVector4 = gradVectors[x1][y0];

        Vector2D distanceVector1 = new Vector2D(xm, ym - 1);
        Vector2D distanceVector2 = new Vector2D(xm - 1, ym - 1);
        Vector2D distanceVector3 = new Vector2D(xm, ym);
        Vector2D distanceVector4 = new Vector2D(xm - 1, ym);

        // dot product gradient vectors with corresponding
        // distance vectors, generating the influence values
        double influenceValue1 = gradientVector1.dot(distanceVector1);
        double influenceValue2 = gradientVector2.dot(distanceVector2);
        double influenceValue3 = gradientVector3.dot(distanceVector3);
        double influenceValue4 = gradientVector4.dot(distanceVector4);

        x = applyFade(xm);
        y = applyFade(ym);

        // return "interpolation" of influence vectors
        double interpolation12 = (influenceValue1 + influenceValue2) / 2;
        double interpolation34 = (influenceValue3 + influenceValue4) / 2;
        return 1 + (interpolation12 + interpolation34) / 2;
    }

    private double applyFade(double t) {
        // 6t^5 - 15t^4 + 10t^3
        return 6 * (t * t * t * t * t) - 15 * (t * t * t * t) + 10 * (t * t * t);
    }
}
