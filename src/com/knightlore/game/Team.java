package com.knightlore.game;

import java.awt.*;

public enum Team {
    NONE(Color.WHITE.getRGB(), "Neutral"),
    RED(Color.RED.getRGB(), "Red"),
    BLUE(Color.BLUE.getRGB(), "Blue");

    private final int color;
    private final String name;

    Team(int color, String name) {
        this.color = color;
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    @Override
    public String toString() {
        return name;
    }
}
