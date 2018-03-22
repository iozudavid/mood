package com.knightlore.gui;

import java.awt.Color;
import java.util.ArrayList;

import com.knightlore.render.PixelBuffer;

public class Group extends GUIObject{
	
	private ArrayList<GUIObject> objectToGroup;
	private final Color upColor = GuiUtils.makeTransparent(Color.LIGHT_GRAY, 75);
	private final Color hoverColor = GuiUtils.makeTransparent(Color.WHITE, 150);
	private double padding;
	private int screenHeight;
	private SelectState state = SelectState.UP;
	private double widthPadding = 0;
	
	public Group(int x, int y, ArrayList<GUIObject> objs, double padding, int screenHeight){
		super(x-50, y-10, 0, 0);
		this.objectToGroup = objs;
		this.padding = padding;
		this.screenHeight = screenHeight;
		this.recalculate();
	}
	
    public Group(int x, int y, ArrayList<GUIObject> objs, double padding, double widthPadding, int screenHeight) {
        super(x - 50, y - 10, 0, 0);
        this.objectToGroup = objs;
        this.padding = padding;
        this.widthPadding = widthPadding;
        this.screenHeight = screenHeight;
        this.recalculate();
    }

    public void addToGroup(GUIObject g){
		this.objectToGroup.add(g);
		this.recalculate();
	}
	
	public void removeFromGroup(GUIObject g){
		this.objectToGroup.remove(g);
		this.recalculate();
	}

	private void recalculate(){
		this.rect.height = calculateHeight();
		this.rect.width = calculateWidth();
	}

	private int calculateWidth(){
		int width = 0;
		for(GUIObject g : this.objectToGroup){
			width = (int) Math.max(width, g.getRectangle().getWidth());
		}
		width += this.widthPadding;
		width += 100;
		return width;
	}

	
	private int calculateHeight(){
		int height = 0;
		for(GUIObject g : this.objectToGroup){
			height += g.getRectangle().getHeight();
			//add padding between elements
			height += this.padding*this.screenHeight;
		}
		height -= this.padding*this.screenHeight;
		height += 10;
		return height;
	}
	
	void OnMouseExit() {
		this.state = SelectState.UP;
	}
	
	void onMouseEnter() {
		this.state = SelectState.HOVER;
	}
	
	void onMouseOver() {
		this.state = SelectState.HOVER;
	}
	
	void onMouseUp() {
		this.state = SelectState.HOVER;
	}
	
	void onMouseDown() {
		this.state = SelectState.HOVER;
	}
	
	public Color activeColor () {
		switch(state){
			case UP:
				return upColor;

			case HOVER:
				return hoverColor;

		}

		throw new IllegalStateException("State " + state + " is not legal");
	}
	
	boolean isSelectable () {
		return true;
	}

	@Override
	void Draw(PixelBuffer pix, int x, int y) {
		pix.fillRect(activeColor().getRGB(),rect.x, rect.y, rect.width, rect.height);
	}

}
