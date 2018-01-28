package com.knightlore.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.network.Connection;
import com.knightlore.network.Port;
import com.knightlore.network.TCPConnection;
import com.knightlore.render.Camera;

/**
 * A network connection manager that runs server-side and deals with all
 * connections to clients.
 * 
 * @author Will
 */
public class ServerManager implements Runnable {
    private ConcurrentHashMap<UUID, Connection> connections = new ConcurrentHashMap<UUID, Connection>();
    private ServerSocket serverSocket = null;
    private GameEngine engine;

    public ServerManager(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void run() {
        System.out.println("Server started");
        Thread pruner = new Thread(new Pruner(connections));
        pruner.start();

        // DEBUG
        // Connection testConn = new Connection(UUID.randomUUID(),
        // commandQueue);

        try {
            serverSocket = new ServerSocket(Port.number);
        } catch (IOException e) {
            System.err.println("Couldn't listen on port " + Port.number);
            System.exit(1);
        }

        while (true) {
            // TODO: listen for connections, create and start a Connection
            // object for each
            // infinite loop to listen for incoming loop
            // here will be the verification of the convention for 4 bytes for
            // example
            // then create an entry in map
            // and also start a new connection thread
            // new Client();
            try {
                Socket socket = serverSocket.accept();
                UUID clientID = UUID.randomUUID();
                Connection conn = new TCPConnection(socket);

                // TODO: decide how to choose player location
                Player player = engine.createPlayer(4.5, 4.5, 1, 0, 0,
                        Camera.FIELD_OF_VIEW);

                new Thread(new ReceiveFromClient(conn, player)).start();
                new Thread(new SendToClient(conn)).start();

                this.connections.put(clientID, conn);
            } catch (IOException e) {
                System.err.println("Couldn't create the connection...");
                System.exit(1);
            }

        }
    }

}