package com.knightlore.game.world;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.menus.Menus;
import com.knightlore.gui.Button;
import com.knightlore.gui.GUICanvas;
import com.knightlore.gui.GUIPanel;
import com.knightlore.gui.TextField;
import com.knightlore.render.Environment;

public class ClientWorld extends GameWorld {

    private GUICanvas gui;

    public ClientWorld() {
        super();
    }

    public void addEntity(Entity ent) {
        this.ents.add(ent);
    }

    public void removeEntity(Entity ent) {
        this.ents.remove(ent);
    }

    public Environment getEnvironment() {
        return Environment.LIGHT_OUTDOORS;
    }

    @Override
    public void setUpWorld() {
        buildGUI();
    }

    public void buildGUI() {
     // setup testing ui
        gui = new GUICanvas();
        GUIPanel optionsPanel = new GUIPanel(0,0,150,150);
        Button b = new Button(5, 5,100,30);
        optionsPanel.AddGUIObject(b);
        Menus.getOptionsMenu().setPanel(optionsPanel);
        b.clickFunction = Menus.getOptionsMenu()::openMenu;
        TextField tf = new TextField(100, 100, 150,30);
        tf.setText("Sample Text");
        tf.rect.width = 150;
        tf.rect.height = 30;
        gui.addGUIObject(tf);
        gui.addGUIObject(b);
  //      GameEngine.getSingleton().getRenderer().setGUI(gui);
    }

}
