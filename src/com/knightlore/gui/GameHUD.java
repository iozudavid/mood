package com.knightlore.gui;

public class GameHUD extends GUICanvas {

    private Text timeLeftText;

    public GameHUD(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight);

        timeLeftText = new Text(0, 0, 128, 30, null, 30);
        addGUIObject(timeLeftText);
        timeLeftText.SetText("00:00");
    }

    public void setTimeLeft(String time) {
        timeLeftText.SetText(time);
    }

}
