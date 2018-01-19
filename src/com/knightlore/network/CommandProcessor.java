package com.knightlore.network;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandProcessor implements Runnable {
    private List<Connection> conns;
    private LinkedBlockingQueue<Command> commandQueue;

    public CommandProcessor(List<Connection> conns,
            LinkedBlockingQueue<Command> commandQueue) {
        this.conns = conns;
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
