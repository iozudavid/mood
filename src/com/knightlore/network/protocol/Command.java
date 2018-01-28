package com.knightlore.network.protocol;

import java.util.Map;

import com.knightlore.game.Player;

/**
 * Represents an input command from a client that may alter the game state. To
 * be processed by the server.
 * 
 * @author Will
 */
public class Command {
    public long timeSent;
    private Map<ClientControl, Byte> inputs;

    public Command(Map<ClientControl, Byte> inputs, long timeSent) {
        this.timeSent = timeSent;
        this.inputs = inputs;
    }

    // Execute this command on the specified player.
    public void execute(Player player) {
        // TODO: check timestamp, only set state if recent enough?
        // Perform this in a thread to avoid the blocking of future commands.
        new Thread(() -> player.setInputState(inputs)).start();
    }
}
