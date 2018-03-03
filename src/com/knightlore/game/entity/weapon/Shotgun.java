package com.knightlore.game.entity.weapon;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.render.graphic.GraphicSheet;
import com.knightlore.utils.Vector2D;

public class Shotgun extends Weapon {

    public Shotgun() {
        super(GraphicSheet.SHOTGUN_SPRITES.graphicAt(0, 16), false, (int) (0.75 * GameEngine.UPDATES_PER_SECOND));
    }
    
    public void shoot(Player shooter) {
    }

    @Override
    public int damageInflicted(Player shooter, Player shot) {
        final int BASE_DAMAGE = 100;
        Vector2D displacement = shooter.getPosition().subtract(shot.getPosition());
        return (int) (BASE_DAMAGE - Math.pow(displacement.magnitude(), 2));
    }

}
