package com.knightlore.gui;

import java.util.ArrayList;

import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.render.PixelBuffer;

/**
 * Class encapsulates all SETTINGS_MENU GUIObjects.
 *
 * @author David Iozu
 */
public class SettingsMenu {
    
    private final GUICanvas gui;
    private final int screenWidth;
    private final int screenHeight;
    private final Image coverImage;
    private final Text nameText;
    private final TextField nameTextField;
    private final Group nameGroup;
    private final Text blockinessText;
    private final Slider blockinessSlider;
    private final Group blockinessGroup;
    private final Group soundGroup;
    private final Text soundText;
    private final Slider soundSlider;
    private final Group bobGroup;
    private final Text bobText;
    private final CheckBox bobCheckBox;
    private final Button applyButton;
    private final Button cancelButton;
    
    /**
     * SetUp all GUIObjects needed for SETTINGS_MENU
     *
     * @param screenWidth  - width of the screen
     * @param screenHeight - height of the screen
     */
    public SettingsMenu(int screenWidth, int screenHeight) {
        this.gui = new GUICanvas();
        this.gui.init();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.coverImage = new Image(0, 0, this.screenWidth, this.screenHeight, "res/graphics/mppadjusted.png");
        this.gui.addGUIObject(this.coverImage);
        this.nameText = new Text(GuiUtils.middleWidth(this.screenWidth, 100),
                GuiUtils.calculateHeight(this.screenHeight, 15), 100, 30, "Username: ", 25);
        this.nameTextField = new TextField(GuiUtils.middleWidth(this.screenWidth, 300),
                GuiUtils.calculateHeight(this.screenHeight, 22), 300, 30, "Client Player");
        this.nameTextField.fontSize = 3;
        this.nameTextField.setRestrictionLength((Integer i) -> i <= 20);
        this.gui.addGUIObject(this.coverImage);
        ArrayList<GUIObject> objs = new ArrayList<>();
        objs.add(nameText);
        objs.add(nameTextField);
        double nameDiff = 22 - 15;
        this.nameGroup = new Group(GuiUtils.minX(objs), GuiUtils.minY(objs), objs, nameDiff * 0.01, screenHeight);
        this.gui.addGUIObject(nameGroup);
        this.gui.addGUIObject(this.nameText);
        this.gui.addGUIObject(this.nameTextField);
        
        this.blockinessText = new Text((int)(this.nameGroup.getRectangle().getX()) + 50,
                GuiUtils.calculateHeight(this.screenHeight, 40), 100, 30, "Blockiness: ", 25);
        this.blockinessSlider = new Slider(
                (int)(this.blockinessText.getRectangle().getX() + (int)(this.blockinessText.getRectangle().getWidth()) + 90),
                (int)(this.blockinessText.getRectangle().getHeight() / 2D) + (int)(this.blockinessText.getRectangle().getY()) - 5,
                150, 10, 0, (float)(GameSettings.desiredBlockiness - 5) / 20F);
        ArrayList<GUIObject> objsBlock = new ArrayList<>();
        objsBlock.add(blockinessText);
        objsBlock.add(blockinessSlider);
        double blockDiff = this.blockinessSlider.getRectangle().getWidth() * 100;
        this.blockinessGroup = new Group(GuiUtils.minX(objsBlock), GuiUtils.minY(objsBlock), objsBlock, 0,
                blockDiff * 0.01, screenHeight);
        this.gui.addGUIObject(blockinessGroup);
        this.gui.addGUIObject(this.blockinessText);
        this.gui.addGUIObject(this.blockinessSlider);
        
        this.soundText = new Text((int)(this.nameGroup.getRectangle().getX()) + 50,
                GuiUtils.calculateHeight(this.screenHeight, 55), 100, 30, "Sound Volume: ", 25);
        this.soundSlider = new Slider(
                (int)(this.soundText.getRectangle().getX() + (int)(this.soundText.getRectangle().getWidth()) + 90),
                (int)(this.soundText.getRectangle().getHeight() / 2D) + (int)(this.soundText.getRectangle().getY()) - 5,
                150, 10);
        ArrayList<GUIObject> soundBlock = new ArrayList<>();
        soundBlock.add(soundText);
        soundBlock.add(soundSlider);
        double soundDiff = this.soundSlider.getRectangle().getWidth() * 100;
        this.soundGroup = new Group(GuiUtils.minX(soundBlock), GuiUtils.minY(soundBlock), soundBlock, 0,
                soundDiff * 0.01, screenHeight);
        this.gui.addGUIObject(soundGroup);
        this.gui.addGUIObject(soundText);
        this.gui.addGUIObject(soundSlider);
        
        this.bobText = new Text((int)(this.nameGroup.getRectangle().getX()) + 50,
                GuiUtils.calculateHeight(this.screenHeight, 70f), 100, 20, "Motion bob: ", 25);
        this.bobCheckBox = new CheckBox(
                (int)(this.bobText.getRectangle().getX() + (int)(this.bobText.getRectangle().getWidth()) + 90
                        + this.soundSlider.getRectangle().getWidth() / 2D),
                (int)(this.bobText.getRectangle().getHeight() / 2D) + (int)(this.bobText.getRectangle().getY()) - 5,
                20, 20, 0, true);
        ArrayList<GUIObject> bobBlock = new ArrayList<>();
        bobBlock.add(bobText);
        bobBlock.add(bobCheckBox);
        double bobDiff = this.soundSlider.getRectangle().getWidth() * 133.5;
        this.bobGroup = new Group(GuiUtils.minX(bobBlock), GuiUtils.minY(bobBlock), bobBlock, 0, bobDiff * 0.01,
                screenHeight);
        this.gui.addGUIObject(bobGroup);
        this.gui.addGUIObject(bobText);
        this.gui.addGUIObject(bobCheckBox);
        
        this.cancelButton = new Button(GuiUtils.middleWidth(this.screenWidth / 2, 300),
                GuiUtils.calculateHeight(this.screenHeight, 85), 300, 40, "Cancel", 21);
        this.applyButton = new Button(GuiUtils.middleWidth(this.screenWidth / 2, 300) + this.screenWidth / 2,
                GuiUtils.calculateHeight(this.screenHeight, 85), 300, 40, "Apply", 21);
        this.gui.addGUIObject(applyButton);
        this.gui.addGUIObject(cancelButton);
        
        this.applyButton.clickFunction = () -> {
            GameSettings.motionBob = SettingsMenu.this.bobCheckBox.getBobingMode();
            GameEngine.getSingleton().setVolume(SettingsMenu.this.soundSlider.getValue());
            GameSettings.desiredBlockiness = (int)(SettingsMenu.this.blockinessSlider.getValue() * 20F);
            GameSettings.playerName = SettingsMenu.this.nameTextField.getText();
            GameEngine.getSingleton().guiState = GUIState.SETTINGS_MENU_APPLY;
        };
        
        this.cancelButton.clickFunction = () -> GameEngine.getSingleton().guiState = GUIState.SETTINGS_MENU_CANCEL;
        
    }
    
    /**
     * Used to save last SETTINGS_MENU config if we cancel actual changes.
     *
     * @return the actual settings menu
     */
    public ArrayList<Object> getObj() {
        ArrayList<Object> obj = new ArrayList<>();
        obj.add(this.nameTextField.getText());
        obj.add(this.blockinessSlider.getValue());
        obj.add(this.soundSlider.getValue());
        obj.add(this.bobCheckBox.getBobingMode());
        return obj;
    }
    
    /**
     * Set config for Settings. Used when we cancel actual changes and we want
     * our old config.
     *
     * @param o - config to be set.
     */
    public void setObj(ArrayList<Object> o) {
        this.nameTextField.setText((String)o.get(0));
        this.blockinessSlider.setValue((float)o.get(1));
        this.soundSlider.setValue((float)o.get(2));
        this.bobCheckBox.setBobingMode((boolean)o.get(3));
    }
    
    /**
     * Render actual SETTINGS_MENU
     *
     * @param pix - PixelBuffer we render on
     * @param x   - X position we start rendering from
     * @param y   - Y position we start rendering from
     */
    public void render(PixelBuffer pix, int x, int y) {
        this.gui.render(pix, x, y);
    }
    
}
