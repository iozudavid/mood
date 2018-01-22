package com.knightlore.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.knightlore.network.Connection;

public class SendToServer implements Runnable{

	private Connection conn;
	
	public SendToServer(Connection conn) {
		this.conn = conn;
	}
	
	public void run() {
		BufferedReader user = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String message = user.readLine();
				System.out.println(message);
				conn.send(message.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
}
