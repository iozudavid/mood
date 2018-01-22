package com.knightlore.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;

/*
 * Basic network connection
 * start using tcp for now, adpat it in the future to use udp
 */

public class TCPConnection extends Connection {

    private InputStream infoReceive;
    private OutputStream infoSend;

    public TCPConnection(Queue<Command> commandQueue, Socket socket) {
        super(commandQueue);
        try {
            this.infoReceive = socket.getInputStream();
            this.infoSend = socket.getOutputStream();
        } catch (IOException e) {
            System.err.println("The connection doesn't seem to work...");
            System.exit(1);
        }
    }

    @Override
    public void send(byte[] data) {
        try {
            infoSend.write(data.length);
            infoSend.write(data);
            infoSend.flush();
        } catch (IOException e) {
            System.err.println("Communication broke...");
            System.exit(1);
        }

    }

    @Override
    public byte[] receiveBlocking() {
        byte[] data = null;
        try {
            int len = infoReceive.read();
            // Unexpected end of stream.
            if (len == -1)
                throw new IOException();
            data = new byte[len];
            int i, count = 0;
            while (count < len && (i = infoReceive.read()) != -1) {
                data[count++] = (byte) i;
            }
        } catch (IOException e) {
            System.err.println("Communication broke...");
            System.exit(1);
        }
        return data;
    }

}
