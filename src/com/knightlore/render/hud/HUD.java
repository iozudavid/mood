package com.knightlore.render.hud;

import com.knightlore.game.Player;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.font.Font;

public class HUD {

    private PixelBuffer display;

    private Player myPlayer;

    private HealthCounter healthCounter;
    private Compass compass;

    public HUD(Player myPlayer, int width, int height) {
        this.myPlayer = myPlayer;
        display = new PixelBuffer(width, height);
        healthCounter = new HealthCounter();
        compass = new Compass();
        healthCounter.setPlayer(myPlayer);
        compass.setPlayer(myPlayer);
    }

    public void render() {
        display.flood(0x1F1F1F);

        final int compassX = 10;
        final int compassY = (int) (display.getHeight() / 2 - compass.compassSprite.getHeight() / 2.5D);
        compass.render(display, compassX, compassY);

        healthCounter.render(display, 0, 0);

        final int scoreX = display.getWidth() - 150;
        final int scoreY = display.getHeight() / 2 - 7;
        display.drawString(Font.DEFAULT_WHITE, String.format("SCORE %d", myPlayer.getScore()), scoreX, scoreY, 2, 2);
    }

    public PixelBuffer getPixelBuffer() {
        return display;
    }

}
