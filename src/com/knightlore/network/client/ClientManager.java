package com.knightlore.network.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.knightlore.network.ConnectionDetails;
import com.knightlore.network.TCPConnection;
import com.knightlore.network.server.Receive;

public class ClientManager implements Runnable {
	
	private static Socket server;
	
    @Override
    public void run() {
        try {
            server = new Socket(ConnectionDetails.SERVER_HOSTNAME, ConnectionDetails.PORT);
            System.out.println("Connected to server " + ConnectionDetails.SERVER_HOSTNAME);
            TCPConnection conn = new TCPConnection(server);
            new Thread(conn).start();
            SendToServer sender = new SendToServer(conn);
            Receive receive = new Receive(conn);
            Thread receiver = new Thread(receive);
            Thread sendToServer = new Thread(sender);

            // start threads
            receiver.start();
            sendToServer.start();

            // Wait for them to end and close sockets.

            receiver.join();
            conn.closeInputStream();

            // if the reciving from server ends
            // no point to keep sending to the server
            // ending threads by closing streams first
            conn.closeOutputStream();
            sender.closeStream();
            sendToServer.join();
            server.close();

            System.out.println("End");

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + ConnectionDetails.SERVER_HOSTNAME);
        } catch (IOException e) {
            System.err.println("The server doesn't seem to be running " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Unexpected interruption occured " + e.getMessage());
        }

    }
    
    public static void disconnect(){
    	try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}