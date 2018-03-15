package com.knightlore.engine.input;

/**
 * This interfaces defines a video game controller. Each of the methods
 * represent an action the player can perform. The methods should return an AWT
 * keycode for the key that needs to be pressed in order to carry out that
 * specific function.
 * 
 * @author Joe Ellis
 *
 */
public interface Controller {

    public int moveForward();

    public int moveBackward();

    public int moveLeft();

    public int moveRight();

    public int sprint();

    public int rotateClockwise();

    public int rotateAntiClockwise();

    public int shoot();

}
