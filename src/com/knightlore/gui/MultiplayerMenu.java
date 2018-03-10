package com.knightlore.gui;

import java.util.ArrayList;

import com.knightlore.render.PixelBuffer;

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
		this.gui = new GUICanvas();	
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
		ArrayList<GUIObject> objsPort = new ArrayList<>();
		objsPort.add(portText);
		objsPort.add(portTextField);
		double portDiff = 32 - 25;
		this.groupPort = new Group(GuiUtils.minX(objsPort), GuiUtils.minY(objsPort), objsPort, portDiff*0.01, screenHeight);
		this.gui.addGUIObject(groupPort);
		this.gui.addGUIObject(this.portText);
		this.gui.addGUIObject(this.portTextField);
		this.connectButton = new Button(GuiUtils.middleWidth(this.screenWidth/2, 300), GuiUtils.calculateHeight(this.screenHeight, 70), 300, 40, "Connect",20);
		this.cancelButton = new Button(GuiUtils.middleWidth(this.screenWidth/2, 300), GuiUtils.calculateHeight(this.screenHeight, 70), 300, 40, "Cancel",20);
		this.gui.addGUIObject(connectButton);
		this.gui.addGUIObject(cancelButton);
	}
	
	public void render(PixelBuffer pix, int x, int y){
		this.gui.render(pix, x, y);
	}
	
	
	
}
