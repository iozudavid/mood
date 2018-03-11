package com.knightlore.gui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class TextArea extends GUIObject{

	private ArrayList<String> text;
	private int positionXToRender;
	private int positionYToRender;

	public TextArea(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.text = new ArrayList<>();
		this.positionXToRender = 0;
		this.positionYToRender = 0;
	}
	
	public TextArea(int x, int y, int width, int height, String text) {
		super(x, y, width, height);
		this.text = new ArrayList<>();
		this.text.add(text);
		this.positionXToRender = 0;
		this.positionYToRender = 0;
	}

	@Override
	void Draw(Graphics g, Rectangle parentRect) {
		g.drawRect(this.getRectangle().x, this.getRectangle().y, this.getRectangle().width, this.getRectangle().height);
		this.positionXToRender = 0;
		this.positionYToRender = 0;
		char[] space = new char[1];
		space[0] = ' ';
		for(String currentText : this.text){			
			for(String word : currentText.split(" ")){
				//draw words
				this.fitText(word, g, parentRect);
				//draw space
				g.drawChars(space, 0, 1, positionXToRender, positionYToRender);
				positionXToRender += g.getFontMetrics().charWidth(' ');
				positionYToRender += g.getFontMetrics().getHeight();
			}
			//new line
			this.positionXToRender = 0;
			this.positionYToRender += g.getFontMetrics().getHeight();
		}
	}
	
	private void fitText(String word, Graphics g, Rectangle parentRect){
		final int hOffset = g.getFontMetrics().getHeight();
		final int wOffset = g.getFontMetrics().stringWidth(word);
		char[] wordAsArray = word.toCharArray();
		if(wOffset + this.positionXToRender < this.getRectangle().getWidth()){
			// everything fits well
			// so just draw it
			g.drawChars(wordAsArray, 0, wordAsArray.length, this.positionXToRender, this.positionYToRender);
			this.positionXToRender += wOffset;
			this.positionYToRender += hOffset;
		} else if(hOffset + this.positionYToRender < this.getRectangle().getHeight()){
			// width will exceed
			// so newline
			this.positionXToRender = 0;
			this.positionYToRender += hOffset;
			//still bigger
			if(wOffset + this.positionXToRender > this.getRectangle().getWidth()){
				//word is too big
				this.fitBigText(word, g, parentRect);
				return;
			}else
				//everything good
				//draw it on the next line
				g.drawChars(wordAsArray, 0, wordAsArray.length, this.positionXToRender, this.positionYToRender);
				this.positionXToRender += wOffset;
				this.positionYToRender += hOffset;
		} else{
			//textarea full
			//need to rerender everything without first message
			//in order to free some space
			this.text.remove(0);
			this.Draw(g, parentRect);
			return;
		}
	}
	
	//word too big
	//render as many chars as possible
	//and go to next line
	private void fitBigText(String word, Graphics g, Rectangle parentRect){
		char[] wordAsArray = word.toCharArray();
		for(char c : wordAsArray){
			final int hOffset = g.getFontMetrics().getHeight();
			final int wOffset = g.getFontMetrics().charWidth(c);
			char[] charAsArray = new char[1];
			charAsArray[0] = c;
			if(wOffset + this.positionXToRender < this.getRectangle().getWidth()){
				// everything fits well
				// so just draw it
				g.drawChars(charAsArray, 0, 1, this.positionXToRender, this.positionYToRender);
				this.positionXToRender += wOffset;
				this.positionYToRender += hOffset;
			} else if(hOffset + this.positionYToRender < this.getRectangle().getHeight()){
				// width will exceed
				// so newline
				this.positionXToRender = 0;
				this.positionYToRender += hOffset;
				g.drawChars(wordAsArray, 0, wordAsArray.length, this.positionXToRender, this.positionYToRender);
				this.positionXToRender += wOffset;
				this.positionYToRender += hOffset;
			} else{
				//textarea full
				//need to rerender everything without first message
				//in order to free some space
				this.text.remove(0);
				this.Draw(g, parentRect);
				return;
			}
		}
	}
	
	public void addText(String newText){
		this.text.add(newText);
	}
	
}
