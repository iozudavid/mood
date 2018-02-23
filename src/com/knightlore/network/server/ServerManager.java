package com.knightlore.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.knightlore.network.Connection;
import com.knightlore.network.ConnectionDetails;
import com.knightlore.network.TCPConnection;

/**
 * A network connection manager that runs server-side and deals with all
 * connections to clients.
 * 
 * @author Will
 */
public class ServerManager implements Runnable {
    private ConcurrentHashMap<UUID, Connection> connections = new ConcurrentHashMap<UUID, Connection>();
    private ServerSocket serverSocket = null;

    @Override
    public void run() {
        System.out.println("Server started");
        Thread pruner = new Thread(new Pruner(connections));
        pruner.start();

        // DEBUG
        // Connection testConn = new Connection(UUID.randomUUID(),
        // commandQueue);

        try {
            serverSocket = new ServerSocket(ConnectionDetails.PORT);
        } catch (IOException e) {
            System.err.println("Couldn't listen on port " + ConnectionDetails.PORT);
            System.exit(1);
        }

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Connection conn = new TCPConnection(socket);
                new Thread(conn).start();
                SendToClient sender = new SendToClient(conn);
                new Thread(sender).start();
                new Thread(new Receive(conn)).start();


                this.connections.put(sender.getUUID(), conn);
            } catch (IOException e) {
                System.err.println("Couldn't create the connection...");
                System.exit(1);
            }

        }
    }

}