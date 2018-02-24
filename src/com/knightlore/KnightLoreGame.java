package com.knightlore;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.client.ClientManager;

public class KnightLoreGame {

    public static void main(String[] args) {
        System.out.println("Starting Client...");
        GameSettings.client = true;
        GameEngine engine = new GameEngine(false);
        engine.start();
        ClientManager networkManager = new ClientManager();
        new Thread(networkManager).start();
        // TODO setup the client here
    }

}
