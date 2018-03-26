package com.knightlore.gui;

import java.awt.Color;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.ConnectionDetails;
import com.knightlore.render.PixelBuffer;

/**
 * Class to render all START_MENU GUIObjects
 *
 * @author David Iozu
 */
public class StartMenu {
    
    private final GUICanvas gui;
    private final int screenHeight;
    private final int screenWidth;
    private final Image coverImage;
    private final Image name;
    private final Button singlePlayerButton;
    private final Button multiPlayerButton;
    private final Button settingsButton;
    private final Button quitButton;
    private final Text noConnection;
    
    /**
     * SetUp all GUIObjects for START_MENU.
     *
     * @param screenHeight - height of the screen
     * @param screenWidth  - width of the screen.
     */
    public StartMenu(int screenHeight, int screenWidth) {
        this.gui = new GUICanvas();
        this.gui.init();
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.coverImage = new Image(0, 0, screenWidth, screenHeight, "res/graphics/knightlorecoverblur.png");
        this.name = new Image(GuiUtils.middleWidth(this.screenWidth, 500),
                GuiUtils.calculateHeight(this.screenHeight, 10) - 40, 500, 200, "res/graphics/logo.png");
        this.singlePlayerButton = new Button(GuiUtils.middleWidth(this.screenWidth, 300),
                GuiUtils.calculateHeight(this.screenHeight, 40), 300, 40, "Single Player", 21);
        this.singlePlayerButton.setGraphic(new Image(0, 0, 0, 0, "res/graphics/shotgun_to_right.png").graphic);
        this.singlePlayerButton.setGraphic2(new Image(0, 0, 0, 0, "res/graphics/shotgun_to_left.png").graphic);
        this.multiPlayerButton = new Button(GuiUtils.middleWidth(this.screenWidth, 300),
                GuiUtils.calculateHeight(this.screenHeight, 50), 300, 40, "Multiplayer", 21);
        this.multiPlayerButton.setGraphic(new Image(0, 0, 0, 0, "res/graphics/multiplayer_to_right.png").graphic);
        this.multiPlayerButton.setGraphic2(new Image(0, 0, 0, 0, "res/graphics/multiplayer_to_left.png").graphic);
        this.settingsButton = new Button(GuiUtils.middleWidth(this.screenWidth, 300),
                GuiUtils.calculateHeight(this.screenHeight, 60), 300, 40, "Settings", 21);
        this.settingsButton.setGraphic(new Image(0, 0, 0, 0, "res/graphics/settings_to_right.png").graphic);
        this.settingsButton.setGraphic2(new Image(0, 0, 0, 0, "res/graphics/settings_to_left.png").graphic);
        this.quitButton = new Button(GuiUtils.middleWidth(this.screenWidth, 300),
                GuiUtils.calculateHeight(this.screenHeight, 70), 300, 40, "Quit", 21);
        this.quitButton.setGraphic(new Image(0, 0, 0, 0, "res/graphics/quit_to_right.png").graphic);
        this.quitButton.setGraphic2(new Image(0, 0, 0, 0, "res/graphics/quit_to_left.png").graphic);
        
        this.noConnection = new Text(GuiUtils.middleWidth(this.screenWidth, 120),
                GuiUtils.calculateHeight(this.screenHeight, 35), 120, 40, "No connection!", 21);
        noConnection.currentColor = Color.RED;
        
        this.singlePlayerButton.clickFunction = () -> {
            // start new session
            // on the local machine
            ConnectionDetails.SERVER_HOSTNAME = "localhost";
            ConnectionDetails.PORT = ConnectionDetails.DEFAULT_PORT;
            // remove this gui
            try {
                GameEngine.getSingleton().startGame();
            } catch (Exception e) {
                StartMenu.this.gui.addGUIObject(StartMenu.this.noConnection);
                return;
            }
            StartMenu.this.gui.destroy();
        };
        
        this.settingsButton.clickFunction = () -> {
            StartMenu.this.gui.destroy();
            GameEngine.getSingleton().guiState = GUIState.SETTINGS_MENU;
        };
        
        this.multiPlayerButton.clickFunction = () -> {
            StartMenu.this.gui.destroy();
            
            GameEngine.getSingleton().guiState = GUIState.MULTIPLAYER_MENU;
        };
        
        this.quitButton.clickFunction = () -> GameEngine.getSingleton().stop();
        
        gui.addGUIObject(coverImage);
        gui.addGUIObject(name);
        gui.addGUIObject(singlePlayerButton);
        gui.addGUIObject(multiPlayerButton);
        gui.addGUIObject(settingsButton);
        gui.addGUIObject(quitButton);
    }
    
    /**
     * Render actual START_MENU
     *
     * @param pix - PixelBuffer we render on
     * @param x   - X position we start rendering from
     * @param y   - Y position we start rendering from
     */
    public void render(PixelBuffer pix, int x, int y) {
        gui.render(pix, x, y);
    }
    
}
