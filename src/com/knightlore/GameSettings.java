package com.knightlore;

public final class GameSettings {

    public static final boolean FULLSCREEN = !true;

    // TODO: FIXME here?
    static boolean client = false;
    static boolean server = false;

    public static boolean MOTION_BOB = true;

    private GameSettings() {
    }

    public static boolean isClient() {
        return client;
    }

    public static boolean isServer() {
        return server;
    }

}
