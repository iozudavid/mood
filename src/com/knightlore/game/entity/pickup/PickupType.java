package com.knightlore.game.entity.pickup;

/**
 * Used by the pickup manager, room generator and pickup tile to
 * specify or identify the appropriate pickup item.
 * @author Thomas
 *
 */
public enum PickupType {
    SHOTGUN("Shotgun"),
    PISTOL("Pistol"),
    HEALTH("Health");


    private final String name;

    PickupType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
