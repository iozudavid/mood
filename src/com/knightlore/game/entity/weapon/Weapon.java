package com.knightlore.game.entity.weapon;

import com.knightlore.render.graphic.Graphic;

public abstract class Weapon {

    private Graphic graphic;

    public Weapon(Graphic graphic) {
        this.graphic = graphic;
    }

    public Graphic getGraphic() {
        return graphic;
    }

}
