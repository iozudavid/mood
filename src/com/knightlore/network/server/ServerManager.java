package com.knightlore.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.network.Connection;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.Port;
import com.knightlore.network.TCPConnection;
import com.knightlore.render.Camera;
import com.knightlore.utils.Tuple;

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
            serverSocket = new ServerSocket(Port.number);
        } catch (IOException e) {
            System.err.println("Couldn't listen on port " + Port.number);
            System.exit(1);
        }

        while (true) {
            UUID nextUUID = UUID.randomUUID();
            try {
                Socket socket = serverSocket.accept();
                // TODO: decide how to choose player location
                //       fix this hack
                Camera camera = new Camera(4.5, 4.5, 1, 0, 0,
                        Camera.FIELD_OF_VIEW,
                        GameEngine.getSingleton().getRenderer().getMap());
                Player player = new Player(nextUUID, camera);
                Connection conn = new TCPConnection(socket);
                new Thread(conn).start();

                new Thread(new ReceiveFromClient(conn, player)).start();
                new Thread(new SendToClient(conn, nextUUID)).start();

                this.connections.put(nextUUID, new Tuple<Connection, NetworkObject>(conn, player));
            } catch (IOException e) {
                System.err.println("Couldn't create the connection...");
                System.exit(1);
            }

        }
    }

}