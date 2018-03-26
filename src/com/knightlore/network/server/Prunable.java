package com.knightlore.network.server;

/**
 * Interface to be used for connection class.
 */
public interface Prunable {

    default void destroy() {
    }

    boolean exists();

}
