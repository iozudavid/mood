package com.knightlore;

public final class GameSettings {

    public static final boolean FULLSCREEN = !true;

    // TODO: FIXME here?
    static boolean client = false;
    static boolean server = false;

    private GameSettings() {
    }

    public static boolean isClient() {
        return client;
    }

    public static boolean isServer() {
        return server;
    }

}
