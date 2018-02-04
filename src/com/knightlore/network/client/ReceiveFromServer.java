package com.knightlore.network.client;

import java.util.UUID;

import com.knightlore.game.Player;
import com.knightlore.network.Connection;
import com.knightlore.network.protocol.ServerCommand;
import com.knightlore.network.protocol.ServerControl;

public class ReceiveFromServer implements Runnable {
	 
	private Connection conn;
	private UUID clientID;

	public ReceiveFromServer(Connection conn) {
	    this.conn = conn;
	}

	@Override
	public void run() {
	    byte[] packet;
		try {
		    //receive client's id
		    packet = conn.receive();
		    ServerCommand command = new Player(null, null).deserialize(packet);
		    this.clientID = command.getPlayerId();

		    while (conn.terminated==false && (packet = conn.receive()) != null) {
				command = new Player(null, null).deserialize(packet);
				System.out.println("=====NEW PACKET=====");
				System.out.println("Received time: " + command.getTimeSent());
				System.out.println("Client ID: " + command.getPlayerId());
				for(ServerControl c : ServerControl.values()){
					System.out.println(c + ": " + command.getValueByControl(c));
				}
				System.out.println("=====END OF PACKET=====");
			}
			if(conn.terminated==false)
				System.out.println("Got null packet, exiting");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
