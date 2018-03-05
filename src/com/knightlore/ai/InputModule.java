package com.knightlore.ai;

import java.util.Map;

import com.knightlore.network.protocol.ClientController;

public abstract class InputModule {
    /*
     * FORWARD, LEFT, BACKWARD, RIGHT, ROTATE_CLOCKWISE, ROTATE_ANTI_CLOCKWISE,
     * SHOOT
     */
	public float turnInput;
	public float strafeInput;
	public float walkInput;
	public boolean shoot;
	
	public abstract Map<ClientController, Byte> updateInput(Map<ClientController, Byte> inputState);
	
	
}
