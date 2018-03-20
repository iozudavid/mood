package com.knightlore.network.server;

public interface Prunable {

    default void destroy() {
    }

    boolean exists();

}
