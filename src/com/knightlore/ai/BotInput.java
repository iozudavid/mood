package com.knightlore.ai;

import java.util.Map;

import com.knightlore.network.protocol.ClientController;

/**
 * Input module for those players that are controlled by bots
 * @authors James, Tom
 *
 */
public final class BotInput extends InputModule {

    @Override
    public Map<ClientController, Byte> updateInput(Map<ClientController, Byte> inputState) {
        inputState.put(ClientController.ROTATE_CLOCKWISE, (byte) 1);
        return inputState;
    }

	
	
}
