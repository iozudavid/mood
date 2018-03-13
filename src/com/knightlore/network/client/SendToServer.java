package com.knightlore.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.input.InputManager;
import com.knightlore.game.Player;
import com.knightlore.network.Connection;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.protocol.ClientController;
import com.knightlore.network.protocol.ClientProtocol;
import com.knightlore.network.protocol.NetworkUtils;

public class SendToServer implements Runnable {
    // How many times PER SECOND to check for new state, and send it if
    // necessary.
    private static final int UPDATE_TICK_FREQ = 100;
    // How often to send an update of controls, rather than just if the
    // control state has changed. A value of 100 means that in every 100 ticks,
    // at *least* one update will be sent.
    private static final int REGULAR_UPDATE_FREQ = 100;

    private Connection conn;
    private BufferedReader user;
    private ByteBuffer lastState;
    // private byte[] currentState;
    // private Object lock;
    private int updateCounter = 1;

    private ByteBuffer currentState;
    private ClientNetworkObjectManager manager;
    private UUID myUUID;

    public SendToServer(Connection conn) {
        this.conn = conn;
        // this.lock = new Object();
        this.manager = (ClientNetworkObjectManager) GameEngine.getSingleton().getNetworkObjectManager();

    }

    private synchronized ByteBuffer getCurrentControlState() {
        // ByteBuffer to store current input state.
        ByteBuffer buf = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
        // Let the server know which UUID this relates to.
        NetworkUtils.putStringIntoBuf(buf, myUUID.toString());
        // Call the setInputState method on our player.
        NetworkUtils.putStringIntoBuf(buf, "setInputState");
        for (int i = 0; i < ClientProtocol.getIndexActionMap().size(); i++) {
            // Encode the current control as an integer.
            buf.putInt(i);
            // taking the current control
            ClientController currentControl = null;
            int keyCode;
            try {
                currentControl = ClientProtocol.getByIndex(i);
                keyCode = ClientController.getKeyCode(currentControl);
            } catch (IOException e) {
                System.err.println("ClientControl index out of range");
                return null;
            }
            if (InputManager.isKeyDown(keyCode)) {
                buf.put((byte) 1);
            } else {
                buf.put((byte) 0);
            }
        }
        return buf;
    }

    // this should be use to close this thread
    // best way to do this as the thread will be blocked listening for the next
    // string
    public void closeStream() {
        try {
            this.user = new BufferedReader(new InputStreamReader(System.in));
            this.user.close();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Unexpected error " + e.getMessage());
            System.exit(1);
        }
    }

    public void tick() {
        // Send a controls update if either the controls have changed or
        // a regular update is due.
        synchronized (this.currentState) {
        	ByteBuffer message = this.manager.takeNextMessageToSend();
        	if(message!=null){
        		conn.send(message);
        	}
            if (updateCounter++ >= REGULAR_UPDATE_FREQ || !Arrays.equals(currentState.array(), lastState.array())) {
                updateCounter = 1;
                conn.send(currentState);
                this.lastState = currentState;
            }
        }
    }

    @Override
    public void run() {
        double freq = (UPDATE_TICK_FREQ / 1000d);
        long delay = (long) (1 / freq);

        Player player;
        while ((player = manager.getMyPlayer()) == null)
            // Wait for UUID to be set.
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        this.myUUID = player.getObjectId();
        
        this.currentState = getCurrentControlState();
        this.lastState = this.currentState;

        while (!conn.terminated) {
            synchronized (this.currentState) {
                this.currentState = getCurrentControlState();
            }
            this.tick();
            try {
                // To milliseconds
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
