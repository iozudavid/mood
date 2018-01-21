package com.knightlore.network;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.InetAddress;
import java.util.Map;

/**
 * A network connection manager that runs server-side and deals with all
 * connections to clients.
 * 
 * @author Will
 */
public class Manager implements Runnable {
    //ip-connection map
    private ConcurrentHashMap<InetAddress, Connection> connections;
    // A queue of commands to be processed, received from clients. This queue is
    // populated by clients' Connection objects.
    private LinkedBlockingQueue<Command> commandQueue;

    @Override
    public void run() {
        Thread pruner = new Thread(new Pruner(connections));
        pruner.start();
        
        // DEBUG
        //Connection testConn = new Connection(UUID.randomUUID(), commandQueue);
        
        while (true) {
            // TODO: listen for connections, create and start a Connection
            // object for each
            //infinite loop to listen for incoming loop
            //here will be the verification of the convention for 4 bytes for example
            //then create an entry in map
            //and also start a new connection thread
        }
    }

    
    public static void main(String[] args) {
        (new Manager()).run();
    }

}