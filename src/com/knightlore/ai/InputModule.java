package com.knightlore.ai;

import java.util.Map;

import com.knightlore.game.entity.Entity;
import com.knightlore.network.protocol.ClientController;

public abstract class InputModule {
    /*
     * FORWARD, LEFT, BACKWARD, RIGHT, ROTATE_CLOCKWISE, ROTATE_ANTI_CLOCKWISE,
     * SHOOT
     */
	public double turnInput;
	public double strafeInput;
	public double walkInput;
	public boolean shoot;
	
	public abstract Map<ClientController, Byte> updateInput(Map<ClientController, Byte> inputState, Entity myPlayer);

    public abstract void onRespawn(Entity myPlayer);
}
