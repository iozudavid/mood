package com.knightlore.network;

import java.util.UUID;

import com.knightlore.utils.Vector2D;

/**
 * Represents an input command from a client that may alter the game state. To
 * be processed by the server.
 * 
 * @author Will
 */
public class Command {
    public Vector2D pos;

    public UUID getClientUUID() {
        // TODO: extract UUID from command
        return null;
    }

    public void setNewPos(Vector2D vec) {
        pos = vec;
    }
}
