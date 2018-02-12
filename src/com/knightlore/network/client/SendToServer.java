package com.knightlore.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.knightlore.engine.input.InputManager;
import com.knightlore.network.Connection;
import com.knightlore.network.protocol.ClientControl;
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
    private byte[] lastState;
    // private byte[] currentState;
    // private Object lock;
    private int updateCounter = 1;
    // debug
    private int l = 0;

    private byte[] currentState;

    public SendToServer(Connection conn) {
        this.conn = conn;
        // this.lock = new Object();
        this.lastState = this.getCurentStateByteArray();
        this.currentState = getCurentStateByteArray();

    }

    private synchronized byte[] getCurentStateByteArray() {
        byte[] thisState = new byte[ClientProtocol.METADATA_LENGTH
                + ClientProtocol.getIndexActionMap().size()];

        // Prepend metadata to the state array.
        byte[] metadata = ClientProtocol.getMetadata();
        for (int i = 0; i < ClientProtocol.METADATA_LENGTH; i++) {
            thisState[i] = metadata[i];
        }

        try {
            for (int i = 0; i < ClientProtocol.getIndexActionMap()
                    .size(); i++) {
                // taking the current control
                ClientControl currentControl = ClientProtocol.getByIndex(i);
                int keyCode = ClientControl.getKeyCode(currentControl);
                boolean currententControlState = InputManager
                        .isKeyDown(keyCode);
                if (currententControlState == false) {
                    thisState[i + ClientProtocol.METADATA_LENGTH] = 0;
                } else {
                    thisState[i + ClientProtocol.METADATA_LENGTH] = 1;
                }
            }
        } catch (IOException e) {
            System.err.println("Index not good...");
        }
        return thisState;
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
            if (updateCounter++ >= REGULAR_UPDATE_FREQ || NetworkUtils
                    .areStatesDifferent(this.lastState, currentState)) {
                System.out.println("Packet " + l++);
                updateCounter = 1;
                conn.send(currentState);
                this.lastState = currentState;
            }
        }

    }

    @Override
    public void run() {
        while (!conn.terminated) {
            synchronized (this.currentState) {
                this.currentState = getCurentStateByteArray();
            }
            this.tick();
            try {
                // To milliseconds
                double freq = (UPDATE_TICK_FREQ / 1000d);
                long delay = (long) (1 / freq);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}