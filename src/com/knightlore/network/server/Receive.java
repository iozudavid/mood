package com.knightlore.network.server;

import java.nio.ByteBuffer;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.Connection;

/**
 * Thread listening for new packets received by networking.
 * @author David Iozu, Will Miller
 */
public class Receive implements Runnable {
    private final Connection conn;

    public Receive(Connection conn) {
        this.conn = conn;
    }

    /**
     * Checks if a new packet was sent via networking and pass it to network
     * manager to be processed.
     */
    @Override
    public void run() {
        ByteBuffer buf;
        try {
            while ((buf = conn.receive()) != null) {
                GameEngine.getSingleton().getNetworkObjectManager().processMessage(buf);
            }
        } catch (Exception e) {
            System.err.println("Error while receiving packet: ");
            e.printStackTrace();
        }

        System.out.println("Got null packet, exiting");
        conn.terminated = true;
    }

}
