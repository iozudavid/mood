package com.knightlore.network;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.network.server.Prunable;

/**
 * Abstraction of networking connection which implements timeout on socket
 * 
 * @author David Iozu, Will Miller
 *
 */
public abstract class Connection implements Runnable, Prunable {
    /**
     *  Wait 5 seconds without receiving packets before disconnecting.
     */
    protected static final int TIMEOUT_MILLIS = 500 * 1000;

    public volatile boolean terminated;
    // Stores the most recently received packet.
    private final BlockingQueue<ByteBuffer> packets;

    public Connection() {
        this.terminated = false;
        this.packets = new LinkedBlockingQueue<>();
    }

    /**
     * 
     * @return whether the connection is still alive or not.
     */
    public synchronized boolean getTerminated() {
        return terminated;
    }

    /**
     * Abstraction of sending the packet. Will be different depending on the
     * chosen protocol
     * 
     * @param data
     *            - packet to be sent
     */
    public abstract void send(ByteBuffer data);

    /**
     * Should block while waiting until a packet is available, and return it.
     * @return a packet when one is available.
     */
    public abstract ByteBuffer receiveBlocking();

    /**
     * Wrapper for receiveBlocking() that implements a timeout.
     * 
     * @return a packet when one is available.
     * @throws Exception
     *             when receive a wrong packet (malformed).
     */
    public ByteBuffer receive() throws Exception {
        return this.packets.take();
    }

    /**
     * Receive packets and put them into the blocking queue, ready to be taken.
     */
    @Override
    public void run() {
        while (!this.terminated) {
            ByteBuffer packet = Connection.this.receiveBlocking();
            if (packet == null) {
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

    /**
     * 
     * @return whether the connection is still alive or not.
     */
    @Override
    public boolean exists() {
        return terminated;
    }

}
