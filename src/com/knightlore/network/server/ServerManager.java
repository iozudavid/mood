package com.knightlore.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.network.Connection;
import com.knightlore.network.ConnectionDetails;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.TCPConnection;
import com.knightlore.utils.Tuple;
import com.knightlore.utils.Vector2D;

/**
 * A network connection manager that runs server-side and deals with all
 * connections to clients.
 * 
 * @author Will
 */
public class ServerManager implements Runnable {
    private ConcurrentHashMap<UUID, Tuple<Connection, NetworkObject>> connections = new ConcurrentHashMap<UUID, Tuple<Connection, NetworkObject>>();
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
                Vector2D pos = GameEngine.getSingleton().getRenderer().getMap().getRandomSpawnPoint();
                Player player = new Player(pos, Vector2D.UP);
                Connection conn = new TCPConnection(socket);
                new Thread(conn).start();

                new Thread(new Receive(conn)).start();
                new Thread(new SendToClient(conn, player.getObjectId())).start();

                this.connections.put(player.getObjectId(), new Tuple<Connection, NetworkObject>(conn, player));
            } catch (IOException e) {
                System.err.println("Couldn't create the connection...");
                System.exit(1);
            }

        }
    }

}