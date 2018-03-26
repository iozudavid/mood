package com.knightlore;

import com.knightlore.engine.GameEngine;

/**
 * Server entry point.
 * 
 * @author James
 *
 */
public class KnightLoreServer {
    
    public static void main(String[] args) {
        System.out.println("Starting Server...");
        GameSettings.server = true;
        System.out.println("Parsing Config...");
        ConfigParser.doParse();
        System.out.println("Done Config.");
        GameEngine.HEADLESS = true;
        GameEngine engine = GameEngine.getSingleton();
        engine.initEngine();
        engine.startGame();
        engine.start();
    }
    
}
