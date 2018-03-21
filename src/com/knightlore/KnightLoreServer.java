package com.knightlore;

import com.knightlore.engine.GameEngine;

public class KnightLoreServer {

    public static void main(String[] args) {
        System.out.println("Starting Server...");
        GameSettings.server = true;

        GameEngine.HEADLESS = true;
        GameEngine engine = GameEngine.getSingleton();
        engine.initEngine();
        engine.startGame();
        engine.start();
    }

}
