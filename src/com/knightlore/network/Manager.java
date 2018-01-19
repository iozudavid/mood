package com.knightlore.network;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A network connection manager that runs server-side and deals with all
 * connections to clients.
 * 
 * @author Will
 */
public class Manager implements Runnable {
    // All objects representing client connections.
    private CopyOnWriteArrayList<Connection> conns;
    // A queue of commands to be processed, received from clients. This queue is
    // populated by clients' Connection objects.
    private LinkedBlockingQueue<Command> commandQueue;

    @Override
    public void run() {
        Thread pruner = new Thread(new Pruner(conns));
        pruner.start();
        
        // DEBUG
        Connection testConn = new Connection(UUID.randomUUID(), commandQueue);
        
        while (true) {
            // TODO: listen for connections, create and start a Connection
            // object for each
        }
    }

}