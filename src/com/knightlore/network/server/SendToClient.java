package com.knightlore.network.server;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.network.Connection;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.NetworkUtils;

public class SendToClient implements Runnable {

    private Connection conn;
    private BlockingQueue<ByteBuffer> commandQueue = new LinkedBlockingQueue<>();
    private UUID uuid;

    public SendToClient(Connection conn, UUID uuid) {
        this.conn = conn;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        ServerNetworkObjectManager manager = (ServerNetworkObjectManager) NetworkObjectManager
                .getSingleton();
        
        System.out.println("sending player identity" + System.currentTimeMillis());
        // Firstly, tell the player who they are.
        ByteBuffer buf = ByteBuffer
                .allocate(NetworkObject.BYTE_BUFFER_MAX_SIZE);
        NetworkUtils.putStringIntoBuf(buf,
                NetworkObjectManager.MANAGER_UUID.toString());
        NetworkUtils.putStringIntoBuf(buf, "registerPlayerIdentity");
        NetworkUtils.putStringIntoBuf(buf, uuid.toString());
        conn.send(buf);
        

        manager.registerClientSender(this);
        
        while (!conn.terminated) {
            ByteBuffer nextState;
            try {
                nextState = commandQueue.take();
                System.out.println("took new command to send");
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
