package com.knightlore.render.hud;

import java.util.List;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.TickListener;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.sprite.DirectionalSprite;

public class WeaponSlot implements IRenderable, TickListener {

    private DirectionalSprite weapon;
    private long anim;

    public WeaponSlot() {
        this.weapon = DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE;
        GameEngine.ticker.addTickListener(this);
    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        List<Graphic> angles = weapon.getAngles();
        final int WIDTH = 100, HEIGHT = WIDTH;
        pix.drawGraphic(angles.get((int) (anim) % angles.size()), x, y, WIDTH, HEIGHT);
    }

    @Override
    public long interval() {
        return (long) (GameEngine.UPDATES_PER_SECOND / 20);
    }

    @Override
    public void onTick() {
        anim++;
    }

}
