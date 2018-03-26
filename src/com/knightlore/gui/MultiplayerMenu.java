package com.knightlore.gui;

import java.awt.Color;
import java.util.ArrayList;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.ConnectionDetails;
import com.knightlore.render.PixelBuffer;
import com.knightlore.utils.funcptrs.VoidFunction;
/**
 * Creating and rendering all objects on multiplayer menu.
 * @author David Iozu.
 *
 */
public class MultiplayerMenu {

    private final GUICanvas gui;
    private final int screenHeight;
    private final int screenWidth;
    private final Image coverImage;
    private final Text ipText;
    private final TextField ipTextField;
    private final Group groupIp;
    private final Text portText;
    private final TextField portTextField;
    private final Group groupPort;
    private final Button connectButton;
    private final Button cancelButton;
    private final Text noConnection;

    /**
     * SetUp all the GUIObjects needed for MultiplayerMenu
     * 
     * @param screenHeight
     *            - height of the screen
     * @param screenWidth
     *            - width of the screen
     */
    public MultiplayerMenu(int screenHeight, int screenWidth) {
        this.gui = new GUICanvas(screenWidth, screenHeight);
        this.gui.init();
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.coverImage = new Image(0, 0, this.screenWidth, this.screenHeight, "res/graphics/mppadjusted.png");
        this.gui.addGUIObject(this.coverImage);
        this.ipText = new Text(GuiUtils.middleWidth(this.screenWidth, 100),
                GuiUtils.calculateHeight(this.screenHeight, 25), 100, 40, "Ip address", 25);
        this.ipTextField = new TextField(GuiUtils.middleWidth(this.screenWidth, 300),
                GuiUtils.calculateHeight(this.screenHeight, 32), 300, 40, "127.0.0.1");
        this.ipTextField.fontSize = 3;
        this.gui.addGUIObject(this.coverImage);
        ArrayList<GUIObject> objs = new ArrayList<>();
        objs.add(ipText);
        objs.add(ipTextField);
        double ipDiff = 32 - 25;
        this.groupIp = new Group(GuiUtils.minX(objs), GuiUtils.minY(objs), objs, ipDiff * 0.01, screenHeight);
        this.gui.addGUIObject(groupIp);
        this.gui.addGUIObject(this.ipText);
        this.gui.addGUIObject(this.ipTextField);
        this.portText = new Text(GuiUtils.middleWidth(this.screenWidth, 50),
                GuiUtils.calculateHeight(this.screenHeight, 50), 50, 40, "Port", 25);
        this.portTextField = new TextField(GuiUtils.middleWidth(this.screenWidth, 300),
                GuiUtils.calculateHeight(this.screenHeight, 57), 300, 40, "" + ConnectionDetails.DEFAULT_PORT);
        this.portTextField.fontSize = 3;
        this.portTextField.setRestriction(Character::isDigit);
        ArrayList<GUIObject> objsPort = new ArrayList<>();
        objsPort.add(portText);
        objsPort.add(portTextField);
        double portDiff = 32 - 25;
        this.groupPort = new Group(GuiUtils.minX(objsPort), GuiUtils.minY(objsPort), objsPort, portDiff * 0.01,
                screenHeight);
        this.gui.addGUIObject(groupPort);
        this.gui.addGUIObject(this.portText);
        this.gui.addGUIObject(this.portTextField);
        this.cancelButton = new Button(GuiUtils.middleWidth(this.screenWidth / 2, 300),
                GuiUtils.calculateHeight(this.screenHeight, 80), 300, 40, "Cancel", 21);
        this.connectButton = new Button(GuiUtils.middleWidth(this.screenWidth / 2, 300) + this.screenWidth / 2,
                GuiUtils.calculateHeight(this.screenHeight, 80), 300, 40, "Connect", 21);
        this.gui.addGUIObject(connectButton);
        this.gui.addGUIObject(cancelButton);

        this.cancelButton.clickFunction = () -> {
            MultiplayerMenu.this.gui.destroy();
            GameEngine.getSingleton().guiState = GUIState.StartMenu;
        };

        this.connectButton.clickFunction = new VoidFunction() {

            @Override
            public void call() {
                ConnectionDetails.PORT = Integer.parseInt(MultiplayerMenu.this.portTextField.getText());
                ConnectionDetails.SERVER_HOSTNAME = MultiplayerMenu.this.ipTextField.getText();
                try {
                    GameEngine.getSingleton().startGame();
                } catch (Exception e) {
                    MultiplayerMenu.this.gui.addGUIObject(MultiplayerMenu.this.noConnection);
                    return;
                }
                MultiplayerMenu.this.gui.destroy();
            }
        };

        this.noConnection = new Text(GuiUtils.middleWidth(this.screenWidth, 120),
                GuiUtils.calculateHeight(this.screenHeight, 75), 120, 40, "No connection!", 21);
        noConnection.currentColor = Color.RED;

    }

    /**
     * Render actual menu
     * 
     * @param pix
     *            - PixelBuffer we render on
     * @param x
     *            - X position to start render from
     * @param y
     *            - Y position to start render from
     */
    public void render(PixelBuffer pix, int x, int y) {
        this.gui.render(pix, x, y);
    }

}
