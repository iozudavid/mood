package com.knightlore.gui;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.GameState;
import com.knightlore.network.client.ClientManager;
import com.knightlore.render.PixelBuffer;
import com.knightlore.utils.funcptrs.VoidFunction;

public class GameChat {

	private GUICanvas gui;
	private TextArea textArea;
	private TextField textField;
	private PixelBuffer pix;
	private int screenWidth;
	private int screenHeight;
	private int count=0;
	private boolean interactive=true;
	private final int timeToInteractive = 200;
	private Image pauseImage;
	private Button resumeButton;
	private Button mainMenuButton;
	private Button exitButton;
	private boolean lastVisible=false;
	private Image scoreBoardImage;
	private Table scoreBoard;
	
	public GameChat(int screenWidth, int screenHeight){
		this.gui = new GUICanvas((int)(screenWidth),(int)(screenHeight));
		this.gui.init();
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.pix = new PixelBuffer((int)(screenWidth),(int)(screenHeight));
		this.pix.flood(-16711936);
		this.textArea = new TextArea(0,0,(int)(screenWidth*0.3),(int)(screenHeight*0.3));
		this.textField = new TextField(0, (int)(this.textArea.getRectangle().getY()+this.textArea.getRectangle().getHeight()), (int)(screenWidth*0.3), (int)(screenHeight*0.05));
		this.textField.setSelect(false);
		this.gui.addGUIObject(this.textArea);
		this.gui.addGUIObject(this.textField);
		this.textArea.addText("System: access team chat by pressing t");
		this.textArea.addText("System: access all chat by pressing y");
		this.textArea.addText("System: exit chat by pressing esc");
		this.pauseImage = new Image(GuiUtils.middleWidth(screenWidth, 150), GuiUtils.calculateHeight(screenHeight, 15), 150, 50, "res/graphics/Pause.png");
		pauseImage.needBackground=true;
		this.scoreBoardImage = new Image(GuiUtils.middleWidth(screenWidth, 150), GuiUtils.calculateHeight(screenHeight, 10), 150, 50, "res/graphics/ScoreBoard.png");	
		scoreBoardImage.needBackground=true;
		this.gui.addGUIObject(scoreBoardImage);
		this.scoreBoard = new Table(GuiUtils.middleWidth(screenWidth, (int)(screenWidth*0.66)), GuiUtils.calculateHeight(screenHeight, 20), (int)(screenWidth*0.66), 20, 0, 3);
		ArrayList<String> header = new ArrayList<>();
		header.add("Player Name");
		header.add("Team");
		header.add("Score");
		CopyOnWriteArrayList<CopyOnWriteArrayList<String>> entries = new CopyOnWriteArrayList<>();
		CopyOnWriteArrayList<String> entry1 = new CopyOnWriteArrayList<>();
		entry1.add("Shackky");
		entry1.add("Blue");
		entry1.add("100");
		entries.add(entry1);
		CopyOnWriteArrayList<String> entry2 = new CopyOnWriteArrayList<>();
		entry2.add("Shackky2");
		entry2.add("Red");
		entry2.add("1000");
		entries.add(entry2);
		CopyOnWriteArrayList<String> entry3 = new CopyOnWriteArrayList<>();
		entry3.add("Shackky3");
		entry3.add("Blue");
		entry3.add("10000");
		entries.add(entry3);
		this.scoreBoard.setTableHeader(header);
		this.scoreBoard.addTableEntry(entry2);
		this.scoreBoard.addTableEntry(entry1);
		this.scoreBoard.addTableEntry(entry3);
		this.gui.addGUIObject(scoreBoard);
		GUICanvas.setOnEscFunction(new VoidFunction() {
			
			@Override
			public void call() {
				GameChat.this.setPauseMenuVisible(!GameChat.this.lastVisible);
			}
		});
		
	}
	
	public void initHidden(){
		this.resumeButton = new Button(GuiUtils.middleWidth(screenWidth, 300), GuiUtils.calculateHeight(screenHeight, 30), 300, 40, "Resume", 20);
		this.mainMenuButton = new Button(GuiUtils.middleWidth(screenWidth, 300), GuiUtils.calculateHeight(screenHeight, 40), 300, 40, "Main Menu", 20);
		this.exitButton = new Button(GuiUtils.middleWidth(screenWidth, 300), GuiUtils.calculateHeight(screenHeight, 50), 300, 40, "Exit", 20);
		this.resumeButton.clickFunction = new VoidFunction() {
			
			@Override
			public void call() {
				GameChat.this.setPauseMenuVisible(false);
			}
		};
		this.mainMenuButton.clickFunction = new VoidFunction() {
			
			@Override
			public void call() {
				ClientManager.disconnect();
				GameChat.this.gui.destroy();
				GameEngine.getSingleton().gameState=GameState.StartMenu;
			}
		};
		this.exitButton.clickFunction = new VoidFunction() {
			
			@Override
			public void call() {
				ClientManager.disconnect();
				GameChat.this.gui.destroy();
				GameEngine.getSingleton().stop();
			}
		};
	}
	
	public void render(){
		if(GUICanvas.activeTextField!=null){
			this.interactive=true;
			count=0;
		} else if(count>this.timeToInteractive){
			this.interactive = false;
			count=0;
		} 
		if(this.interactive==true)
			count++;
		this.textArea.setActive(GUICanvas.activeTextField!=null);
		if(GUICanvas.activeTextField!=null)
			this.textArea.setInteractive(true);
		else
			this.textArea.setInteractive(this.interactive);
		this.gui.render(pix,0,0);
	}
	
	public PixelBuffer getPixelBuffer(){
		PixelBuffer copy = this.pix;
		this.pix = new PixelBuffer((int)(screenWidth),(int)(screenHeight));
		this.pix.flood(-16711936);
		return copy;
	}
	
	public TextArea getTextArea(){
		this.interactive=true;
		this.count=0;
		return this.textArea;
	}
	
	public void setPauseMenuVisible(boolean b){
		this.lastVisible = b;
		if(b){
			this.initHidden();
			this.gui.addGUIObject(pauseImage);
			this.gui.addGUIObject(resumeButton);
			this.gui.addGUIObject(mainMenuButton);
			this.gui.addGUIObject(exitButton);
		}else{
			this.gui.removeGUIObject(pauseImage);
			this.gui.removeGUIObject(resumeButton);
			this.gui.removeGUIObject(mainMenuButton);
			this.gui.removeGUIObject(exitButton);
			this.resumeButton=null;
			this.mainMenuButton=null;
			this.exitButton=null;
		}
	}
	
}
