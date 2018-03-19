package com.knightlore.gui;

import java.util.ArrayList;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.GameState;
import com.knightlore.network.ConnectionDetails;
import com.knightlore.render.PixelBuffer;
import com.knightlore.utils.funcptrs.BooleanFunction;
import com.knightlore.utils.funcptrs.VoidFunction;

public class MultiplayerMenu {
	
	private GUICanvas gui;
	private final int screenHeight;
	private final int screenWidth;
	private Image coverImage;
	private Text ipText;
	private TextField ipTextField;
	private Group groupIp;
	private Text portText;
	private TextField portTextField;
	private Group groupPort;
	private Button connectButton;
	private Button cancelButton;

	public MultiplayerMenu(int screenHeight, int screenWidth){
		this.gui = new GUICanvas(screenWidth, screenHeight);	
		this.gui.init();
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		this.coverImage = new Image(0, 0, this.screenWidth, this.screenHeight, "res/graphics/mppadjusted.png");
		this.gui.addGUIObject(this.coverImage);
		this.ipText = new Text(GuiUtils.middleWidth(this.screenWidth, 100), GuiUtils.calculateHeight(this.screenHeight, 25), 100, 40, "Ip address", 20);
		this.ipTextField = new TextField(GuiUtils.middleWidth(this.screenWidth, 300), GuiUtils.calculateHeight(this.screenHeight, 32), 300, 40, "127.0.0.1");
		this.gui.addGUIObject(this.coverImage);
		ArrayList<GUIObject> objs = new ArrayList<>();
		objs.add(ipText);
		objs.add(ipTextField);
		double ipDiff = 32 - 25;
		this.groupIp = new Group(GuiUtils.minX(objs), GuiUtils.minY(objs), objs, ipDiff*0.01, screenHeight);
		this.gui.addGUIObject(groupIp);
		this.gui.addGUIObject(this.ipText);
		this.gui.addGUIObject(this.ipTextField);
		this.portText = new Text(GuiUtils.middleWidth(this.screenWidth, 50), GuiUtils.calculateHeight(this.screenHeight, 50), 50, 40, "Port", 20);
		this.portTextField = new TextField(GuiUtils.middleWidth(this.screenWidth, 300), GuiUtils.calculateHeight(this.screenHeight, 57), 300, 40, "5000");
		this.portTextField.setRestriction(new BooleanFunction<Character>() {

			@Override
			public boolean check(Character value) {
				return Character.isDigit(value);
			}
		});
		ArrayList<GUIObject> objsPort = new ArrayList<>();
		objsPort.add(portText);
		objsPort.add(portTextField);
		double portDiff = 32 - 25;
		this.groupPort = new Group(GuiUtils.minX(objsPort), GuiUtils.minY(objsPort), objsPort, portDiff*0.01, screenHeight);
		this.gui.addGUIObject(groupPort);
		this.gui.addGUIObject(this.portText);
		this.gui.addGUIObject(this.portTextField);
		this.cancelButton = new Button(GuiUtils.middleWidth(this.screenWidth/2, 300), GuiUtils.calculateHeight(this.screenHeight, 80), 300, 40, "Cancel",20);
		this.connectButton = new Button((int)(GuiUtils.middleWidth(this.screenWidth/2, 300)+this.screenWidth/2), GuiUtils.calculateHeight(this.screenHeight, 80), 300, 40, "Connect",20);
		this.gui.addGUIObject(connectButton);
		this.gui.addGUIObject(cancelButton);
		
		this.cancelButton.clickFunction = new VoidFunction(){

			@Override
			public void call() {
				MultiplayerMenu.this.gui.destroy();
				GameEngine.getSingleton().gameState = GameState.StartMenu;
			}
			
		};
		
		this.connectButton.clickFunction = new VoidFunction() {
			
			@Override
			public void call() {
				ConnectionDetails.PORT = Integer.parseInt(MultiplayerMenu.this.portTextField.getText());
				ConnectionDetails.SERVER_HOSTNAME = MultiplayerMenu.this.ipTextField.getText();
				MultiplayerMenu.this.gui.destroy();
				GameEngine.getSingleton().startGame();
			}
		};
		
	}
	
	public void render(PixelBuffer pix, int x, int y){
		this.gui.render(pix, x, y);
	}
	
	
	
}
