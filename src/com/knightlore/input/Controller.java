package com.knightlore.input;

import java.awt.event.KeyEvent;

public class Controller {

	private Keyboard keyboard;

	public Controller(Keyboard keyboard) {
		this.keyboard = keyboard;
	}

	public boolean w() {
		return keyboard.isPressed(KeyEvent.VK_W);
	}

	public boolean a() {
		return keyboard.isPressed(KeyEvent.VK_A);
	}

	public boolean s() {
		return keyboard.isPressed(KeyEvent.VK_S);
	}

	public boolean d() {
		return keyboard.isPressed(KeyEvent.VK_D);
	}

	public boolean q() {
		return keyboard.isPressed(KeyEvent.VK_Q);
	}

	public boolean e() {
		return keyboard.isPressed(KeyEvent.VK_E);
	}

}
