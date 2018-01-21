package com.knightlore.input;

import java.awt.event.KeyEvent;

public class BasicController implements Controller {

	@Override
	public int moveForward() {
		return KeyEvent.VK_W;
	}

	@Override
	public int moveBackward() {
		return KeyEvent.VK_S;
	}

	@Override
	public int moveLeft() {
		return KeyEvent.VK_A;
	}

	@Override
	public int moveRight() {
		return KeyEvent.VK_D;
	}

	@Override
	public int rotateClockwise() {
		return KeyEvent.VK_RIGHT;
	}

	@Override
	public int rotateAntiClockwise() {
		return KeyEvent.VK_LEFT;
	}

	@Override
	public int shoot() {
		return KeyEvent.VK_SPACE;
	}

}
