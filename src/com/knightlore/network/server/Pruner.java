package com.knightlore.network.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.knightlore.network.Connection;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.utils.Tuple;

/**
 * Constantly checks for inactive client connections, and removes them from the
 * list.
 * 
 * @author Will
 */
public class Pruner implements Runnable {
    // The time to wait, in milliseconds, between checks for terminated threads.
    private static int PERIOD_MILLIS = 1000;
    private Map<UUID, Tuple<Connection, NetworkObject>> conns;

    public Pruner(Map<UUID, Tuple<Connection, NetworkObject>> conns) {
        this.conns = conns;
    }

    public void run() {
        while (true) {
            Iterator<Entry<UUID, Tuple<Connection, NetworkObject>>> i = conns.entrySet().iterator();
            Entry<UUID, Tuple<Connection, NetworkObject>> next = null;
            while (i.hasNext()) {
                next = i.next();
                if (next.getValue().x.getTerminated()) {
                    // if connection lost then remove the network object
                    // remove from connection list
                    // and inform other clients
                    NetworkObjectManager.getSingleton().disconnectClient(next.getKey());
                    next.getValue().y.destroy();
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
