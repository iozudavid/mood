package com.knightlore.network;

import java.util.Iterator;
import java.util.List;

/**
 * Constantly checks for inactive client connections, and removes them from the
 * list.
 * 
 * @author Will
 */
public class Pruner implements Runnable {
    // The time to wait, in milliseconds, between checks for terminated threads.
    private static int PERIOD_MILLIS = 1000;
    private List<Connection> conns;

    public Pruner(List<Connection> conns) {
        this.conns = conns;
    }

    public void run() {
        while (true) {
            Iterator<Connection> i = conns.iterator();
            while (i.hasNext())
                if (i.next().getTerminated())
                    i.remove();
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
