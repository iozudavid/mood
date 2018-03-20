package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.engine.GameState;
import com.knightlore.engine.input.InputManager;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.client.ClientNetworkObjectManager;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.utils.Vector2D;

public class TextField extends GUIObject {
    private static final Color upColour = Color.WHITE;
    private static final Color downColour = Color.LIGHT_GRAY;
    private static final Color hoverColour = Color.GRAY;

	private SelectState state = SelectState.UP;
	private String text;
	private String insertString;
	private char[] rawChars;
	private int insertPosition = 0;
	private Graphics g;
	private char sendTo;
	private boolean select = true; 
		
	public TextField(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    TextField(int x, int y, int width, int height, int depth) {
        super(x, y, width, height, depth);
    }
	
    public TextField(int x, int y, int width, int height, String text) {
    	this(x, y, width, height, 0, text);
    }

    public void setText(String newText){
    	text = newText;
    	displayText(text);
   	}

	
	public TextField(int x, int y, int width, int height, int depth, String text) {
		super(x, y, width, height, depth);
		setText(text);
		displayText(text);
	}
	
	public String getText() {
	    return text;
	}
	
	public void displayText(String t){
		rawChars = t.toCharArray();
	}
	
	public Color activeColor () {
		switch(state){
			case UP:
				return upColour;

			case HOVER:
				return hoverColour;

			case DOWN:
				return downColour;
		}

		throw new IllegalStateException("State " + state + " is not legal");
	}
	
	@Override
	void Draw(Graphics g, Rectangle parentRect) {
		if((GameEngine.getSingleton().gameState==GameState.InGame) && GUICanvas.activeTextField==null)
			return;
		// draw a background
		g.setColor(Color.DARK_GRAY);
    	g.fillRect(rect.x-2, rect.y-2, rect.width+2, rect.height+2);
    	g.setColor(Color.BLACK);
    	g.fillRect(rect.x-1, rect.y-1, rect.width+1, rect.height+1);
    	g.setColor(activeColor());
		int hOffset = g.getFontMetrics().getHeight();
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
		// draw the characters of the string
		g.setColor(Color.BLACK);
		
		if(text!=null){
			int width = g.getFontMetrics().charsWidth(rawChars, 0, rawChars.length);
			if(width<this.rect.width)
				g.drawChars(rawChars, 0, rawChars.length, rect.x, rect.y+hOffset);
			else{
				width = g.getFontMetrics().charsWidth(rawChars, 0, rawChars.length);
				char[] toDisplay = rawChars;
				while(width>this.rect.width){
					toDisplay = new char[toDisplay.length-1];
					for(int i = toDisplay.length-1; i>=0; i--){
						int j = (toDisplay.length-1)-i;
						System.out.println(toDisplay.length);
						System.out.println(rawChars.length);
						toDisplay[i] = rawChars[rawChars.length-1-j];
					}
					width = g.getFontMetrics().charsWidth(toDisplay, 0, toDisplay.length);
				}
				g.drawChars(toDisplay, 0, toDisplay.length, rect.x, rect.y+hOffset);
					
			}
		}
		this.g=g;
	}
	
	@Override
	boolean isSelectable() {
		return select;
	}
	
	public void setSelect(boolean select){
		this.select = select;
	}
	
	@Override
	void onGainedFocus() {
		System.out.println("GAINED FOCUS");
		GUICanvas.setActiveTextField(this);
		
	}
	
	@Override
	void onLostFocus() {
		System.out.println("LOST FOCUS");
		GUICanvas.activeTextField = null;
		displayText(text);
	}

	void onInputChar(char c) {
		if(text.length()==0)
			insertString = c + "|";
		else
			insertString = text.substring(0, insertPosition)+c+'|'+text.substring(insertPosition);
		text = insertString.replace("|", "");
		insertPosition++;
		displayText(insertString);
	}
	
	void onMessage(char c) {
		this.sendTo = c;
		if(text==null || text.length()==0)
			insertString = "|";
		else
			insertString = text.substring(0, insertPosition)+'|'+text.substring(insertPosition);
		text = insertString.replace("|", "");
		displayText(insertString);
	}
	
	void onSendMessage(char c) {
	    // do not send if null or nothing to send
	    if(text == null || text.length() == 0) {
	        return;
	    }
		ClientNetworkObjectManager manager = (ClientNetworkObjectManager) GameEngine.getSingleton().getNetworkObjectManager();
		ByteBuffer nextMessage = constructMessage(manager.getMyPlayer().getObjectId());
		manager.addToChat(nextMessage);
		this.insertPosition=0;
		this.insertString="|";
		this.text=this.insertString.replaceAll("|", "");
		displayText(insertString);
	}
	
	void escape() {
		this.insertPosition=0;
		this.insertString="";
		this.text=this.insertString.replaceAll("|", "");
		displayText(insertString);
	}
	
	public ByteBuffer constructMessage(UUID uuid){
		ByteBuffer bf = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
		NetworkUtils.putStringIntoBuf(bf, uuid.toString());
		if(this.sendTo=='t')
			NetworkUtils.putStringIntoBuf(bf, "messageToTeam");
		else
			NetworkUtils.putStringIntoBuf(bf, "messageToAll");
		NetworkUtils.putStringIntoBuf(bf, this.text);
		return bf;
	}
	
	void onLeftArrow() {
		insertPosition --;
		if(insertPosition < 0){
			insertPosition = 0;
		}

		insertString = text.substring(0, insertPosition)+'|'+text.substring(insertPosition);
		displayText(insertString);
	}
	
	void onRightArrow () {
		insertPosition++;
		if(insertPosition > text.length()){
			insertPosition = text.length();
		}
		insertString = text.substring(0, insertPosition)+'|'+text.substring(insertPosition);
		displayText(insertString);
	}
	
	void onDeleteChar(){
		if(text.length()==0 || insertPosition==0)
			return;
		else if(insertPosition >= text.length()){
			insertPosition = text.length();
			if(text.length()==1){
				insertPosition = 0;
				insertString = "|";
			}
			else{
				insertString = text.substring(0,insertPosition-1)+'|';
				insertPosition--;
			}
			displayText(insertString);
		} else {
			insertString = text.substring(0, insertPosition - 1) + '|' + text.substring(insertPosition);
			insertPosition--;
			displayText(insertString);
		}
		text = insertString.replace("|", "");
	}
	
	@Override
	void onMouseEnter() {
		state = SelectState.HOVER;
	}
	
	void onMouseOver() {
		if(state == SelectState.UP) {
			state = SelectState.HOVER;
		}
	}
	
	@Override
	void OnMouseExit() {
		state = SelectState.UP;
	}
	
	@Override
	void onMouseDown() {
		Vector2D mousePos = InputManager.getMousePos();
		double deltaPos = mousePos.getX()-this.rect.x;
		int chooseLocation = 0;
		if(this.text==null){
			this.text = "";
			this.insertPosition = 0;
			
		}else{
		for(char c : this.text.toCharArray()){
			chooseLocation++;
			int width = this.g.getFontMetrics().charWidth(c);
			double oldDelta = deltaPos;
			deltaPos -= width;
			if(deltaPos<0){
				if(oldDelta<Math.abs(deltaPos))
					this.insertPosition = --chooseLocation;
				else
					this.insertPosition = chooseLocation;
				break;
			}
		}
		if(deltaPos>0)
			this.insertPosition = text.length();
		}
		insertString = text.substring(0, chooseLocation) + "|" + text.substring(chooseLocation);
		displayText(insertString);
		state = SelectState.DOWN;
	}
	
	@Override
	void onMouseUp() {
		state = SelectState.UP;
	}
}
