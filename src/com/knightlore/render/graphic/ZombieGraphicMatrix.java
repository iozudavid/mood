package com.knightlore.render.graphic;

public class ZombieGraphicMatrix {

    public enum Stance {
        MOVE, STAND
    }

    private final static GraphicSheet[] zombieMatrix = new GraphicSheet[Stance.values().length];
    static {
        zombieMatrix[Stance.MOVE.ordinal()] = new GraphicSheet("res/models/zombie_anim_sprites.png", 128);
        zombieMatrix[Stance.STAND.ordinal()] = new GraphicSheet("res/models/zombie_anim_sprites.png", 128);
    }

    public static GraphicSheet getGraphic(Stance stance) {
        return zombieMatrix[stance.ordinal()];
    }

}
