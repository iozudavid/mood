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
public final class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {

	private volatile int x, y;
	private volatile int scroll;

	private boolean leftHeld, rightHeld;
	private boolean lastLeftHeld, lastRightHeld;
	private boolean leftClick, rightClick;
	
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		System.out.println("DRAGGED");
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
		System.out.println("CLICKED");
		leftClick = SwingUtilities.isLeftMouseButton(e);
		rightClick = SwingUtilities.isRightMouseButton(e);
	}

	public void mouseReleased(MouseEvent e) {
		x = e.getX();
		y = e.getY();

		if (SwingUtilities.isLeftMouseButton(e)) {
			leftHeld = false;
		} else {
			rightHeld = false;
		}
		System.out.println("RELEASED");
	}
	
	public void clearButtons(){
		if(lastLeftHeld && !leftHeld){
			leftClick = true;
		}
		else{
			leftClick = false;
		}
		
		if(lastRightHeld && !rightHeld){
			rightClick = true;
		}
		else{
			rightClick = false;
		}
		lastLeftHeld = leftHeld;
		lastRightHeld = rightHeld;
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
	
	public boolean isLeftClick(){
		return leftClick;
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
		if(SwingUtilities.isLeftMouseButton(e)){
			leftHeld = true;
		}
		if(SwingUtilities.isRightMouseButton(e)){
			rightHeld = true;
		}
		System.out.println("PRESSED");
	}

}