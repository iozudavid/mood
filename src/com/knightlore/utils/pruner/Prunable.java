package com.knightlore.utils.pruner;

public interface Prunable {

    public default void destroy() {
    }

    public boolean exists();

}
