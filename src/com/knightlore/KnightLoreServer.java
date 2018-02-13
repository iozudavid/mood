package com.knightlore;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.server.ServerManager;

public class KnightLoreServer {

    public static void main(String[] args) {
        System.out.println("Starting Server...");
        GameSettings.server = true;

        GameEngine.HEADLESS = true;
        GameEngine engine = GameEngine.getSingleton();
        engine.initEngine();
        engine.start();

        ServerManager networkManager = new ServerManager();
        new Thread(networkManager).start();
        // TODO setup the server here
    }

}
