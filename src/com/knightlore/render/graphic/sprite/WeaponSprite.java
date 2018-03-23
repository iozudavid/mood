package com.knightlore.render.graphic.sprite;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.GraphicSheet;

/**
 * Dumb data holder for weapon sprites.
 * 
 * @author Joe Ellis
 *
 */
public final class WeaponSprite {

    public static final Graphic SHOTGUN = GraphicSheet.WEAPONS.graphicAt(0, 0);

    public static final Graphic PISTOL = GraphicSheet.WEAPONS.graphicAt(0, 1);

    private WeaponSprite() {
    }

}
