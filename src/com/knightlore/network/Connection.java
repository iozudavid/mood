package com.knightlore.network;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Connection implements Runnable {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    // Wait 5 seconds without receiving packets before disconnecting.
    protected static int TIMEOUT_MILLIS = 500 * 1000;
    // protected static int TIMEOUT_MILLIS = 30 * 1000;

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
        while (true) {
            ByteBuffer packet = Connection.this.receiveBlocking();
            if (packet == null) {
                this.terminated = true;
                System.err.println("Connection terminated.");
                break;
            }
            System.out.println("received buffer of size " + packet.remaining());
            try {
                packets.put(packet);
            } catch (InterruptedException e) {
                this.terminated = true;
                System.err
                        .println("Thread interrupted while receiving packet: ");
                break;
            }
        }
    }
}
