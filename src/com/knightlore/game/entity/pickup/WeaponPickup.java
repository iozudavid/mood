package com.knightlore.game.entity.pickup;

import java.util.UUID;

import com.knightlore.game.Player;
import com.knightlore.game.entity.weapon.WeaponType;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

/**
 * A weapon pickup item.
 * 
 * @author Will Miller, Joe Ellis
 *
 */
public abstract class WeaponPickup extends PickupItem {
    public WeaponPickup(Vector2D position, DirectionalSprite dSprite, PickupManager pickupManager) {
        this(UUID.randomUUID(), position, dSprite, pickupManager);
    }

    public WeaponPickup(UUID uuid, Vector2D position, DirectionalSprite dSprite, PickupManager pickupManager) {
        super(uuid, position, dSprite, pickupManager);
        spawnDelay = 10;
    }

    // FIGURE OUT
    // private ShotgunPickup(UUID uuid, Vector2D position) {
    // super(uuid, position, DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE);
    // spawnProtectTime = GameEngine.ticker.getTime() +
    // PICKUP_SPAWN_PROTECT_TIME;
    // }
    // FIGURE OUT

    @Override
    public int getMinimapColor() {
        return 0xFF00FF;
    }

    @Override
    public String getClientClassName() {
        // One class for both client and server.
        return this.getClass().getName();
    }

    @Override
    public void onCollide(Player player) {
        if (exists) {
            System.out.println("Player " + player.getName() + " collided with " + this.getClass().getSimpleName());
            // update pickup manager
            addToPickupManager();
            // Set the player's new weapon
            player.setCurrentWeaponType(this.getWeaponType());
            // existence of object set to false
            this.destroy();
        }
    }

    abstract WeaponType getWeaponType();
}
