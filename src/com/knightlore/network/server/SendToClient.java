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
	private LinkedBlockingQueue<Command> commandQueue;
	
	public SendToClient(Connection conn, LinkedBlockingQueue<Command> commandQueue){
		this.conn = conn;
		this.commandQueue = commandQueue;
	}
	
	public void run() {
		BufferedReader user = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
			    System.out.println("Enter a String: ");
				String message = user.readLine();
				conn.send(message.getBytes(Connection.CHARSET));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

	}
}
