package com.knightlore.game.tile;

import com.knightlore.game.Player;
import com.knightlore.game.buff.Fire;
import com.knightlore.game.buff.SpawnVision;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.ZombieServer;
import com.knightlore.render.animation.TimedAnimation;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.engine.GameEngine;
import com.knightlore.render.animation.Animation;
import com.knightlore.render.graphic.texture.Texture;

public class LavaTile extends Tile {

    private static final TimedAnimation<Graphic> LAVA_ANIM =
            new TimedAnimation<>((long) (GameEngine.UPDATES_PER_SECOND / 4));
    static {
        LAVA_ANIM.addFrame(Texture.LAVA_F1);
        LAVA_ANIM.addFrame(Texture.LAVA_F2);
        LAVA_ANIM.addFrame(Texture.LAVA_F3);
        LAVA_ANIM.addFrame(Texture.LAVA_F4);
        GameEngine.ticker.addTickListener(LAVA_ANIM);
    }

    @Override
    public Graphic getWallTexture() {
        return LAVA_ANIM.getFrame();
    }

    @Override
    public void onShot() {
    }

    @Override
    public double getCost() {
        return 100D / (1 - getSolidity());
    }

    @Override
    public Tile copy() {
        return new LavaTile();
    }

    @Override
    public void onEntered(Entity entity) {
        // not quite sure which zombie class to use...
        if(entity instanceof Player || entity instanceof ZombieServer) {
            entity.resetBuff(new Fire(entity));
        }

    }
    
    @Override
    public char toChar() {
        return 'L';
    }
    
    public double getSolidity() {
        return 0.75D;
    }

    @Override
    public double getOpacity() {
        return 0D;
    }

}
