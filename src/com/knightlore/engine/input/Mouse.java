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
 */
public final class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * The current x and y positions of the mouse.
     */
    private volatile int x, y;

    /**
     * The number of scroll clicks since the last time getScroll() was called.
     */
    private volatile int scroll;

    private boolean leftHeld, rightHeld;
    private boolean leftClick, rightClick;

    @Override
    public void mouseDragged(MouseEvent e) {
        // update the X and Y positions.
        // since we're dragging, set the appropriate booleans to true.
        x = e.getX();
        y = e.getY();
        leftHeld = SwingUtilities.isLeftMouseButton(e);
        rightHeld = SwingUtilities.isRightMouseButton(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        leftClick = SwingUtilities.isLeftMouseButton(e);
        rightClick = SwingUtilities.isRightMouseButton(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        x = e.getX();
        y = e.getY();

        leftHeld = !SwingUtilities.isLeftMouseButton(e);
        rightHeld = !SwingUtilities.isRightMouseButton(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scroll += e.getWheelRotation();
    }

    /**
     * Sets the booleans for button clicks to false.
     */
    public void clearButtons() {
        leftClick = false;
        rightClick = false;
    }

    /**
     * Get the current x position of the mouse.
     *
     * @return int the current x position of the mouse.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the current y position of the mouse.
     *
     * @return int the current y position of the mouse.
     */
    public int getY() {
        return y;
    }

    /**
     * Get the number of scroll wheel clicks since the last time this function
     * was called.
     *
     * @return int the number of scroll wheel clicks. Negative values if the
     * mouse wheel was rotated up/away from the user, and positive
     * values if the mouse wheel was rotated down/towards the user.
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
     * mouse button.
     */
    public boolean isLeftHeld() {
        return leftHeld;
    }

    /**
     * Get a boolean value representing whether the right mouse button is
     * pressed or not.
     *
     * @return boolean A boolean representing the current state of the right
     * mouse button.
     */
    public boolean isRightHeld() {
        return rightHeld;
    }

    /* UNUSED -- NEEDED SINCE WE ARE IMPLEMENTING AN INTERFACE */
    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftHeld = true;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            rightHeld = true;
        }
    }

}