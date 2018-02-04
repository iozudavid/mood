package com.knightlore;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.server.ServerManager;

public class KnightLoreServer {

    public static void main(String[] args) {
        System.out.println("Starting Server...");
        GameEngine engine = new GameEngine();
        engine.start(false);
        ServerManager networkManager = new ServerManager();
        new Thread(networkManager).start();
        // TODO setup the server here
    }

}
