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
    protected final DirectionalSprite sprite;

    /**
     * The time taken for a new pickup to be spawned by the
     * spawn manager when this pickup has been destroyed.
     * This only applies if this item was produced by the
     * pickup manager.
     */
    protected double spawnDelay;

    /**
     * Manages all pickups placed, at the start of the game, on
     * PickupTiles.
     */
    private PickupManager pickupManager;
    
    /**
     * Produce a pickup item with the specified uuid, position vector, directional sprite and 
     * pickup manager.
     * @param uuid
     * @param position
     * @param sprite
     * @param pickupManager
     */
    public PickupItem(UUID uuid, Vector2D position, DirectionalSprite sprite, PickupManager pickupManager) {
        super(uuid, PICKUP_SIZE , position, Vector2D.UP);
        this.sprite = sprite;
        this.pickupManager = pickupManager;
    }

    /**
     * Upon update, the pickup spins around and bobs up and down slightly by setting its
     * direction and zOffset respectively.
     */
    @Override
    public void onUpdate() {
        super.onUpdate();
        // FIXME: avoid null pointer for direction when this is called and the
        // constructor hasn't yet finished.
        if (direction == null) {
            return;
        }

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
    
    public double getSpawnDelay() {
        return spawnDelay;
    }
    
    @Override
    public String getName() {
        return "NAME THIS";
    }
    
    /**
     * If the given pickup has a pickup manager, it is placed in the queue
     * of the pickup manager so it can be spawned after its spawn delay expires.
     * Items on the client, or items whose placement was not determined by
     * Pickup Tiles while not have a pickup manager.
     */
    public void addToPickupManager() {
        // Clients will have a null Pickup manager
        if(pickupManager != null) {
            pickupManager.addToQueue(this);
        }
    }

}
