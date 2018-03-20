package com.knightlore.game;

public enum Team {
    NONE(0xFFFFFFFF),
    RED(0xFFFF0000),
    BLUE(0xFF00FF88);

    private final int color;

    Team(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
