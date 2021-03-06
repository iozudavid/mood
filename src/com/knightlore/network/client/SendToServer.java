package com.knightlore.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.input.InputManager;
import com.knightlore.game.entity.Player;
import com.knightlore.network.Connection;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.protocol.ClientController;
import com.knightlore.network.protocol.ClientProtocol;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.render.GameFeed;

/**
 * Class which keep sending client updates to server
 *
 * @author David Iozu, Will Miller
 */
public class SendToServer implements Runnable {
    // How many times PER SECOND to check for new state, and send it if
    // necessary.
    private static final int UPDATE_TICK_FREQ = 100;
    // How often to send an update of controls, rather than just if the
    // control state has changed. A value of 100 means that in every 100 ticks,
    // at *least* one update will be sent.
    private static final int REGULAR_UPDATE_FREQ = 100;
    
    private final Connection conn;
    private final ClientNetworkObjectManager manager;
    private final Prediction prediction;
    /**
     * Send a ByteBuffer to the server.
     *
     * @param buf:
     * The buffer to be sent.
     */
    int debug = 0;
    private BufferedReader user;
    private ByteBuffer lastState;
    // private byte[] currentState;
    // private Object lock;
    private int updateCounter = 1;
    private ByteBuffer currentState;
    private UUID myUUID;
    private int lastPosition = 0;
    private long packetNumber = 0;
    
    public SendToServer(Connection conn) {
        this.conn = conn;
        this.manager = (ClientNetworkObjectManager)GameEngine.getSingleton()
                .getNetworkObjectManager();
        this.prediction = new Prediction();
    }
    
    /**
     * Get the current ByteBuffer of all inputs performed by current client.
     *
     * @param time - packet number to be sent
     * @return a ByteBuffer containing player's actual state
     */
    private synchronized ByteBuffer getCurrentControlState(double time) {
        // ByteBuffer to store current input state.
        ByteBuffer buf = ByteBuffer
                .allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
        // Let the server know which UUID this relates to.
        NetworkUtils.putStringIntoBuf(buf, myUUID.toString());
        // Call the setInputState method on our player.
        NetworkUtils.putStringIntoBuf(buf, "setInputState");
        buf.putDouble(time);
        for (int i = 0; i < ClientProtocol.getIndexActionMap().size(); i++) {
            // Encode the current control as an integer.
            buf.putInt(i);
            // taking the current control
            ClientController currentControl;
            int keyCode;
            try {
                currentControl = ClientProtocol.getByIndex(i);
                keyCode = ClientController.getKeyCode(currentControl);
            } catch (IOException e) {
                System.err.println("ClientControl index out of range");
                return null;
            }
            if (InputManager.isKeyDown(keyCode)
                    && !InputManager.getKeyboard().isTyping()) {
                buf.put((byte)1);
            } else {
                buf.put((byte)0);
            }
        }
        this.lastPosition = buf.position();
        return buf;
    }
    
    /**
     * Closes the stream as the buffer is blocked waiting for a packet to arrive.
     */
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
    
    /**
     * Gets the player's states stored on server and use them in prediction
     * class. Send new inputs to the server.
     */
    public void tick() {
        // Send a controls update if either the controls have changed or
        // a regular update is due.
        
        synchronized (this.currentState) {
            ByteBuffer nextMessage = this.manager.takeNextMessageToSend();
            if (nextMessage != null) {
                this.send(nextMessage);
            }
            ArrayList<ByteBuffer> lastStates = this.manager
                    .getPlayerStateOnServer();
            if (!lastStates.isEmpty()) {
                for (ByteBuffer b : lastStates) {
                    this.prediction.onServerFrame(this.manager.getMyPlayer(),
                            b);
                }
            }
            if (updateCounter++ >= REGULAR_UPDATE_FREQ) {
                
                updateCounter = 1;
                this.send(currentState);
                this.lastState = currentState;
            }
            boolean check = true;
            byte[] current = currentState.array();
            byte[] last = lastState.array();
            for (int i = this.lastPosition; i > this.lastPosition
                    - ClientProtocol.getIndexActionMap().size() * 5; i--) {
                if (current[i] != last[i]) {
                    check = false;
                    break;
                }
            }
            if (!check) {
                updateCounter = 1;
                this.currentState.position(0);
                NetworkUtils.getStringFromBuf(this.currentState);
                NetworkUtils.getStringFromBuf(this.currentState);
                double sendTime = this.currentState.getDouble();
                byte[] inputsAsArray = new byte[ClientProtocol
                        .getIndexActionMap().size() * 2];
                int count = 0;
                for (int i = 0; i < ClientProtocol.getIndexActionMap()
                        .size(); i++) {
                    inputsAsArray[count++] = (byte)this.currentState.getInt();
                    inputsAsArray[count++] = this.currentState.get();
                }
                this.prediction.update(this.manager.getMyPlayer(),
                        inputsAsArray, sendTime);
                this.currentState.position(this.lastPosition);
                this.send(currentState);
                this.lastState = currentState;
            }
        }
    }
    
    /**
     * Waits for manager to set the current player. Keep calling tick until the
     * connection will break.
     */
    @Override
    public void run() {
        double freq = (UPDATE_TICK_FREQ / 1000d);
        long delay = (long)(1 / freq);
        
        Player player;
        while ((player = manager.getMyPlayer()) == null)
        // Wait for UUID to be set.
        {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.myUUID = player.getObjectId();
        this.currentState = getCurrentControlState(this.packetNumber);
        this.packetNumber++;
        this.lastState = this.currentState;
        int lastHealth = player.getCurrentHealth();
        
        while (!conn.terminated) {
            int currentHealth = player.getCurrentHealth();
            if (currentHealth < lastHealth) {
                GameFeed.getInstance().printlnDamage("-" + (lastHealth - currentHealth));
                lastHealth = currentHealth;
            } else {
                lastHealth = currentHealth;
            }
            synchronized (this.currentState) {
                this.currentState = getCurrentControlState(this.packetNumber);
                this.packetNumber++;
            }
            this.tick();
            try {
                Thread.sleep(5 * 2);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        this.manager.getMyPlayer().destroy();
    }
    
    public void send(ByteBuffer buf) {
        System.out.println("snd " + debug++);
        conn.send(buf);
    }
}
