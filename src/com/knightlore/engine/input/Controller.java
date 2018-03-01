package com.knightlore.engine.input;

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
