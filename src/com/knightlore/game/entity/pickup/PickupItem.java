package com.knightlore.game.entity.pickup;

import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

/**
 * An item that a player can collect in the game world. A good example would be
 * a weapon or health pickup.
 * 
 * @author Joe Ellis
 *
 */
public abstract class PickupItem extends Entity {

    private static final double ROTATION_SPEED = 0.1D;
    private static final double PICKUP_SIZE = 0.25D;
    private static final double FLOAT_BOB_AMOUNT = 50.0;
    private static final double FLOAT_BOB_SPEED = 0.10;
    private static final int FLOOR_OFFSET = 100;

    protected DirectionalSprite sprite;

    public PickupItem(Vector2D position) {
        super(PICKUP_SIZE, position, Vector2D.UP);
    }

    public PickupItem(UUID uuid, Vector2D position) {
        super(uuid, PICKUP_SIZE, position, Vector2D.UP);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // FIXME: avoid null pointer for direction when this is called and the
        // constructor hasn't yet finished.
        if (direction == null)
            return;

        // Make the item bob up and down.
        long t = GameEngine.ticker.getTime();
        zOffset = FLOOR_OFFSET + (int) (Math.sin(t * FLOAT_BOB_SPEED) * FLOAT_BOB_AMOUNT);

        // Make the item rotate.
        double xprime = direction.getX() * Math.cos(ROTATION_SPEED) - direction.getY() * Math.sin(ROTATION_SPEED);
        double yprime = direction.getX() * Math.sin(ROTATION_SPEED) + direction.getY() * Math.cos(ROTATION_SPEED);
        direction = new Vector2D(xprime, yprime);
        plane = direction.perpendicular();
    }

    @Override
    public DirectionalSprite getDirectionalSprite() {
        return sprite;
    }

}
