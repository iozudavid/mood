package com.knightlore.gui;

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
		this.pauseImage = new Image(GuiUtils.middleWidth(screenWidth, 150), GuiUtils.calculateHeight(screenHeight, 15), 150, 50, "res/graphics/Pause_final.png");
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
