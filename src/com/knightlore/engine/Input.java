package com.knightlore.engine;

import com.knightlore.input.Keyboard;
import com.knightlore.input.Mouse;
import com.knightlore.utils.Vector2D;

public class Input {
	
	private static Keyboard keyboard;
	private static Mouse mouse;
	
	public static Keyboard getKeyboard() {
		return keyboard;
	}

	public static Mouse getMouse() {
		return mouse;
	}
	
	public static Boolean isKeyDown(int keyCode) {
		return keyboard.isPressed(keyCode);
	}
	
	public static Vector2D getMousePos() {
		return new Vector2D(mouse.getX(), mouse.getY());
	}
	
	static void init(){
		keyboard = new Keyboard();
		mouse = new Mouse();
	}
	

}
