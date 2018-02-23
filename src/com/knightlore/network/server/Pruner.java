package com.knightlore.network.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.knightlore.network.Connection;
import com.knightlore.network.NetworkObjectManager;

/**
 * Constantly checks for inactive client connections, and removes them from the
 * list.
 * 
 * @author Will
 */
public class Pruner implements Runnable {
    // The time to wait, in milliseconds, between checks for terminated threads.
    private static int PERIOD_MILLIS = 1000;
    private Map<UUID, Connection> conns;

    public Pruner(Map<UUID, Connection> conns) {
        this.conns = conns;
    }

    public void run() {
        while (true) {
            Iterator<Entry<UUID, Connection>> i = conns.entrySet().iterator();
            Entry<UUID, Connection> next = null;
            while (i.hasNext()){
                next = i.next();
                UUID uuid = next.getKey();
                Connection conn = next.getValue();
                if (conn.getTerminated()){
                    //if connection lost then remove the network object
                    //remove from connection list
                    //and inform other clients
                    NetworkObjectManager.getSingleton().getNetworkObject(uuid).destroy();
                    i.remove();
                }
            }
            try {
                // Reduce CPU usage by only checking periodically.
                Thread.sleep(PERIOD_MILLIS);
            } catch (InterruptedException e) {
                System.err.println("Pruner interrupted during sleep:");
                e.printStackTrace();
            }
        }
    }
}
