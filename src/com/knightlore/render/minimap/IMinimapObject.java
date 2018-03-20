package com.knightlore.render.minimap;

import com.knightlore.utils.Vector2D;

public interface IMinimapObject {

    Vector2D getPosition();

    double getDrawSize();

    int getMinimapColor();

}
