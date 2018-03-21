package com.knightlore.game.world;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.audio.BackgroundMusic;
import com.knightlore.engine.audio.SoundManager;
import com.knightlore.gui.GameChat;
import com.knightlore.render.Environment;

public class ClientWorld extends GameWorld {

    public GameChat getGameChat() {
        return gameChat;
    }

    private GameChat gameChat;
    private int screenWidth;
    private int screenHeight;

    @Override
    public void update() {
        super.update();
        if(gameChat != null && gameManager != null) {
            gameChat.setTimeLeft(gameManager.timeLeftString());
        }
    }

    public Environment getEnvironment() {
        return Environment.DARK_OUTDOORS;
    }

    @Override
    public void setUpWorld(Long mapSeed) {
        super.setUpWorld(mapSeed);
        startBgMusic();
    }

    public void buildGUI() {
        gameChat = new GameChat(screenWidth, screenHeight);
        gameChat.init();
       // gameHUD = new GameHUD(150,150);
        GameEngine.getSingleton().getDisplay().addGUICanvas(gameChat);
    }

    private void startBgMusic() {
        BackgroundMusic bgMusic = this.getEnvironment().getBgMusic();
        SoundManager soundManager = GameEngine.getSingleton().getSoundManager();
        soundManager.loop(bgMusic.soundRes, soundManager.defaultVolume);
    }

    @Override
    public void onPostEngineInit() {
        buildGUI();
    }
    
    public void setScreenWidth(int w){
        this.screenWidth=w;
    }
    
    public void setScreenHeight(int h){
        this.screenHeight=h;
    }
}
