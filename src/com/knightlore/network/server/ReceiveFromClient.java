package com.knightlore.network.server;

import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.network.Command;
import com.knightlore.network.Connection;

public class ReceiveFromClient implements Runnable {
    private Connection conn;
    private LinkedBlockingQueue<Command> commandQueue;

    public ReceiveFromClient(Connection conn,
            LinkedBlockingQueue<Command> commandQueue) {
        this.conn = conn;
        this.commandQueue = commandQueue;
    }

    @Override
    public void run() {
        byte[] packet;
        while ((packet = conn.receiveBlocking()) != null) {
            System.out.println("Received: " + new String(packet, Connection.CHARSET));
//            Command c = null;
//            try {
//                c = commandQueue.take();
//            	 
//            } catch (InterruptedException e) {
//                System.err.println(
//                        "Interruption while waiting to process a command:");
//                e.printStackTrace();
//            }
            // TODO: update game state based on command, send state delta to
            // clients??
        	
        	
      
        	
        }

        System.out.println("Got null packet, exiting");
    }

}
