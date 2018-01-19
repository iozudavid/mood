package com.knightlore.network;

import java.util.Date;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Represents a connection from the server to a particular client, identified by
 * a UUID.
 * 
 * @author Will
 */
public class Connection implements Runnable {
    // Wait 5 seconds without receiving packets before disconnecting.
    private static int TIMEOUT_MILLIS = 5 * 1000;

    private UUID clientUUID;
    // Whether this connection has been closed.
    private boolean terminated;
    // The time-stamp of when the most recent communication (packet) was
    // received.
    private Date lastPacketDate;
    private Queue<Command> commandQueue;

    public Connection(UUID clientUUID, Queue<Command> commandQueue) {
        this.clientUUID = clientUUID;
        this.commandQueue = commandQueue;
        this.terminated = false;
    }

    /**
     * Send data to the other party.
     * 
     * @param data
     */
    public void send(byte[] data) {
        // TODO
    }

    /**
     * Receive data from the other party. This is a blocking operation, so the
     * method will not return until a packet is received.
     */
    public byte[] receive() {
        // TODO
        return null;
    }

    public boolean getTerminated() {
        return terminated;
    }

    public Date getLastPacketDate() {
        return lastPacketDate;
    }

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
                System.err.println("Connection " + this.clientUUID
                        + " timed out while waiting for a packet.");
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
            } finally {
                future.cancel(true); // may or may not desire this
            }

            byte[] receivedData = receive();
            // TODO: process the received data, and add a command to the queue
            // if necessary.
        }
    }
}
