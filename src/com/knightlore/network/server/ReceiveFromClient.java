package com.knightlore.network.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.network.Connection;
import com.knightlore.network.protocol.ClientControl;
import com.knightlore.network.protocol.ClientProtocol;
import com.knightlore.network.protocol.Command;

public class ReceiveFromClient implements Runnable {
    public BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<>();

    private Connection conn;

    public ReceiveFromClient(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void run() {
        byte[] packet;
        try {
            while ((packet = conn.receive()) != null)
                this.commandQueue.put(getPacketDecoded(packet));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Got null packet, exiting");
    }

    public Command getPacketDecoded(byte[] packet) {
        // Metadata processing.
        ByteBuffer buf = ByteBuffer.wrap(packet);
        long timeSent = buf.getLong();

        try {
            Map<ClientControl, Byte> clientInput = new HashMap<ClientControl, Byte>();
            for (int i = ClientProtocol.METADATA_LENGTH; i < packet.length; i++) {
                // using the protocol
                // to decode the byte array
                ClientControl control = ClientProtocol.getByIndex(i);
                clientInput.put(control, packet[i]);
            }
            return new Command(clientInput, timeSent);
        } catch (IOException e) {
            System.err.println("Bad input");
        }
        return null;
    }

}
