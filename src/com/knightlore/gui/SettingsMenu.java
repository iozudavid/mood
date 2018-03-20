package com.knightlore.gui;

import java.util.ArrayList;

import com.knightlore.render.PixelBuffer;
import com.knightlore.render.Renderer;

public class SettingsMenu {

    private GUICanvas gui;
    private int screenWidth;
    private int screenHeight;
    private Image coverImage;
    private Text nameText;
    private TextField nameTextField;
    private Group nameGroup;
    private Text blockinessText;
    private TextField blockinessTextField;
    private Group blockinessGroup;
    private Slider soundSlider;

    public SettingsMenu(int screenWidth, int screenHeight) {
        this.gui = new GUICanvas(screenWidth, screenHeight);
        this.gui.init();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.coverImage = new Image(0, 0, this.screenWidth, this.screenHeight, "res/graphics/settings_modified.jpg");
        this.gui.addGUIObject(this.coverImage);
        this.nameText = new Text(GuiUtils.middleWidth(this.screenWidth, 100),
                GuiUtils.calculateHeight(this.screenHeight, 25), 100, 30, "Username: ", 20);
        this.nameTextField = new TextField(GuiUtils.middleWidth(this.screenWidth, 300),
                GuiUtils.calculateHeight(this.screenHeight, 32), 300, 30, "player");
        this.gui.addGUIObject(this.coverImage);
        ArrayList<GUIObject> objs = new ArrayList<>();
        objs.add(nameText);
        objs.add(nameTextField);
        double nameDiff = 32 - 25;
        this.nameGroup = new Group(GuiUtils.minX(objs), GuiUtils.minY(objs), objs, nameDiff * 0.01, screenHeight);
        this.gui.addGUIObject(nameGroup);
        this.gui.addGUIObject(this.nameText);
        this.gui.addGUIObject(this.nameTextField);
        
        this.blockinessText = new Text(GuiUtils.middleWidth(this.screenWidth, 100),
                GuiUtils.calculateHeight(this.screenHeight, 45), 100, 30, "Blockiness: ", 20);
        this.blockinessTextField = new TextField(GuiUtils.middleWidth(this.screenWidth, 300),
                GuiUtils.calculateHeight(this.screenHeight, 52), 300, 30, Renderer.getBlockiness());
        ArrayList<GUIObject> objsBlock = new ArrayList<>();
        objsBlock.add(blockinessText);
        objsBlock.add(blockinessTextField);
        double blockDiff = 52 - 45;
        this.blockinessGroup = new Group(GuiUtils.minX(objsBlock), GuiUtils.minY(objsBlock), objsBlock, blockDiff * 0.01, screenHeight);
        this.gui.addGUIObject(blockinessGroup);
        this.gui.addGUIObject(this.blockinessText);
        this.gui.addGUIObject(this.blockinessTextField);
        
        this.soundSlider = new Slider(GuiUtils.middleWidth(this.screenWidth, 100),
                GuiUtils.calculateHeight(this.screenHeight, 75), 100, 10);
        this.gui.addGUIObject(soundSlider);
    }

    public void render(PixelBuffer pix, int x, int y) {
        this.gui.render(pix, x, y);
    }

}
