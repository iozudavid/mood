package com.knightlore.network.protocol;

import java.util.Map;

/**
 * Represents an input command from a client that may alter the game state. To
 * be processed by the server.
 * 
 * @author Will
 */
public class Command {
    public long timeSent;
    public Map<ClientControl, Byte> inputs;

    public Command(Map<ClientControl, Byte> inputs, long timeSent) {
        this.timeSent = timeSent;
        this.inputs = inputs;
    }
}
