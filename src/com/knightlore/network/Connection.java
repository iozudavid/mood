package com.knightlore.network;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class Connection {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    // Wait 5 seconds without receiving packets before disconnecting.
    protected static int TIMEOUT_MILLIS = 5 * 1000;

    protected BlockingQueue<Command> commandQueue;
    public volatile boolean terminated;
    protected UUID clientID;

    public Connection(BlockingQueue<Command> commandQueue, UUID clientID) {
        this.terminated = false;
        this.commandQueue = commandQueue;
        this.clientID = clientID;
    }

    public synchronized boolean getTerminated() {
        return terminated;
    }

    public abstract void send(byte[] data);

    // Should block while waiting until a packet is available, and return it.
    public abstract byte[] receiveBlocking();

    // Wrapper for receiveBlocking() that implements a timeout.
    public byte[] receive() throws Exception {
        // Use Future to execute a blocking operation (receiving a packet) with
        // a timeout. This run() method will return upon a timeout,
        // terminating the thread.
        Callable<byte[]> task = new Callable<byte[]>() {
            public byte[] call() {
                return (Connection.this.receiveBlocking());
            }
        };
        Future<byte[]> future = Executors.newCachedThreadPool().submit(task);
        try {
            return future.get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            System.err.println(
                    "Connection timed out while waiting for a packet.");
            this.terminated = true;
        } catch (InterruptedException | ExecutionException ex) {
            System.err.println(
                    "Unexpected exception while waiting to receive packet:");
        }
        return null;
    }
    
    public void addToCommandQueue(Command newCommand) {
        commandQueue.offer(newCommand);
    }
    
	public Command takeNextCommand() {

		while (true) {
			try {
				return (commandQueue.take());
			} catch (InterruptedException e) {
				System.err.println("Thread interrupted wile waiting for the next command");
			}
		}
	}

}
