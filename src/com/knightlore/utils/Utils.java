package com.knightlore.utils;

public class Utils {
    
    /**
     * Quick clamp function, used in the config parser for bounding values. min
     * <= returned value <= max
     * <p>
     * Note: this method assumes min <= max. Otherwise it will not work properly
     *
     * @param val - the value to be clamped
     * @param min -the lower bound to clamp to
     * @param max - the upper bound to clamp tp
     * @returns the value provided limited to within the bounds.
     */
    public static int clamp(int val, int min, int max) {
        if (val < min) {
            return min;
        }
        if (val > max) {
            return max;
        }
        return val;
    }
    
}
