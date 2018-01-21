package com.knightlore.network;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class Connection implements Runnable {
    // Wait 5 seconds without receiving packets before disconnecting.
    protected static int TIMEOUT_MILLIS = 5 * 1000;

    protected Queue<Command> commandQueue;
    public volatile boolean terminated;

    public Connection(Queue<Command> commandQueue) {
        this.terminated = false;
        this.commandQueue = commandQueue;
    }

    public boolean getTerminated() {
        return terminated;
    }

    public abstract void send(byte[] data);

    public abstract byte[] receive();

    @Override
    public void run() {
        // Use Future to execute a blocking operation (receiving a packet) with
        // a timeout. This run() method will return upon a timeout,
        // terminating the thread.
        Callable<byte[]> task = new Callable<byte[]>() {
            public byte[] call() {
                return Connection.this.receive();
            }
        };
        Future<byte[]> future = Executors.newCachedThreadPool().submit(task);

        while (true) {
            try {
                byte[] receivedData = future.get(TIMEOUT_MILLIS,
                        TimeUnit.MILLISECONDS);
            } catch (TimeoutException ex) {
                System.err.println(
                        "Connection timed out while waiting for a packet.");
                this.terminated = true;
                return;
            } catch (InterruptedException e) {
                System.err.println(
                        "InterruptedException while waiting to receive packet:");
                e.printStackTrace();
                this.terminated = true;
                return;
            } catch (ExecutionException e) {
                System.err.println(
                        "ExecutionException while waiting to receive packet:");
                e.printStackTrace();
                this.terminated = true;
                return;
            }
            // TODO: process the received data, and add a command to the queue
            // if necessary.
        }
    }
}
