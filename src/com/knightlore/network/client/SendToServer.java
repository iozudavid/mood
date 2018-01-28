package com.knightlore.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.knightlore.engine.input.InputManager;
import com.knightlore.network.Connection;
import com.knightlore.network.protocol.ClientControl;
import com.knightlore.network.protocol.ClientProtocol;

public class SendToServer implements Runnable {

    private Connection conn;
    private BufferedReader user;
    private byte[] lastState;
    private final int timesPerSecodSendingPackets = 10;

    public SendToServer(Connection conn) {
        this.conn = conn;
    }

    private byte[] getCurentStateByteArray() {
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

    private boolean isLastStateDifferent(byte[] currentState) {
        // for (int i = 0; i < this.lastState.length; i++) {
        // if (this.lastState[i] == currentState[i])
        // continue;
        // return true;
        // }
        // return false;
        // }
        // DEBUG
        return true;
    }

    public void run() {
        // initialize lastState with current state for now
        this.lastState = this.getCurentStateByteArray();
        long taskTime = 0;
        // 10 times per second
        long sleepTime = 1000 / this.timesPerSecodSendingPackets;
        while (conn.terminated == false) {
            taskTime = System.currentTimeMillis();

            byte[] currentClientState = this.getCurentStateByteArray();
            // check if the last state was changed
            // if it was then send to the server the current sate
            if (isLastStateDifferent(currentClientState)) {
                conn.send(currentClientState);
                this.lastState = currentClientState;
            }
            // if there is the same state
            // don't send anything to the server

            taskTime = System.currentTimeMillis() - taskTime;
            if (sleepTime - taskTime > 0) {
                try {
                    Thread.sleep(sleepTime - taskTime);
                } catch (InterruptedException e) {
                    System.err.println("Thread interrupted while sleeping...");
                }
            }
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
