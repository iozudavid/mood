package com.knightlore.network.server;

import java.util.concurrent.BlockingQueue;

import com.knightlore.network.Connection;
import com.knightlore.network.NetworkObjectManager;

public class SendToClient implements Runnable {
    
    private Connection conn;
    private BlockingQueue<byte[]> commandQueue;

    public SendToClient(Connection conn) {
        this.conn = conn;

        NetworkObjectManager.getSingleton().registerClientSender(this);
    }

    @Override
    public void run() {

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
