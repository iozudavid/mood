package com.knightlore;

public final class GameSettings {

    public static final boolean FULLSCREEN = false;

    // TODO: FIXME here?
    static boolean client = false;
    static boolean server = false;

    public static final int DEFAULT_BLOCKINESS = 10;
    public static int desiredBlockiness = DEFAULT_BLOCKINESS;
    public static int actualBlockiness = desiredBlockiness;

    // FIXME: enter dynamic player names in the GUI.
    public static String PLAYER_NAME = "Client Player";

    public static boolean MOTION_BOB = true;

    public static int mapWidth = 16;

    public static int mapHeight = 16;

    public static int mapSeed = 40;

    public static boolean randomMap = true;

    private GameSettings() {
    }

    public static boolean isClient() {
        return client;
    }

    public static boolean isServer() {
        return server;
    }

}
