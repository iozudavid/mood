package com.knightlore.render.minimap;

import com.knightlore.utils.Vector2D;

public interface IMinimapObject {

    public Vector2D getPosition();

    public int getDrawSize();

    public int getMinimapColor();

}
