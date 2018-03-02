package com.knightlore;

import com.knightlore.engine.GameEngine;

public class KnightLoreGame {

    public static void main(String[] args) {
        System.out.println("Starting Client...");
        GameSettings.client = true;

        GameEngine.HEADLESS = false;
        GameEngine engine = GameEngine.getSingleton();
        engine.initEngine();
        engine.start();
    }

}
