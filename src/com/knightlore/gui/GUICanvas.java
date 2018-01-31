package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.knightlore.engine.GameObject;
import com.knightlore.engine.input.InputManager;
import com.knightlore.render.IRenderable;
import com.knightlore.render.Screen;
import com.knightlore.render.sprite.Texture;
import com.knightlore.utils.Physics;
import com.knightlore.utils.Vector2D;

public class GUICanvas extends GameObject implements IRenderable {

	private ArrayList<GUIObject> guis;
	
	private Texture canvasTexture;
	private BufferedImage canvasImage;
	private Graphics2D canvasGraphics;
	
	public GUICanvas (){
		super();
		guis = new ArrayList<GUIObject>();
		canvasImage = new BufferedImage(300,300, BufferedImage.TYPE_INT_ARGB);
		canvasTexture = new Texture(canvasImage);
		canvasGraphics = canvasImage.createGraphics();
	}
	
	@Override
	public void render(Screen screen, int x, int y) {
		// TODO Auto-generated method stub
		canvasGraphics.setColor(Color.PINK);
		canvasGraphics.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
		for(int i=0;i<guis.size();i++){
			guis.get(i).Draw(canvasGraphics);
		}
		
		screen.drawGraphic(canvasTexture, x, y);
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
		canvasGraphics.dispose();
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
