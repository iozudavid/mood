package com.knightlore.game;

public enum Team {
    NONE(0xFFFFFFFF, "Neutral"),
    RED(0xFFFF0000, "Red"),
    BLUE(0xFF00FF88, "Blue");

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
