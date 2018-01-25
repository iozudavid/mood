package com.knightlore.engine.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

/**
 * Handles basic mouse input. This code was taken from my project at
 * https://github.com/joechrisellis/QuickGameEngine and modified slightly.
 * 
 * @author Joe Ellis
 *
 */
public class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {

	protected volatile int x, y;
	protected volatile int scroll;

	protected boolean leftHeld, rightHeld;

	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		leftHeld = SwingUtilities.isLeftMouseButton(e);
		rightHeld = SwingUtilities.isRightMouseButton(e);
	}

	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	public void mouseClicked(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		leftHeld = SwingUtilities.isLeftMouseButton(e);
		rightHeld = SwingUtilities.isRightMouseButton(e);
	}

	public void mouseReleased(MouseEvent e) {
		x = e.getX();
		y = e.getY();

		if (SwingUtilities.isLeftMouseButton(e)) {
			leftHeld = false;
		} else {
			rightHeld = false;
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		scroll += e.getWheelRotation();
	}

	/**
	 * Get the current x position of the mouse.
	 * 
	 * @return int The current x position of the mouse.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the current y position of the mouse.
	 * 
	 * @return int The current y position of the mouse.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get the number of scroll wheel clicks since the last time this function
	 * was called.
	 * 
	 * @return int The number of scroll wheel clicks. Negative values if the
	 *         mouse wheel was rotated up/away from the user, and positive
	 *         values if the mouse wheel was rotated down/towards the user.
	 */
	public int getScroll() {
		int retVal = scroll;
		scroll = 0;
		return retVal;
	}

	/**
	 * Get a boolean value representing whether the left mouse button is pressed
	 * or not.
	 * 
	 * @return boolean A boolean representing the current state of the left
	 *         mouse button.
	 */
	public boolean isLeftHeld() {
		return leftHeld;
	}

	/**
	 * Get a boolean value representing whether the right mouse button is
	 * pressed or not.
	 * 
	 * @return boolean A boolean representing the current state of the right
	 *         mouse button.
	 */
	public boolean isRightHeld() {
		return rightHeld;
	}

	/* UNUSED */
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

}