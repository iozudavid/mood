package com.knightlore.render.hud;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class Compass extends HUDElement {

    protected DirectionalSprite compassSprite;

    public Compass() {
        super();
        this.compassSprite = DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE;
    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        if (player == null) {
            return;
        }
        final int WIDTH = 100;

        Vector2D v = new Vector2D(player.getyDir(), player.getxDir());
        Graphic g = compassSprite.getCurrentGraphic(Vector2D.ZERO, v, Vector2D.DOWN);
        pix.drawGraphic(g, x, y, WIDTH, WIDTH);
    }

}
