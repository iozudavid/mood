package com.knightlore.game.world;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.audio.BackgroundMusic;
import com.knightlore.engine.audio.SoundManager;
import com.knightlore.gui.GameHUD;
import com.knightlore.render.Environment;

public class ClientWorld extends GameWorld {

    private GameHUD gameHUD;

    @Override
    public void update() {
        super.update();
        if(gameHUD != null && gameManager != null) {
            gameHUD.setTimeLeft(gameManager.timeLeftString());
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
        gameHUD = new GameHUD(150,150);
        GameEngine.getSingleton().getDisplay().addGUICanvas(gameHUD);
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
}
