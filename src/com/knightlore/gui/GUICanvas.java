package com.knightlore.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.knightlore.engine.GameObject;
import com.knightlore.engine.input.InputManager;
import com.knightlore.render.IRenderable;
import com.knightlore.render.PixelBuffer;
import com.knightlore.utils.Vector2D;
import com.knightlore.utils.funcptrs.VoidFunction;
import com.knightlore.utils.physics.Physics;

public class GUICanvas extends GameObject implements IRenderable {

	private static int WIDTH;
	private static int HEIGHT;
	private static final Color BACKGROUND_COLOR = new Color(0xFF00FF00,true);

	static TextField activeTextField;
	static TextField gameTextField;
	
	private final List<GUIObject> guis;
	private Graphics2D canvasG2D;
	// the object that was selected when the mouse was pressed down
	private GUIObject downSelected;
	private GUIObject focussed;
	private GUIObject lastSelected;
	private boolean lastHeld;
	
	private static Optional<VoidFunction> onPressEscape=Optional.empty();
	private static Optional<VoidFunction> onPressQ=Optional.empty();
	private static Optional<VoidFunction> onReleaseQ=Optional.empty();
    public boolean isVisible;
	
	public GUICanvas(int screenWidth, int screenHeight){
		super();
		WIDTH = screenWidth;
		HEIGHT = screenHeight;
		guis = new ArrayList<>();
		isVisible = true;
	}
	
	/** Called from the keyboard when a textual character is typed
	 */
	public static void inputChar(char c) {
		if (activeTextField != null) {
			activeTextField.onInputChar(c);
		}
	}
	
	public static void startMessageTeam(char c) {
		if (activeTextField == null) {
			if (gameTextField != null) {
				activeTextField = gameTextField;
				gameTextField.onMessage(c);
			}
		}else {
			inputChar(c);
		}
		
	}
	
	public static void startMessageAll(char c) {
		if (activeTextField == null) {
			if (gameTextField != null) {
				activeTextField = gameTextField;
				gameTextField.onMessage(c);
			}
		}else {
			inputChar(c);
		}
		
	}
	
	public static void sendMessage(char c) {
		if (gameTextField != null) {
			activeTextField=gameTextField;
			gameTextField.onSendMessage(c);
		}
	}
	
	public static void escape(){
		if (activeTextField != null) {
			activeTextField = null;
			gameTextField.escape();
		} else if(onPressEscape.isPresent()){
			onPressEscape.get().call();
		}
	}
	
	public static void pressQ(){
		if(onPressQ.isPresent()){
			onPressQ.get().call();
		}
	}
	
	public static void releaseQ(){
		if(onReleaseQ.isPresent()){
			onReleaseQ.get().call();
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
		canvasG2D.setColor(BACKGROUND_COLOR);
		for (GUIObject gui : guis) {
			gui.Draw(pix,x,y);
		}
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
		if(gui instanceof TextField) {
			gameTextField = (TextField) gui;
		}
		guis.add(gui);
		sort();
	}
	
	public void removeGUIObject(GUIObject gui) {
		guis.remove(gui);
	}

	public static void setActiveTextField(TextField activeTextField) {
		GUICanvas.activeTextField = activeTextField;
	}
	
	public static void setOnEscFunction(VoidFunction func){
		onPressEscape=Optional.of(func);
	}
	
	public static void setOnQFunction(VoidFunction func){
		onPressQ=Optional.of(func);
	}
	
	public static void setOnQReleaseFunction(VoidFunction func){
		onReleaseQ=Optional.of(func);
	}
	
}
