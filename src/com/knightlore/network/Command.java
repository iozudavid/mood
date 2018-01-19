package com.knightlore.network;

import java.util.UUID;

/**
 * Represents an input command from a client that may alter the game state. To
 * be processed by the server.
 * 
 * @author Will
 */
public class Command {
    public UUID getClientUUID() {
        // TODO: extract UUID from command
        return null;
    }
}
