package com.knightlore.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

/*
 * Basic network connection
 * start using tcp for now, adpat it in the future to use udp
 */

public class TCPConnection extends Connection {

    private DataInputStream infoReceive;
    private DataOutputStream infoSend;

    public TCPConnection(Socket socket) {
        try {
            socket.setSoTimeout(TIMEOUT_MILLIS);
            this.infoReceive = new DataInputStream(socket.getInputStream());
            this.infoSend = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("The connection doesn't seem to work...");
            System.exit(1);
        }
    }

    public void closeInputStream() {
        try {
            infoReceive.close();
        } catch (IOException e) {
            System.err.println("Something wrong " + e.getMessage());
        }
    }

    public void closeOutputStream() {
        try {
            infoSend.close();
        } catch (IOException e) {
            System.err.println("Something wrong " + e.getMessage());
        }
    }

    @Override
    public void send(ByteBuffer data) {
        Object lock = new Object();
        synchronized (lock) {
            try {
                int numBytes = data.position();
                byte[] tmp = new byte[numBytes];
                data.rewind();
                if (!data.hasRemaining())
                    System.err.println("Error: Trying to send an empty ByteBuffer!");
                data.get(tmp);
                infoSend.writeInt(tmp.length);
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
                byte[] tmp = new byte[size];
                infoReceive.readFully(tmp);
                // return ByteBuffer.wrap(tmp);
                ByteBuffer buf = ByteBuffer.wrap(tmp);
                if (!buf.hasRemaining())
                    System.err.println("Error: Trying to receive an empty ByteBuffer!");
                return buf;
            } catch (IOException e) {
                System.err.println("Communication broke...");
                this.terminated = true;
                return null;
            }
        }
    }

}
