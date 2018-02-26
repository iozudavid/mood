package com.knightlore.network.server;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.Connection;

public class SendToClient implements Runnable {

    private Connection conn;
    private BlockingQueue<ByteBuffer> commandQueue = new LinkedBlockingQueue<>();
    private UUID uuid;
    private ServerNetworkObjectManager manager;

    public SendToClient(Connection conn) {
        this.conn = conn;
        this.manager = (ServerNetworkObjectManager) GameEngine.getSingleton().getNetworkObjectManager();
        this.uuid = manager.registerClientSender(this);
    }

    @Override
    public void run() {

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

    public UUID getUUID() {
        return this.uuid;
    }

}
