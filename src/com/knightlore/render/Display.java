package com.knightlore.render;

import com.knightlore.render.hud.HUD;
import com.knightlore.render.minimap.Minimap;

public class Display implements IRenderable {

    private Renderer renderer;
    private Minimap minimap;
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
