package com.knightlore.network.client;

import com.knightlore.network.Connection;

public class ReceiveFromServer implements Runnable {
	 
	private Connection conn;

	public ReceiveFromServer(Connection conn) {
	    this.conn = conn;
	}

	@Override
	public void run() {

		while (true) {
			byte[] packet = conn.receive();
			System.out.println(packet);
			
		}
		
	}
	
}
