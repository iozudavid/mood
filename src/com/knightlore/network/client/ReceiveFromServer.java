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
		try {
			while (conn.terminated==false && (packet = conn.receive()) != null) {
				System.out.println("Received: " + new String(packet, Connection.CHARSET));
			}
			if(conn.terminated==false)
				System.out.println("Got null packet, exiting");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
