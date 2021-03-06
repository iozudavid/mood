package com.knightlore.utils.funcptrs;

/**
 * A functional interface, used in the GUI as a function pointer to a void
 * method.
 *
 * @author James
 */
@FunctionalInterface
public interface VoidFunction {
    void call();
}