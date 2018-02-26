package com.knightlore.network.server;

import java.nio.ByteBuffer;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.Connection;

public class Receive implements Runnable {
    private Connection conn;

    public Receive(Connection conn) {
        this.conn = conn;
    }

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
