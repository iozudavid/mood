package com.knightlore.gui;

import java.util.ArrayList;

import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.Renderer;
import com.knightlore.utils.funcptrs.VoidFunction;

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
    private Group bobGroup;
    private Text bobText;
    private CheckBox bobCheckBox;
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
                GuiUtils.calculateHeight(this.screenHeight, 25), 100, 30, "Username: ", 25);
        this.nameTextField = new TextField(GuiUtils.middleWidth(this.screenWidth, 300),
                GuiUtils.calculateHeight(this.screenHeight, 32), 300, 30, "Client Player");
        this.nameTextField.fontSize=3;
        this.nameTextField.setRestrictionLength((Integer i) -> i<=20);
        this.gui.addGUIObject(this.coverImage);
        ArrayList<GUIObject> objs = new ArrayList<>();
        objs.add(nameText);
        objs.add(nameTextField);
        double nameDiff = 32 - 25;
        this.nameGroup = new Group(GuiUtils.minX(objs), GuiUtils.minY(objs), objs, nameDiff * 0.01, screenHeight);
        this.gui.addGUIObject(nameGroup);
        this.gui.addGUIObject(this.nameText);
        this.gui.addGUIObject(this.nameTextField);
        
        this.blockinessText = new Text(GuiUtils.middleWidth(this.screenWidth, 150),
                GuiUtils.calculateHeight(this.screenHeight, 45), 150, 30, "Blockiness(5-20): ", 25);
        this.blockinessTextField = new TextField(GuiUtils.middleWidth(this.screenWidth, 300),
                GuiUtils.calculateHeight(this.screenHeight, 52), 300, 30, Renderer.getBlockiness()+"");
        this.blockinessTextField.fontSize = 3;
        this.blockinessTextField.setRestriction((Character c) -> Character.isDigit(c));
        this.blockinessTextField.setRestrictionLength((Integer i) -> i<=2);
        this.blockinessTextField.setRestrictionValue((String s) -> Integer.parseInt(s)>=5 && Integer.parseInt(s)<=20);
        ArrayList<GUIObject> objsBlock = new ArrayList<>();
        objsBlock.add(blockinessText);
        objsBlock.add(blockinessTextField);
        double blockDiff = 52 - 45;
        this.blockinessGroup = new Group(GuiUtils.minX(objsBlock), GuiUtils.minY(objsBlock), objsBlock, blockDiff * 0.01, screenHeight);
        this.gui.addGUIObject(blockinessGroup);
        this.gui.addGUIObject(this.blockinessText);
        this.gui.addGUIObject(this.blockinessTextField);
        
        this.soundText = new Text((int)(this.blockinessGroup.getRectangle().getX())+50,
                GuiUtils.calculateHeight(this.screenHeight, 65), 100, 30, "Sound Volume: ", 25);
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
        
        this.bobText = new Text((int)(this.blockinessGroup.getRectangle().getX())+50,
                GuiUtils.calculateHeight(this.screenHeight, 74.5f), 100, 20, "Motion bob: ", 25);
        this.bobCheckBox = new CheckBox((int)(this.bobText.getRectangle().getX()+(int)(this.bobText.getRectangle().getWidth())+90+this.soundSlider.getRectangle().getWidth()/2D),
                (int)(this.bobText.getRectangle().getHeight()/2D)+(int)(this.bobText.getRectangle().getY())-5, 20, 20, 0, true);
        ArrayList<GUIObject> bobBlock = new ArrayList<>();
        bobBlock.add(bobText);
        bobBlock.add(bobCheckBox);
        double bobDiff = this.soundSlider.getRectangle().getWidth()*133.5;
        this.bobGroup = new Group(GuiUtils.minX(bobBlock), GuiUtils.minY(bobBlock), bobBlock, 0, bobDiff * 0.01, screenHeight);
        this.gui.addGUIObject(bobGroup);
        this.gui.addGUIObject(bobText);
        this.gui.addGUIObject(bobCheckBox);
        
        this.cancelButton = new Button(GuiUtils.middleWidth(this.screenWidth/2, 300), GuiUtils.calculateHeight(this.screenHeight, 85), 300, 40, "Cancel",21);
        this.applyButton = new Button((int)(GuiUtils.middleWidth(this.screenWidth/2, 300)+this.screenWidth/2), GuiUtils.calculateHeight(this.screenHeight, 85), 300, 40, "Apply",21);
        this.gui.addGUIObject(applyButton);
        this.gui.addGUIObject(cancelButton);
        
        this.applyButton.clickFunction = new VoidFunction() {
            
            @Override
            public void call() {
                GameSettings.MOTION_BOB = SettingsMenu.this.bobCheckBox.getBobingMode();
                GameEngine.getSingleton().setVolume(SettingsMenu.this.soundSlider.getValue());
                Renderer.setBlockiness(Integer.parseInt(SettingsMenu.this.blockinessTextField.getText()));
                GameSettings.PLAYER_NAME = SettingsMenu.this.nameTextField.getText();
                GameEngine.getSingleton().guiState = GUIState.SettingsMenuApply;
            }
        };
        
        this.cancelButton.clickFunction = new VoidFunction() {
            
            @Override
            public void call() {
                GameEngine.getSingleton().guiState = GUIState.SettingsMenuCancel;                
            }
        };
        
    }
    
    public ArrayList<Object> getObj(){
        ArrayList<Object> obj = new ArrayList<>();
        obj.add(this.nameTextField.getText());
        obj.add(this.blockinessTextField.getText());
        obj.add(this.soundSlider.getValue());
        obj.add(this.bobCheckBox.getBobingMode());
        return obj;
    }

    public void setObj(ArrayList<Object> o){
        this.nameTextField.setText((String)o.get(0));
        this.blockinessTextField.setText((String)o.get(1));
        this.soundSlider.setValue((float)o.get(2));
        this.bobCheckBox.setBobingMode((boolean)o.get(3));
    }
    
    public void render(PixelBuffer pix, int x, int y) {
        this.gui.render(pix, x, y);
    }
    

}
