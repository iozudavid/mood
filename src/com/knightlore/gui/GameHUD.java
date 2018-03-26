package com.knightlore.gui;

/**
 * Let the user know game time left.
 *
 * @author David Iozu
 */
public class GameHUD extends GUICanvas {
    
    private final Text timeLeftText;
    
    public GameHUD(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight);
        
        timeLeftText = new Text(0, 0, 128, 30, null, 30);
        addGUIObject(timeLeftText);
        timeLeftText.SetText("00:00");
    }
    
    /**
     * Set time left for this game.
     *
     * @param time - set game time
     */
    public void setTimeLeft(String time) {
        timeLeftText.SetText(time);
    }
    
}
