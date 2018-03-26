package com.knightlore.engine.input;

import com.knightlore.utils.Vector2D;

/**
 * Central manager for input devices.
 *
 * @author James Adey
 */
public class InputManager {
    
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
    
    /**
     * Creates the keyboard and mouse
     */
    public static void init() {
        keyboard = new Keyboard();
        mouse = new Mouse();
    }
    
    /**
     * Clears the saved mouse presses, called at the end of each game loop.
     */
    public static void clearMouse() {
        mouse.clearButtons();
    }
}
