package com.knightlore;

public final class GameSettings {

    public static final boolean FULLSCREEN;

    static {
        FULLSCREEN = false;
    }

    // TODO: FIXME here?
    static boolean client = false;
    static boolean server = false;

    // FIXME: enter dynamic player names in the GUI.
    public static String PLAYER_NAME = "Client Player";

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
