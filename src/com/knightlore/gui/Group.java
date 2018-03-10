package com.knightlore.gui;

import java.util.ArrayList;

public class Group extends GUIObject{
	
	private ArrayList<GUIObject> objectToGroup;
	
	public Group(ArrayList<GUIObject> objs){
		super(0,0,0,0);
		this.objectToGroup = objs;
	}

	private int calculateWidth(){
		int width = 0;
		for(GUIObject g : this.objectToGroup){
			width += g.getRectangle().getWidth();
		}
		//add 5 before
		//and 5 after
		width += 2*5;
		return width;
	}

	
	private int calculateHeight(){
		int height = 0;
		for(GUIObject g : this.objectToGroup){
			height += g.getRectangle().getHeight();
		}
		//add 5 before
		//and 5 after
		height += 2*5;
		return height;
	}

}
