package com.knightlore.utils.funcptrs;

@FunctionalInterface
public interface BooleanFunction<T> {
    boolean check(T value);
}
