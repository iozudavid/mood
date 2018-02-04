package com.knightlore.network.client;

import com.knightlore.network.Connection;
import com.knightlore.network.protocol.ServerCommand;
import com.knightlore.network.protocol.ServerControl;

public class ReceiveFromServer implements Runnable {
	 
	private Connection conn;

	public ReceiveFromServer(Connection conn) {
	    this.conn = conn;
	}

	@Override
	public void run() {
	    byte[] packet;
		try {
			while (!conn.terminated && (packet = conn.receive()) != null) {
				ServerCommand command = ServerCommand.decodePacket(packet);
				
				System.out.println("=====NEW PACKET=====");
				System.out.println("Received time: " + command.getTimeSent());
				System.out.println("Client ID: " + command.getObjectId());
				for(ServerControl c : ServerControl.values()){
					System.out.println(c + ": " + command.getValueByControl(c));
				}
				System.out.println("=====END OF PACKET=====");
				
			}
			if(!conn.terminated)
				System.out.println("Got null packet, exiting");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
