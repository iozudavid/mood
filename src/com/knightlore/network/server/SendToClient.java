package com.knightlore.network.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.network.Command;
import com.knightlore.network.Connection;

public class SendToClient implements Runnable{
	
	private Connection conn;
	
	public SendToClient(Connection conn){
		this.conn = conn;
	}
	
	public void run() {
		BufferedReader user = new BufferedReader(new InputStreamReader(System.in));
		
		while (true) {
		      Command nextCommand = conn.takeNextCommand();
		      //conn.send()...
		      //protocol for server should be provided
		 }

	}
}
