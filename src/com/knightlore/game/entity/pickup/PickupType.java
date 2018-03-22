package com.knightlore.game.entity.pickup;

public enum PickupType {
    SHOTGUN("Shotgun"),
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
