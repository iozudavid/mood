package com.knightlore.network.server;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.Connection;

/**
 * Thread which sends packets about game state to the client.
 *
 * @author David Iozu
 */
public class SendToClient implements Runnable {
    
    private final Connection conn;
    private final BlockingQueue<ByteBuffer> commandQueue = new LinkedBlockingQueue<>();
    private final UUID uuid;
    private final ServerNetworkObjectManager manager;
    
    public SendToClient(Connection conn) {
        this.conn = conn;
        this.manager = (ServerNetworkObjectManager)GameEngine.getSingleton().getNetworkObjectManager();
        System.out.println("registering client sender");
        this.uuid = manager.registerClientSender(this);
    }
    
    /**
     * Continuously checking if there are any packets to be sent to the client.
     * If there are send them.
     */
    @Override
    public void run() {
        
        while (!conn.getTerminated()) {
            ByteBuffer nextState;
            try {
                nextState = commandQueue.take();
                conn.send(nextState);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
        manager.removeClientSender(this);
    }
    
    /**
     * Add a new packet to the list of packets to be sent.
     *
     * @param data - packet to be sent to the user.
     */
    public void send(ByteBuffer data) {
        commandQueue.offer(data);
    }
    
    /**
     * @return the UUID of the client which is on the other side of the given
     * connection.
     */
    public UUID getUUID() {
        return this.uuid;
    }
    
}
