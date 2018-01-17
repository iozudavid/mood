package com.knightlore.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Keyboard extends KeyAdapter {

	/**
	 * Singleton instance of the keyboard.
	 */
	private static Keyboard instance = null;

	private boolean[] keys;

	private Keyboard() {
		keys = new boolean[256];
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	/**
	 * Check whether a key is pressed given by its keycode.
	 * 
	 * @param kc
	 *            the keycode (found in KeyEvent.VK_...)
	 * @return true if the key is currently being pressed, false otherwise.
	 */
	public boolean isPressed(int kc) {
		return keys[kc];
	}

	/**
	 * Gets the global keyboard.
	 * 
	 * @return the global keyboard object.
	 */
	public static Keyboard getInstance() {
		if (instance == null) {
			instance = new Keyboard();
		}
		return instance;
	}

}
