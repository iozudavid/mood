package com.knightlore.network.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.knightlore.network.GenerateNextId;
import com.knightlore.network.Port;
import com.knightlore.network.TCPConnection;

public class ClientManager implements Runnable {
    private String hostname = "localhost";

    @Override
    public void run() {
        try {
            Socket server = new Socket(hostname, Port.number);
            System.out.println("Connected to server " + hostname);
            TCPConnection conn = new TCPConnection(server,
                    GenerateNextId.forClient());
            SendToServer sender = new SendToServer(conn);
            ReceiveFromServer receiver = new ReceiveFromServer(conn);
            Thread receiveFromServer = new Thread(receiver);
            Thread sendToServer = new Thread(sender);

            // start threads
            sendToServer.start();
            receiveFromServer.start();

            // Wait for them to end and close sockets.

            receiveFromServer.join();
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
            System.err.println("Unknown host: " + hostname);
        } catch (IOException e) {
            System.err.println(
                    "The server doesn't seem to be running " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println(
                    "Unexpected interruption occured " + e.getMessage());
        }

    }

    public static void main(String[] args) {
        (new ClientManager()).run();
    }

}