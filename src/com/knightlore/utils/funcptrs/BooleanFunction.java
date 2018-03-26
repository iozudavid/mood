package com.knightlore.utils.funcptrs;

/**
 * A functional interface, that takes some parameter and returns a boolean, used
 * in the GUI.
 * 
 * @author James
 *
 * @param <T>
 */
@FunctionalInterface
public interface BooleanFunction<T> {
    boolean check(T value);
}
