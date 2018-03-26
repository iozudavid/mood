package com.knightlore;

import com.knightlore.engine.GameEngine;

/**
 * Client game entry point.
 *
 * @author James
 */
public class KnightLoreGame {
    
    public static void main(String[] args) {
        System.out.println("Starting Client...");
        GameSettings.client = true;
        System.out.println("Parsing Config...");
        ConfigParser.doParse();
        System.out.println("Done Config.");
        GameEngine.HEADLESS = false;
        GameEngine engine = GameEngine.getSingleton();
        engine.initEngine();
        engine.start();
    }
    
}
