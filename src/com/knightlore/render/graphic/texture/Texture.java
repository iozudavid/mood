package com.knightlore.render.graphic.texture;

import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.GraphicSheet;

public class Texture {

    public static final Graphic AIR = Graphic.EMPTY;
    public static final Graphic BRICK = GraphicSheet.TEXTURES.graphicAt(0, 0);
    public static final Graphic BUSH = GraphicSheet.TEXTURES.graphicAt(1, 0);

    private Texture() {
    }

}
