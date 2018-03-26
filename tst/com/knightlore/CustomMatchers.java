package com.knightlore;

import com.knightlore.utils.Vector2D;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class CustomMatchers {
    public static Matcher<Vector2D> isCloseTo(final Vector2D v, final double epsilon) {
        return new BaseMatcher<Vector2D>() {
            @Override
            public boolean matches(final Object item) {
                final Vector2D vector = (Vector2D)item;
                return v.equals(vector, epsilon);
            }
            
            @Override
            public void describeTo(final Description description) {
                description.appendText(v.toString()).appendValue(" with possible difference ").appendValue(epsilon);
            }
        };
    }
    
    public static Matcher<Double> isCloseTo(final double d, final double epsilon) {
        return new BaseMatcher<Double>() {
            @Override
            public boolean matches(final Object item) {
                double value = (double)item;
                return d > value - epsilon && d < value + epsilon;
            }
            
            @Override
            public void describeTo(final Description description) {
                description.appendText(String.valueOf(d)).appendValue(" with possible difference ").appendValue(epsilon);
            }
        };
    }
}
