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
    // How often to send an update of controls, rather than just if the
    // control state has changed. A value of 100 means that in every 100 loops,
    // at *least* one update will be sent.
    private static final int REGULAR_UPDATE_FREQ = 100;

    private Connection conn;
    private BufferedReader user;
    private byte[] lastState;
    private byte[] currentState;
    private Object lock;

    public SendToServer(Connection conn) {
        this.conn = conn;
        this.lock = new Object();
        this.lastState = this.getCurentStateByteArray();
        this.currentState = this.getCurentStateByteArray();
        new Thread() {
            public void run() {
                while (!conn.terminated)
                    synchronized (lock) {
                        currentState = getCurentStateByteArray();

                    }
            }
        }.start();

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

    public void run() {
        int updateCounter = 1;
        while (!conn.terminated) {
            synchronized (lock) {
                // Send a controls update if either the controls have changed or
                // a regular update is due.
                if (updateCounter++ >= REGULAR_UPDATE_FREQ
                        || NetworkUtils.areStatesDifferent(this.lastState,
                                this.currentState)) {
                    updateCounter = 1;
                    conn.send(this.currentState);
                    this.lastState = this.currentState;
                }
            }
            // if there is the same state
            // don't send anything to the server
        }
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

}
