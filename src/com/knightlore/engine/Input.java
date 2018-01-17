package com.knightlore.engine;

import com.knightlore.input.Keyboard;
import com.knightlore.input.Mouse;

public class Input {
	
	private static Keyboard keyboard;
	private static Mouse mouse;
	
	public static Keyboard GetKeyboard(){
		return keyboard;
	}
	
	public static Mouse GetMouse(){
		return mouse;
	}
	
	public static Boolean IsKeyDown (int keyCode) {
		return keyboard.isPressed(keyCode);
	}
	
	public static Vector2 GetMousePos(){
		return new Vector2(mouse.getX(),mouse.getY());
	}
	
	static void init(){
		keyboard = new Keyboard();
		mouse = new Mouse();
	}
	

}
