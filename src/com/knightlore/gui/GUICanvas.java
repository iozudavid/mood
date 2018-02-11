package com.knightlore.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.knightlore.engine.GameObject;
import com.knightlore.engine.input.InputManager;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.utils.Physics;
import com.knightlore.utils.Vector2D;


public class GUICanvas extends GameObject implements IRenderable {

	private ArrayList<GUIObject> guis;
	
	private Graphic canvasGraphic;
	private BufferedImage canvasImage;
	private Graphics2D canvasG2D;
	private int[] drawPixels;
	
	// the object that was selected when the mouse was pressed down
	private GUIObject downSelected;
	private boolean lastHeld;
	
	// TODO remove this and make it screen size
	private final int TEMP_SIZE = 200;
	
	private final Color backgroundColor = new Color(0xFF000000,true);
	
	public GUICanvas (){
		super();
		guis = new ArrayList<GUIObject>();
		canvasImage = new BufferedImage(TEMP_SIZE,TEMP_SIZE, BufferedImage.TYPE_INT_ARGB);
		canvasG2D = canvasImage.createGraphics();
		canvasG2D.setComposite(AlphaComposite.SrcOver);
		canvasGraphic = new Graphic(canvasImage);
		// store pixel array
		drawPixels = canvasGraphic.getPixels();
		
	}
	
	@Override
	public void render(PixelBuffer pix, int x, int y) {
		canvasG2D.setColor(backgroundColor);
		canvasG2D.fillRect(x, y, TEMP_SIZE, TEMP_SIZE);
		
		for(int i=0;i<guis.size();i++){
			guis.get(i).Draw(canvasG2D);
		}
		canvasImage.getRGB(0, 0, TEMP_SIZE, TEMP_SIZE, drawPixels, 0, TEMP_SIZE);
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
		for(int i=guis.size()-1;i>=0;i--){
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
				if(downSelected == lastSelected){
					lastSelected.OnMouseUp();
				}
				lastSelected.OnMouseExit();
			}
		}
		
		// notify new gui of mouse enter
		if(selected != null){
			// send mouse entered event
			if(selected != lastSelected){
				selected.OnMouseEnter();
			}
			
			selected.OnMouseOver();
			
			// mouse held and we 
			if(InputManager.getMouse().isLeftHeld() && !lastHeld){
				// send mouse down event
				selected.OnMouseDown();
				downSelected = selected;
			}
			else if(!InputManager.getMouse().isLeftHeld() && lastHeld){
				// did we click down over the same object
				if(downSelected == selected){
					selected.OnClick();
				}
				// send mouse up event
				selected.OnMouseUp();
			}
		}
		lastHeld = InputManager.getMouse().isLeftHeld();
		lastSelected = selected;		
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
