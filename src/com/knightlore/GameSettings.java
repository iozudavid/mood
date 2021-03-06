package com.knightlore;

/**
 * Data holder class for global game settings
 *
 * @author James
 */
public final class GameSettings {
    
    public static final boolean FULLSCREEN = false;
    public static int desiredBlockiness = 10;
    public static int actualBlockiness = desiredBlockiness;
    public static String playerName = "Client Player";
    public static boolean motionBob = true;
    public static int mapWidth = 16;
    public static int mapHeight = 16;
    public static int mapSeed = 40;
    public static boolean randomMap = true;
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
