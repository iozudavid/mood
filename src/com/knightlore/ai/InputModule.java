package com.knightlore.ai;

public abstract class InputModule {
    /*
     * FORWARD, LEFT, BACKWARD, RIGHT, ROTATE_CLOCKWISE, ROTATE_ANTI_CLOCKWISE,
     * SHOOT
     */
	public float turnInput;
	public float strafeInput;
	public float walkInput;
	public boolean shoot;
	
	public abstract void updateInput();
	
	
}
