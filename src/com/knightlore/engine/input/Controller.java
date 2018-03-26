package com.knightlore.engine.input;

/**
 * This interfaces defines a video game controller. Each of the methods
 * represent an action the player can perform. The methods should return an AWT
 * keycode for the key that needs to be pressed in order to carry out that
 * specific function.
 *
 * @author Joe Ellis
 */
public interface Controller {

    int moveForward();

    int moveBackward();

    int moveLeft();

    int moveRight();

    int sprint();

    int rotateClockwise();

    int rotateAntiClockwise();

    int shoot();

}
