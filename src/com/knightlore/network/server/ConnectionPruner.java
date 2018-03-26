package com.knightlore.network.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.Connection;

/**
 * Constantly checks for inactive client connections, and removes them from the
 * list.
 *
 * @author Will Miller
 */
public class ConnectionPruner implements Runnable {
    // The time to wait, in milliseconds, between checks for terminated threads.
    private static final int PERIOD_MILLIS = 1000;
    private final Map<UUID, Connection> conns;

    public ConnectionPruner(Map<UUID, Connection> conns) {
        this.conns = conns;
    }
    
    /**
     * If a connection has been lost remove the client and its player and inform
     * other clients
     */
    public void run() {
        while (true) {
            Iterator<Entry<UUID, Connection>> i = conns.entrySet().iterator();
            Entry<UUID, Connection> next;
            while (i.hasNext()) {
                next = i.next();
                UUID uuid = next.getKey();
                Connection conn = next.getValue();
                if (conn.getTerminated()) {
                    // if connection lost then remove the network object
                    // remove from connection list
                    // and inform other clients
                    GameEngine.getSingleton().getNetworkObjectManager().getNetworkObject(uuid).destroy();
                    i.remove();
                }
            }
            try {
                // Reduce CPU usage by only checking periodically.
                Thread.sleep(PERIOD_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
