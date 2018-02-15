package com.knightlore;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.server.ServerManager;

public class KnightLoreServer {

    public static void main(String[] args) {
        System.out.println("Starting Server...");
        GameSettings.server = true;
        GameEngine engine = new GameEngine(true);
        engine.start();
        ServerManager networkManager = new ServerManager();
        new Thread(networkManager).start();
        // TODO setup the server here
    }

}
