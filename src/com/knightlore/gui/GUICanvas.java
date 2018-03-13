package com.knightlore.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.knightlore.MainWindow;
import com.knightlore.engine.GameObject;
import com.knightlore.engine.input.InputManager;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.utils.physics.Physics;
import com.knightlore.utils.Vector2D;

public class GUICanvas extends GameObject implements IRenderable {

	private static int WIDTH;
	private static int HEIGHT;
	private static final Color BACKGROUND_COLOR = new Color(0xFF0000FF,true);

	static TextField activeTextField;

	private final List<GUIObject> guis;
	private final Graphic canvasGraphic;
	private final BufferedImage canvasImage;
	private final Graphics2D canvasG2D;
	private final int[] drawPixels;
	
	// the object that was selected when the mouse was pressed down
	private GUIObject downSelected;
	private GUIObject focussed;
	private GUIObject lastSelected;
	private boolean lastHeld;
	
	private Rectangle rect;
	
	public GUICanvas(int screenWidth, int screenHeight){
		super();
		WIDTH = screenWidth;
		HEIGHT = screenHeight;
		guis = new ArrayList<GUIObject>();
		canvasImage = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_ARGB);
		canvasG2D = canvasImage.createGraphics();
		canvasG2D.setComposite(AlphaComposite.SrcOver);
		canvasGraphic = new Graphic(canvasImage);
		// store pixel array
		drawPixels = canvasGraphic.getPixels();
		canvasG2D.setFont(Font.getFont(Font.SERIF));
	}
	
	/** Called from the keyboard when a textual character is typed
	 */
	public static void inputChar(char c) {
		if (activeTextField != null) {
			activeTextField.onInputChar(c);
		}
	}
	
	public static void inputLeftArrow(){
		if (activeTextField != null) {
			activeTextField.onLeftArrow();
		}
	}
	
	public static void inputRightArrow() {
		if (activeTextField != null) {
			activeTextField.onRightArrow();
		}
	}
	
	public static void deleteChar() {
		if (activeTextField != null) {
			activeTextField.onDeleteChar();
		}
	}
	
	public static boolean isTyping() {
		return activeTextField != null;
	}
	
	@Override
	public void render(PixelBuffer pix, int x, int y) {
		canvasG2D.setColor(GuiUtils.makeTransparent(BACKGROUND_COLOR,255));
		int maxWidth = 0;
		int maxHeight = 0;
		for(int i=0;i<guis.size();i++){
			guis.get(i).Draw(canvasG2D,rect);
			if(guis.get(i).getRectangle().getWidth()+guis.get(i).getRectangle().getX()>maxWidth)
				maxWidth = (int)(guis.get(i).getRectangle().getWidth()+guis.get(i).getRectangle().getX());
			if(guis.get(i).getRectangle().getHeight()+guis.get(i).getRectangle().getY()>maxHeight)
				maxHeight = (int)(guis.get(i).getRectangle().getHeight()+guis.get(i).getRectangle().getY());
		}
		canvasImage.getRGB(0, 0, WIDTH, HEIGHT, drawPixels, 0, WIDTH);
		pix.drawGraphic(canvasGraphic, x, y, WIDTH, HEIGHT);
	}

	@Override
	public void onCreate() {
	}
	
	@Override
	public void onUpdate() {
		Vector2D mousePos = InputManager.getMousePos();
		GUIObject selected = null;
		// linear reverse search
		for (int i = guis.size() - 1; i >= 0; i--) {
			GUIObject gui = guis.get(i);
			// skip those that aren't selectable
			if (!gui.isSelectable()) {
				continue;
			}
			
			if (Physics.pointInAWTRectangleTest(mousePos, gui.rect)) {
				selected = gui;
				break;
			}
		}
		
		// notify previous gui of mouse exit
		if (lastSelected != selected) {
			if (lastSelected != null) {
				if (downSelected == lastSelected) {
					lastSelected.onMouseUp();
				}
				lastSelected.OnMouseExit();
			}
		}
		
		// notify new gui of mouse enter
		if (selected != null) {
			System.out.println(selected);
			// send mouse entered event
			if (selected != lastSelected) {
				selected.onMouseEnter();
			}
			
			selected.onMouseOver();
			
			// mouse held and we 
			if (InputManager.getMouse().isLeftHeld() && !lastHeld) {
				// send mouse down event
				selected.onMouseDown();
				downSelected = selected;
			} else if (!InputManager.getMouse().isLeftHeld() && lastHeld) {
				// did we click down over the same object
				if (downSelected == selected) {
					selected.OnClick();
					if (focussed != selected && focussed != null) {
						focussed.onLostFocus();
					}

					focussed = selected;
					selected.onGainedFocus();
				}
				// send mouse up event
				selected.onMouseUp();
			}
		} else {
			if (!InputManager.getMouse().isLeftHeld() && lastHeld) {
				// we have nothing selected... did we click?
				if (focussed != null) {
					focussed.onLostFocus();
					focussed = null;
				}
			}
		}
		lastHeld = InputManager.getMouse().isLeftHeld();
		lastSelected = selected;		
	}

	@Override
	public void onDestroy() {
		guis.clear();
		canvasG2D.dispose();
	}
	
	private void sort() {
		for (int i = 1; i < guis.size(); i++) {
			GUIObject left = guis.get(i);
			GUIObject right = guis.get(i-1);
			// swap
			if (left.depth < right.depth) {
				guis.set(i, right);
				guis.set(i-1, left);
			}
		}
	}

	public void addGUIObject(GUIObject gui) {
		guis.add(gui);
		sort();
	}
	
	public void removeGUIObject(GUIObject gui) {
		guis.remove(gui);
	}

	public static void setActiveTextField(TextField activeTextField) {
		GUICanvas.activeTextField = activeTextField;
	}
	
}
