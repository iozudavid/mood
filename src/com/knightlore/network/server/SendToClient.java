package com.knightlore.network.server;

import com.knightlore.game.Player;
import com.knightlore.network.Connection;

public class SendToClient implements Runnable{
	
	private Connection conn;
	private Player player;
	
	public SendToClient(Connection conn, Player player){
		this.conn = conn;
		this.player = player;
	}
	
	public void run() {
		
		while (true) {
		     // Command nextCommand = conn.takeNextCommand();
		      //conn.send()...
		      //protocol for server should be provided
			
			
			
		 }

	}
}
