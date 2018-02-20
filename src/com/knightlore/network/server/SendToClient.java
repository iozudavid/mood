package com.knightlore.network.server;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.network.Connection;
import com.knightlore.network.NetworkObjectManager;

public class SendToClient implements Runnable {

    private Connection conn;
    private BlockingQueue<ByteBuffer> commandQueue = new LinkedBlockingQueue<>();
    
    public UUID uuid;

    public SendToClient(Connection conn, UUID uuid) {
        this.conn = conn;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        ServerNetworkObjectManager manager = (ServerNetworkObjectManager) NetworkObjectManager
                .getSingleton();
        

        

        manager.registerClientSender(this);
        
        while (!conn.terminated) {
            ByteBuffer nextState;
            try {
                nextState = commandQueue.take();
                conn.send(nextState);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        manager.removeClientSender(this);
    }

    public void send(ByteBuffer data) {
        commandQueue.offer(data);
    }

}
