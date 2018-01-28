package com.knightlore.network.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.knightlore.network.Connection;
import com.knightlore.network.protocol.ClientControl;
import com.knightlore.network.protocol.ClientProtocol;

public class ReceiveFromClient implements Runnable {
    private Connection conn;

    public ReceiveFromClient(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void run() {
        byte[] packet;
        try {
			while ((packet = conn.receive()) != null) {
				
				Map<ClientControl, Byte> decodedPacket = this.getPacketDecoded(packet);
				
				//now use the decoded packet
				//create new game states as commands
				//put them in player's queues
				//and send them
				
				// TODO: update game state based on command, send state delta to
				// clients??
				
				
     
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println("Got null packet, exiting");
    }
    
    public Map<ClientControl, Byte> getPacketDecoded(byte[] packet){
		try {
			Map<ClientControl, Byte> clientInput = new HashMap<ClientControl, Byte>();
			for (int i : ClientProtocol.getIndexActionMap().keySet()) {
				//using the protocol
				//to decode the byte array
				ClientControl control = ClientProtocol.getByIndex(i);
				clientInput.put(control, packet[i]);
			}
			return clientInput;
		} catch (IOException e) {
			System.err.println("Bad input");
		}
    	return null;
    }

}
