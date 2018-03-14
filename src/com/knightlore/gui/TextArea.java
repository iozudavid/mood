package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TextArea extends GUIObject{

	private BlockingQueue<String> text;
	private int positionXToRender;
	private int positionYToRender;

	public TextArea(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.text = new LinkedBlockingQueue<>();
		this.positionXToRender = 0;
		this.positionYToRender = 0;
	}
	
	public TextArea(int x, int y, int width, int height, String text) {
		super(x, y, width, height);
		this.text = new LinkedBlockingQueue<>();
		this.text.add(text);
		this.positionXToRender = 0;
		this.positionYToRender = 0;
	}

	private Iterator<String> it = null;
	private Iterator<String> it2 = null;
	
	@Override
	void Draw(Graphics g, Rectangle parentRect) {
		g.setColor(GuiUtils.makeTransparent(new Color(0x1F1F1F),255));
		g.fillRect(this.getRectangle().x, this.getRectangle().y, this.getRectangle().width, this.getRectangle().height);
		this.positionXToRender = (int)this.getRectangle().getX() + 1;
		this.positionYToRender = (int)this.getRectangle().getY()+g.getFontMetrics().getHeight();
		char[] space = new char[1];
		space[0] = ' ';
		g.setColor(Color.white);
		it = this.text.iterator();
		it2 = null;
		while(it.hasNext()){
			if(it2==null){
				it2 = this.text.iterator();
				it2.next();
			}
			String currentText = it.next();
			for(String word : currentText.split(" ")){
				//draw words
				this.fitText(word, g, parentRect);
				//draw space
				g.drawChars(space, 0, 1, positionXToRender, positionYToRender);
				positionXToRender += g.getFontMetrics().charWidth(' ');
			}
			//new line
			this.positionXToRender = (int)this.getRectangle().getX();
			this.positionYToRender += g.getFontMetrics().getHeight();
		}
	}
	
	private void fitText(String word, Graphics g, Rectangle parentRect){
		final int hOffset = g.getFontMetrics().getHeight();
		final int wOffset = g.getFontMetrics().stringWidth(word);
		char[] wordAsArray = word.toCharArray();
		if(wOffset + this.positionXToRender < this.getRectangle().getWidth() &&
				this.positionYToRender < this.getRectangle().getHeight()){
			// everything fits well
			// so just draw it
			g.drawChars(wordAsArray, 0, wordAsArray.length, this.positionXToRender, this.positionYToRender);
			this.positionXToRender += wOffset;
		} else if(hOffset + this.positionYToRender < this.getRectangle().getHeight()){
			// width will exceed
			// so newline
			this.positionXToRender = (int)this.getRectangle().getX();
			this.positionYToRender += hOffset;
			//still bigger
			if(wOffset + this.positionXToRender > this.getRectangle().getWidth()){
				//word is too big
				this.fitBigText(word, g, parentRect);
				return;
			}else{
				//everything good
				//draw it on the next line
				g.drawChars(wordAsArray, 0, wordAsArray.length, this.positionXToRender, this.positionYToRender);
				this.positionXToRender += wOffset;
			}
		} else{
			//textarea full
			//need to rerender everything without first message
			//in order to free some space
			it2.remove();
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
			if(wOffset + this.positionXToRender < this.getRectangle().getWidth() &&
					this.positionYToRender < this.getRectangle().getHeight()){
				// everything fits well
				// so just draw it
				g.drawChars(charAsArray, 0, 1, this.positionXToRender, this.positionYToRender);
				this.positionXToRender += wOffset;
			} else if(hOffset + this.positionYToRender < this.getRectangle().getHeight()){
				// width will exceed
				// so newline
				this.positionXToRender = (int)this.getRectangle().getX();
				this.positionYToRender += hOffset;
				g.drawChars(charAsArray, 0, 1, this.positionXToRender, this.positionYToRender);
				this.positionXToRender += wOffset;
			} else{
				//textarea full
				//need to rerender everything without first message
				//in order to free some space
				it2.remove();
				this.Draw(g, parentRect);
				return;
			}
		}
	}
	
	public void addText(String newText){
		this.text.add(newText);
	}
	
}
