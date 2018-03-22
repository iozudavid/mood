package com.knightlore.network;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.network.server.Prunable;

public abstract class Connection implements Runnable, Prunable {
    // Wait 5 seconds without receiving packets before disconnecting.
    protected static int TIMEOUT_MILLIS = 500 * 1000;

    public volatile boolean terminated;
    // Stores the most recently received packet.
    private BlockingQueue<ByteBuffer> packets;

    public Connection() {
        this.terminated = false;
        this.packets = new LinkedBlockingQueue<>();
    }

    public synchronized boolean getTerminated() {
        return terminated;
    }

    public abstract void send(ByteBuffer data);

    // Should block while waiting until a packet is available, and return it.
    public abstract ByteBuffer receiveBlocking();

    // Wrapper for receiveBlocking() that implements a timeout.
    public ByteBuffer receive() throws Exception {
        return this.packets.take();
    }

    @Override
    public void run() {
        // Receive packets and put them into the blocking queue, ready to be
        // taken.
        while (!this.terminated) {
            ByteBuffer packet = Connection.this.receiveBlocking();
            if (packet == null) {
                // Indicates that the connection has ended.
                this.terminated = true;
                continue;
            }
            try {
                packets.put(packet);
            } catch (InterruptedException e) {
                this.terminated = true;
                System.err
                        .println("Thread interrupted while receiving packet: ");
            }
        }
    }

    @Override
    public boolean exists() {
        return terminated;
    }

}
