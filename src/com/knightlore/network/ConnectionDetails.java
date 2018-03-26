package com.knightlore.network;

/**
 * Details of important data we need to complete before connecting to a game.
 */
public class ConnectionDetails {
    
    /**
     * Default port run multiplayer game on.
     */
    public static int DEFAULT_PORT = 27001;
    /**
     * Actual port we want to connect for a multiplayer game.
     * Initialized as Default port(27001) and may be changed or not.
     */
    public static int PORT = DEFAULT_PORT;
    /**
     * Server name initialized as localhost as a start.
     * May be changed to an IP address we want to connect.
     */
    public static String SERVER_HOSTNAME = "localhost";
    
}
