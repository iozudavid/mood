package com.knightlore.network.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.network.Command;
import com.knightlore.network.Connection;
import com.knightlore.network.Port;
import com.knightlore.network.TCPConnection;


public class ClientManager implements Runnable {

    private Socket server = null;
    private String hostname = "localhost";

    @Override
    public void run() {
        
    	try {
			server = new Socket(hostname, Port.number);
			System.out.println(server.toString());
			Connection conn = new TCPConnection(new LinkedBlockingQueue<Command>(), server);
			System.out.println("Connected to " + hostname);
			new Thread(conn).start();
			new Thread(new ReceiveFromServer(conn)).start();
			new Thread(new SendToServer(conn)).start();
    	}catch (UnknownHostException e) {
			System.err.println("Unknown host: " + hostname);
		} catch (IOException e) {
			System.err.println("The server doesn't seem to be running " + e.getMessage());
		}
    	
    }

    
    public static void main(String[] args) {
        (new ClientManager()).run();
    }

}