package com.knightlore.render;

import com.knightlore.render.hud.HUD;
import com.knightlore.render.minimap.Minimap;

/**
 * The display represents everything that is rendered onto the game canvas. The
 * game renderer, minimap and HUD are encapsulated here. The Display class is
 * responsible for compositing the individual screen components onto the main
 * pixel buffer.
 * 
 * @author Joe Ellis
 *
 */
public class Display implements IRenderable {

    /**
     * The renderer -- renders the game world and the perspective of the camera.
     */
    private Renderer renderer;

    /**
     * The minimap -- renders the minimap.
     */
    private Minimap minimap;

    /**
     * The HUD -- renders player health, etc.
     */
    private HUD hud;

    public Display(Renderer renderer, Minimap minimap, HUD hud) {
        this.renderer = renderer;
        this.minimap = minimap;
        this.hud = hud;
    }

    @Override
    public void render(PixelBuffer pix, int x, int y) {
        renderer.render();
        minimap.render();
        hud.render();

        final int w = pix.getWidth(), h = pix.getHeight();
        pix.composite(renderer.getPixelBuffer(), x, y);

        PixelBuffer minimapBuffer = minimap.getPixelBuffer();
        pix.composite(minimapBuffer, x + w - minimapBuffer.getWidth(), y);

        PixelBuffer hudBuffer = hud.getPixelBuffer();
        pix.composite(hudBuffer, x, y + renderer.getPixelBuffer().getHeight());

        GameFeed.getInstance().render(pix, x, y);
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public Minimap getMinimap() {
        return minimap;
    }

    public HUD getHud() {
        return hud;
    }

}
