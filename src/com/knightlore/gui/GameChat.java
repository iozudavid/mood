package com.knightlore.gui;

import java.awt.Color;
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
	private boolean lastPauseVisible=false;
	private boolean lastScoreVisible=false;
	private Image scoreBoardImage;
	private Table scoreBoard;
	private Text timeLeftText;
	
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
		this.textArea.addText("System: acces pause menu by pressing esc");
		this.textArea.addText("System: acces scoreboard menu by pressing and holding q");
		this.pauseImage = new Image(GuiUtils.middleWidth(screenWidth, 150), GuiUtils.calculateHeight(screenHeight, 15), 150, 50, "res/graphics/Pause.png");
		pauseImage.needBackground=true;
		this.scoreBoardImage = new Image(GuiUtils.middleWidth(screenWidth, 150), GuiUtils.calculateHeight(screenHeight, 10), 150, 50, "res/graphics/ScoreBoard.png");	
		scoreBoardImage.needBackground=true;
		this.scoreBoard = new Table(GuiUtils.middleWidth(screenWidth, (int)(screenWidth*0.66)), GuiUtils.calculateHeight(screenHeight, 20), (int)(screenWidth*0.66), 20, 0);
		ArrayList<String> header = new ArrayList<>();
		header.add("Player Name");
		header.add("Team");
		header.add("Score");
		this.scoreBoard.setTableHeader(header);
		timeLeftText = new Text(GuiUtils.middleWidth(screenWidth, 128), 0, 128, 30, null, 30);
		timeLeftText.currentColor=Color.WHITE;
        gui.addGUIObject(timeLeftText);
        timeLeftText.SetText("00:00");
		GUICanvas.setOnEscFunction(new VoidFunction() {
			
			@Override
			public void call() {
				GameChat.this.setPauseMenuVisible(!GameChat.this.lastPauseVisible);
			}
		});
		
		GUICanvas.setOnQFunction(new VoidFunction() {
			
			@Override
			public void call() {
				if(GameChat.this.lastScoreVisible)
					return;
				GameChat.this.lastScoreVisible=true;
				GameChat.this.setScoreMenuVisible();
			}
		});
		
		GUICanvas.setOnQReleaseFunction(new VoidFunction() {
			
			@Override
			public void call() {
				if(!GameChat.this.lastScoreVisible)
					return;
				GameChat.this.lastScoreVisible=false;
				GameChat.this.setScoreMenuVisible();
			}
		});
		
	}
	
	public void initPauseHidden(){
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
		if(this.interactive) {
            count++;
        }
		this.textArea.setActive(GUICanvas.activeTextField!=null);
		if(GUICanvas.activeTextField!=null) {
            this.textArea.setInteractive(true);
        } else {
            this.textArea.setInteractive(this.interactive);
        }
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
	
	public void addToTable(CopyOnWriteArrayList<String> entry){
		this.scoreBoard.addTableEntry(entry);
	}
	
	public void removeFromTable(String uuid){
		this.scoreBoard.removeTableEntry(uuid);
	}
	
	public void setPauseMenuVisible(boolean b){
		this.lastPauseVisible = b;
		if(b){
			this.initPauseHidden();
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
	
	public void setScoreMenuVisible(){
		if(this.lastScoreVisible){
			this.gui.addGUIObject(scoreBoardImage);
			this.gui.addGUIObject(scoreBoard);
		}else{
			this.gui.removeGUIObject(scoreBoardImage);
			this.gui.removeGUIObject(scoreBoard);
		}
	}
	
    public void setTimeLeft(String time) {
        timeLeftText.SetText(time);
    }
	
}
