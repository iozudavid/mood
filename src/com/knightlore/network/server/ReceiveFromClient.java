package com.knightlore.network.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.knightlore.game.Player;
import com.knightlore.network.Connection;
import com.knightlore.network.protocol.ClientControl;
import com.knightlore.network.protocol.ClientProtocol;
import com.knightlore.network.protocol.ClientCommand;

public class ReceiveFromClient implements Runnable {
    private Connection conn;

    private Player player;

    public ReceiveFromClient(Connection conn, Player player) {
        this.conn = conn;
        this.player = player;
    }

    @Override
    public void run() {
        byte[] packet;
        try {
            while ((packet = conn.receive()) != null)
                // Execute the received command on the relevant player.
                getPacketDecoded(packet).execute(player);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Got null packet, exiting");
    }

    public ClientCommand getPacketDecoded(byte[] packet) {
        // Metadata processing.
        ByteBuffer buf = ByteBuffer.wrap(packet);
        long timeSent = buf.getLong();

        try {
            Map<ClientControl, Byte> clientInput = new HashMap<ClientControl, Byte>();
            for (int i = 0; i < ClientProtocol.getIndexActionMap()
                    .size(); i++) {
                // using the protocol
                // to decode the byte array
                ClientControl control = ClientProtocol.getByIndex(i);
                clientInput.put(control,
                        packet[i + ClientProtocol.METADATA_LENGTH]);
            }
            return new ClientCommand(clientInput, timeSent);
        } catch (IOException e) {
            System.err.println("Bad input: ");
            e.printStackTrace();
        }
        return null;
    }

}
