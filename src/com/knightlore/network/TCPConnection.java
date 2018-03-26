package com.knightlore.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Basic network connection based on TCP Protocol
 *
 * @author David Iozu, Will Miller
 */
public class TCPConnection extends Connection {
    
    final Object receiveLock = new Object();
    final Object sendLock = new Object();
    private final Socket socket;
    private DataInputStream infoReceive;
    private DataOutputStream infoSend;
    
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
    
    /**
     * Sends a packet using standard TCP Protocol.
     *
     * @param data - packet to be sent.
     */
    @Override
    public void send(ByteBuffer data) {
        /*
         * Effectively synchronize this method with itself, ensuring the stream
         * is not written to in parallel. Don't use the 'synchronized' method
         * modifier since we can run this in parallel with receive().
         */
        synchronized (sendLock) {
            try {
                int dataLength = data.position();
                if (dataLength == 0) {
                    System.err.println(
                            "Error: Trying to send an empty ByteBuffer!");
                    return;
                }
                // Copy buffer contents to avoid rewinding buffer (which has
                // side-effects and changes the pointer of the buffer).
                infoSend.writeInt(dataLength);
                infoSend.write(data.array(), 0, dataLength);
            } catch (IOException e) {
                System.err.println("Communication broke...");
                e.printStackTrace();
                this.terminated = true;
            }
        }
    }
    
    @Override
    public ByteBuffer receiveBlocking() {
        /*
         * Effectively synchronise this method with itself, ensuring the stream
         * is not read from in parallel. Don't use the 'synchronized' method
         * modifier since we can run this in parallel with send().
         */
        synchronized (receiveLock) {
            /*
             * Whether the received packet is broken. If so, we should error
             * out, since this is a reliable TCPConnection, so a malformed
             * packet indicates a fatal game error.
             */
            boolean malformed = false;
            try {
                int size = -1;
                try {
                    size = infoReceive.readInt();
                    if (size == 0) {
                        return null;
                    }
                    if (size < 0 || size > 2048) {
                        malformed = true;
                    }
                } catch (NumberFormatException e) {
                    malformed = true;
                }
                if (malformed) {
                    throw new IOException(
                            "Fatal: received a malformed packet of size " + size
                                    + " bytes. Usually this indicates an error in the "
                                    + "client-server connection; try restarting "
                                    + "your instance of the game.");
                }
                byte[] tmp = new byte[size];
                infoReceive.readFully(tmp);
                return ByteBuffer.wrap(tmp);
            } catch (IOException e) {
                System.err.println("Communication broke...");
                e.printStackTrace();
                this.terminated = true;
                return null;
            }
        }
    }
    
}
