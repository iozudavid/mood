package com.knightlore.game.tile;

import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.texture.Texture;

public class ExpectRoom extends Tile {
    private char direction;

    public static final char RIGHT = 'r';
    public static final char DOWN = 'd';
    public static final char BOTH = 'b';

    public ExpectRoom(char c) {
        assert (c == RIGHT || c == DOWN || c == BOTH);
        direction = c;
    }

    @Override
    public Graphic getTexture() {
        return Texture.AIR;
    }

    @Override
    public void onShot() {
    }

    @Override
    public void onEntered(Entity entity) {
    }

    public char toChar() {
        return direction;
    }

    @Override
    public Tile copy() {
        return new ExpectRoom(direction);
    }
}
