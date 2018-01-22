package com.knightlore.network.client;

import com.knightlore.network.Connection;

public class ReceiveFromServer implements Runnable {
	 
	private Connection conn;

	public ReceiveFromServer(Connection conn) {
	    this.conn = conn;
	}

	@Override
	public void run() {
	    byte[] packet;
		while ((packet = conn.receiveBlocking()) != null) {
			System.out.println("Received: " + new String(packet, Connection.CHARSET));
		}
		System.out.println("Got null packet, exiting");
		
	}
	
}
