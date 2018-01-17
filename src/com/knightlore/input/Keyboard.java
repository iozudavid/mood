package com.knightlore.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Keyboard extends KeyAdapter {


	private boolean[] keys;

	public Keyboard() {
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

}
