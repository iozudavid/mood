package com.knightlore.network.server;

import com.knightlore.network.Connection;

public class SendToClient implements Runnable{
	
	private Connection conn;
	
	public SendToClient(Connection conn){
		this.conn = conn;
	}
	
	public void run() {
		
		while (true) {
		     // Command nextCommand = conn.takeNextCommand();
		      //conn.send()...
		      //protocol for server should be provided
		 }

	}
}
