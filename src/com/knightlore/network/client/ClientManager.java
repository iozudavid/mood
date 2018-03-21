package com.knightlore.network.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.knightlore.network.ConnectionDetails;
import com.knightlore.network.TCPConnection;
import com.knightlore.network.server.Receive;

public class ClientManager implements Runnable {
	
	private static Socket server;
    private SendToServer sender;
    private Receive receive;
    private TCPConnection conn;

    public ClientManager() {
        try {
            server = new Socket(ConnectionDetails.SERVER_HOSTNAME,
                    ConnectionDetails.PORT);
        } catch (UnknownHostException e) {
            System.err.println(
                    "Unknown host: " + ConnectionDetails.SERVER_HOSTNAME);
        } catch (IOException e) {
            System.err.println(
                    "The server doesn't seem to be running " + e.getMessage());
        }
        System.out.println(
                "Connected to server " + ConnectionDetails.SERVER_HOSTNAME);
        this.conn = new TCPConnection(server);
        new Thread(conn).start();
        this.sender = new SendToServer(conn);
        this.receive = new Receive(conn);
    }

    public SendToServer getServerSender() {
        return sender;
    }

    @Override
    public void run() {

        Thread receiver = new Thread(receive);
        Thread sendToServer = new Thread(sender);
        // start threads
        receiver.start();
        sendToServer.start();

        try {
            // Wait for them to end and close sockets.
            receiver.join();
            conn.close();
            sender.closeStream();
            sendToServer.join();
        } catch (InterruptedException e) {
            System.err.println("Client connection threads interrupted.");
        }
        System.out.println("End");

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