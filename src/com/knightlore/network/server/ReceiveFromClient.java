package com.knightlore.network.server;

import java.util.List;
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
        while (true) {
            Command c = null;
            try {
                c = commandQueue.take();
            } catch (InterruptedException e) {
                System.err.println(
                        "Interruption while waiting to process a command:");
                e.printStackTrace();
            }
            // TODO: update game state based on command, send state delta to
            // clients??
        }
    }

}
