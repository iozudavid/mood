package com.knightlore.render.graphic;

public class PlayerGraphicMatrix {

    public enum Color {
        RED, BLUE;
    }

    public enum Weapon {
        PISTOL, SHOTGUN;
    }

    // 55
    public enum Stance {
        MOVE, STRAFE, STAND;
    }

    private final static GraphicSheet[][][] playerMatrix = new GraphicSheet[Color.values().length][Weapon
            .values().length][Stance.values().length];
    static {
        playerMatrix[Color.BLUE.ordinal()][Weapon.SHOTGUN.ordinal()][Stance.MOVE.ordinal()] = new GraphicSheet(
                "res/models/pl_blue_shotgun_anim_sprites.png", 128);
        playerMatrix[Color.BLUE.ordinal()][Weapon.PISTOL.ordinal()][Stance.MOVE.ordinal()] = new GraphicSheet(
                "res/models/pl_blue_pistol_anim_sprites.png", 128);
    }

    public static GraphicSheet getGraphic(Color color, Weapon weapon, Stance stance) {
        return playerMatrix[color.ordinal()][weapon.ordinal()][stance.ordinal()];
    }

}
