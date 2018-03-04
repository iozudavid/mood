package com.knightlore.render.graphic.sprite;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.GraphicSheet;

public final class WeaponSprite {
    
    public static final Graphic SHOTGUN = GraphicSheet.WEAPONS.graphicAt(0, 0);
    public static final Graphic SHOTGUN_LEFT = GraphicSheet.WEAPONS.graphicAt(1, 0);
    public static final Graphic SHOTGUN_RIGHT = GraphicSheet.WEAPONS.graphicAt(2, 0);

    private WeaponSprite() {
    }

}
