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
    private String hostname = "localhost";

    @Override
    public void run() {
        try {
            Socket server = new Socket(hostname, Port.number);
            System.out.println("Connected to server " + hostname);
            Connection conn = new TCPConnection(
                    new LinkedBlockingQueue<Command>(), server);
            new Thread(new ReceiveFromServer(conn)).start();
            new Thread(new SendToServer(conn)).start();
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + hostname);
        } catch (IOException e) {
            System.err.println(
                    "The server doesn't seem to be running " + e.getMessage());
        }

    }

    public static void main(String[] args) {
        (new ClientManager()).run();
    }

}