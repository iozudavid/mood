package com.knightlore.ai;

import java.util.Map;

import com.knightlore.game.entity.Entity;
import com.knightlore.network.protocol.ClientController;

/**
 * Remote input module for those players that are controlled by clients
 * @authors James, Tom
 *
 */
public final class RemoteInput extends InputModule {

    @Override
    public Map<ClientController, Byte> updateInput(Map<ClientController, Byte> inputState, Entity myPlayer) {
        // do nothing, just hand it back to the player
        return inputState;
    }

    @Override
    public void onRespawn(Entity myPlayer) {
        // TODO Auto-generated method stub
    }

}
