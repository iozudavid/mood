package com.knightlore.game.world;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.audio.BackgroundMusic;
import com.knightlore.engine.audio.SoundManager;
import com.knightlore.game.area.generation.MapGenerator;
import com.knightlore.game.manager.AIManager;
import com.knightlore.game.manager.GameManager;
import com.knightlore.game.manager.GameState;
import com.knightlore.game.manager.PlayerManager;
import com.knightlore.gui.GameChat;
import com.knightlore.render.Environment;

/**
 * The client side implementation of the GameWorld.
 * 
 * @author James Adey
 */
public class ClientWorld extends GameWorld {
    
    public GameChat getGameChat() {
        return gameChat;
    }
    
    private GameChat gameChat;
    private int screenWidth;
    private int screenHeight;
    
    /**
     * Sets the time left in the GUI, and displays the scoreboard if the game is
     * over.
     */
    @Override
    public void update() {
        super.update();
        if (gameChat != null && gameManager != null) {
            gameChat.setTimeLeft(gameManager.timeLeftString());
        }
        
        if (GameManager.getGameState() == GameState.FINISHED) {
            gameChat.setScoreMenuVisible();
        }
    }
    
    public Environment getEnvironment() {
        return Environment.DARK_OUTDOORS;
    }
    
    /**
     * Begins the background music and Generates the map and creates the
     * AIManager and the PlayerManager.
     * <p>
     * Note: a null map seed will cause the map to generate a random map.
     * 
     * @param mapSeed
     *            the desired seed for this map
     * @see MapGenerator
     * @see AIManager
     * @see PlayerManager
     */
    @Override
    public void setUpWorld(Long mapSeed) {
        super.setUpWorld(mapSeed);
        startBgMusic();
    }
    
    /**
     * Creates the game chat.
     */
    public void buildGUI() {
        gameChat = new GameChat(screenWidth, screenHeight);
        // gameHUD = new GameHUD(150,150);
        GameEngine.getSingleton().getDisplay().addGUICanvas(gameChat);
    }
    
    private void startBgMusic() {
        BackgroundMusic bgMusic = this.getEnvironment().getBgMusic();
        SoundManager soundManager = GameEngine.getSingleton().getSoundManager();
        soundManager.loop(bgMusic.soundRes, soundManager.defaultVolume);
    }
    
    /**
     * Creates the UI, called after the display has finished initialising from
     * the GameEngine
     * 
     * @see GameEngine
     */
    @Override
    public void onPostEngineInit() {
        buildGUI();
    }
    
    public void setScreenWidth(int w) {
        this.screenWidth = w;
    }
    
    public void setScreenHeight(int h) {
        this.screenHeight = h;
    }
}
