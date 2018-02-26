package com.knightlore.render;

/**
 * Something that can be rendered on the screen.
 * 
 * @author Joe Ellis
 *
 */
public interface IRenderable {

    void render(PixelBuffer pix, int x, int y);

}
