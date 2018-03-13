package com.knightlore.game.world;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import com.knightlore.engine.audio.BackgroundMusic;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.Environment;

public class ClientWorld extends GameWorld {

    @Override
    public void update() {
    }

    public void addEntity(Entity ent) {
        this.ents.add(ent);
    }

    public void removeEntity(Entity ent) {
        this.ents.remove(ent);
    }

    public Environment getEnvironment() {
        return Environment.DARK_OUTDOORS;
    }

    @Override
    public void setUpWorld(Long mapSeed) {
        super.setUpWorld(mapSeed);
        buildGUI();
        startBgMusic();
    }

    public void buildGUI() {
    }

    private void startBgMusic() {
        BackgroundMusic bgMusic = this.getEnvironment().getBgMusic();
        Clip clip;
        try {
            clip = bgMusic.soundRes.getNewClip();
        } catch (LineUnavailableException e) {
            System.out.println(
                    "No lines available to start background music - skipping.");
            return;
        }
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
}
