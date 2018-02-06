package com.knightlore.network.server;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.network.Connection;
import com.knightlore.network.NetworkObjectManager;

public class SendToClient implements Runnable {

    private Connection conn;
    private BlockingQueue<byte[]> commandQueue = new LinkedBlockingQueue<>();
    private UUID uuid;

    public SendToClient(Connection conn, UUID uuid) {
        this.conn = conn;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        NetworkObjectManager.getSingleton().registerClientSender(this);
        // Firstly, send the player's own state to inform them of their own identity.
        conn.send(NetworkObjectManager.getSingleton().getNetworkObject(uuid)
                .serialize(false));
        while (!conn.terminated) {
            byte[] nextState;
            try {
                nextState = commandQueue.take();
                conn.send(nextState);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        NetworkObjectManager.getSingleton().removeClientSender(this);
    }

    public void sendState(byte[] state) {
        commandQueue.offer(state);
    }

}
