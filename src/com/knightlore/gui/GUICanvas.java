package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.knightlore.engine.GameObject;
import com.knightlore.engine.input.InputManager;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.Screen;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.utils.Physics;
import com.knightlore.utils.Vector2D;

public class GUICanvas extends GameObject implements IRenderable {

	private ArrayList<GUIObject> guis;
	
	private Graphic canvasGraphic;
	private BufferedImage canvasImage;
	private Graphics2D canvasG2D;
	
	public GUICanvas (){
		super();
		guis = new ArrayList<GUIObject>();
		canvasImage = new BufferedImage(300,300, BufferedImage.TYPE_INT_ARGB);
		canvasGraphic = new Graphic(canvasImage);
		canvasG2D = canvasImage.createGraphics();
	}
	
	@Override
	public void render(PixelBuffer pix, int x, int y) {
		// TODO Auto-generated method stub
		canvasG2D.setColor(Color.PINK);
		canvasG2D.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
		for(int i=0;i<guis.size();i++){
			guis.get(i).Draw(canvasG2D);
		}
		
		pix.drawGraphic(canvasGraphic, x, y);
	}

	@Override
	public void onCreate() {
		
	}

	private GUIObject lastSelected;
	private GUIObject selected;
	
	@Override
	public void onUpdate() {
		Vector2D mousePos = InputManager.getMousePos();
		
		selected = null;
		// linear reverse search
		for(int i=guis.size()-1;i>0;i--){
			GUIObject gui = guis.get(i);
			// skip those that aren't selectable
			if(!gui.isSelectable()){
				continue;
			}
			if(Physics.pointInAWTRectangleTest(mousePos, gui.rect)){
				selected = gui;
				break;
				
			}
		}
		
		// notify previous gui of mouse exit
		if(lastSelected != selected){
			if(lastSelected != null){
				lastSelected.OnMouseExit();
			}
			lastSelected = selected;
		}
		
		// notify new gui of mouse enter
		if(selected != null){
			selected.OnMouseEnter();
		}
	}

	@Override
	public void onDestroy() {
		canvasG2D.dispose();
	}
	
	private void sort(){
		for(int i=1;i<guis.size();i++){
			GUIObject left = guis.get(i);
			GUIObject right = guis.get(i-1);
			// swap
			if(left.depth < right.depth){
				guis.set(i, right);
				guis.set(i-1, left);
			}
		}
	}

	public void addGUIObject(GUIObject gui){
		guis.add(gui);
		sort();
	}
	
	public void removeGUIObject(GUIObject gui){
		guis.remove(gui);
	}
	
}
