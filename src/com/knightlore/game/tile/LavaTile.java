package com.knightlore.game.tile;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.animation.Animation;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class LavaTile extends Tile {

    private static Animation LAVA_ANIM = new Animation((long) (GameEngine.UPDATES_PER_SECOND / 4));
    static {
        LAVA_ANIM.addFrame(Texture.LAVA_F1);
        LAVA_ANIM.addFrame(Texture.LAVA_F2);
        LAVA_ANIM.addFrame(Texture.LAVA_F3);
        LAVA_ANIM.addFrame(Texture.LAVA_F4);
        GameEngine.ticker.addTickListener(LAVA_ANIM);
    }

    @Override
    public Graphic getTexture() {
        return LAVA_ANIM.getGraphic();
    }

    @Override
    public void onShot() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEntered(Entity entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public Tile copy() {
        return new LavaTile();
    }

    @Override
    public double getSolidity() {
        return 0.75D;
    }

    @Override
    public double getOpacity() {
        return 0D;
    }

}
