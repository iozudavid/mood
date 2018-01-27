package com.knightlore.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.knightlore.network.Connection;

public class SendToServer implements Runnable{

	private Connection conn;
	private BufferedReader user;
	private String message;
	
	public SendToServer(Connection conn) {
		this.conn = conn;
	}
	
	public void run() {
		user = new BufferedReader(new InputStreamReader(System.in));
		message = null;
	    System.out.println("Enter a String: ");
		try {
			while (conn.terminated==false && (message=user.readLine())!=null) {
				conn.send(message.getBytes(Connection.CHARSET));
				System.out.println("Enter a String: ");
			}
		} catch (IOException e) {
			System.err.println("Unexpected error " + e.getMessage());
			System.exit(1);
		}

	}
	
	//this should be use to close this thread
	//best way to do this as the thread will be blocked listening for the next string
	public void closeStream(){
		try {
			this.user = new BufferedReader(new InputStreamReader(System.in));
			this.user.close();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Unexpected error " + e.getMessage());
			System.exit(1);
		}
	}
	
}
