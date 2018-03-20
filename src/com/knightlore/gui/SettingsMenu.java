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
    private Group soundGroup;
    private Text soundText;
    private Slider soundSlider;
    private Button applyButton;
    private Button cancelButton;

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
                GuiUtils.calculateHeight(this.screenHeight, 52), 300, 30, Renderer.getBlockiness()+"");
        ArrayList<GUIObject> objsBlock = new ArrayList<>();
        objsBlock.add(blockinessText);
        objsBlock.add(blockinessTextField);
        double blockDiff = 52 - 45;
        this.blockinessGroup = new Group(GuiUtils.minX(objsBlock), GuiUtils.minY(objsBlock), objsBlock, blockDiff * 0.01, screenHeight);
        this.gui.addGUIObject(blockinessGroup);
        this.gui.addGUIObject(this.blockinessText);
        this.gui.addGUIObject(this.blockinessTextField);
        
        this.soundText = new Text((int)(this.blockinessGroup.getRectangle().getX())+50,
                GuiUtils.calculateHeight(this.screenHeight, 65), 100, 30, "Sound Volume: ", 20);
        this.soundSlider = new Slider((int)(this.soundText.getRectangle().getX()+(int)(this.soundText.getRectangle().getWidth())+90),
                (int)(this.soundText.getRectangle().getHeight()/2D)+(int)(this.soundText.getRectangle().getY()), 150, 10);
        ArrayList<GUIObject> soundBlock = new ArrayList<>();
        soundBlock.add(soundText);
        soundBlock.add(soundSlider);
        double soundDiff = this.soundSlider.getRectangle().getWidth()*100;
        this.soundGroup = new Group(GuiUtils.minX(soundBlock), GuiUtils.minY(soundBlock), soundBlock, 0, soundDiff * 0.01, screenHeight);
        this.gui.addGUIObject(soundGroup);
        this.gui.addGUIObject(soundText);
        this.gui.addGUIObject(soundSlider);
        
        this.cancelButton = new Button(GuiUtils.middleWidth(this.screenWidth/2, 300), GuiUtils.calculateHeight(this.screenHeight, 80), 300, 40, "Cancel",20);
        this.applyButton = new Button((int)(GuiUtils.middleWidth(this.screenWidth/2, 300)+this.screenWidth/2), GuiUtils.calculateHeight(this.screenHeight, 80), 300, 40, "Apply",20);
        this.gui.addGUIObject(applyButton);
        this.gui.addGUIObject(cancelButton);
    }

    public void render(PixelBuffer pix, int x, int y) {
        this.gui.render(pix, x, y);
    }

}
