package com.knightlore.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

/*
 * Basic network connection
 * start using tcp for now, adpat it in the future to use udp
 */

public class TCPConnection extends Connection {

    private DataInputStream infoReceive;
    private DataOutputStream infoSend;
    private Socket socket;

    public TCPConnection(Socket socket) {
        this.socket = socket;
        try {
            socket.setSoTimeout(TIMEOUT_MILLIS);
            this.infoReceive = new DataInputStream(socket.getInputStream());
            this.infoSend = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("The connection doesn't seem to work...");
            System.exit(1);
        }
    }

    /**
     * Closes the input and output streams, and also the socket itself.
     */
    public void close() {
        try {
            infoReceive.close();
            infoSend.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Something wrong " + e.getMessage());
        }
    }

    @Override
    public void send(ByteBuffer data) {
        Object lock = new Object();
        synchronized (lock) {
            try {
                int dataLength = data.position();
                if (dataLength == 0) {
                    System.err.println(
                            "Error: Trying to send an empty ByteBuffer!");
                    return;
                }
                // Copy buffer contents to avoid rewinding buffer (which has
                // side-effects and changes the pointer of the buffer).
                byte[] tmp = Arrays.copyOfRange(data.array(), 0, dataLength);

                infoSend.writeInt(dataLength);
                infoSend.write(tmp);
            } catch (IOException e) {
                System.err.println("Communication broke...");
                this.terminated = true;
            }
        }
    }

    @Override
    public ByteBuffer receiveBlocking() {
        Object lock = new Object();
        synchronized (lock) {
            try {
                int size = infoReceive.readInt();
                if (size == 0) {
                    System.err.println(
                            "Error: Trying to receive an empty ByteBuffer!");
                    return null;
                }
                byte[] tmp = new byte[size];
                infoReceive.readFully(tmp);
                ByteBuffer buf = ByteBuffer.wrap(tmp);
                return buf;
            } catch (IOException e) {
                System.err.println("Communication broke...");
                this.terminated = true;
                return null;
            }
        }
    }

}
