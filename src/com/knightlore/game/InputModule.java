package com.knightlore.game;

import java.util.Map;

import com.knightlore.game.entity.Entity;
import com.knightlore.network.protocol.ClientController;

/**
 * The input component of a player, with human and bot implementations.
 *
 * @authors James Adey, Tom Williams
 */
public abstract class InputModule {
    /*
     * Instructions that can be placed in a map
     * FORWARD, LEFT, BACKWARD, RIGHT, ROTATE_CLOCKWISE, ROTATE_ANTI_CLOCKWISE,
     * SHOOT
     */
    public double turnInput;
    public double strafeInput;
    public double walkInput;
    public boolean shoot;
    
    /**
     * Updates the input state provided to it
     *
     * @param inputState the initial input state of the player
     * @param myPlayer   the player that this input module is controlling
     * @returns a modified input mapping for the player
     */
    public abstract Map<ClientController, Byte> updateInput(Map<ClientController, Byte> inputState, Entity myPlayer);
    
    /**
     * Used to notify the input that the player it is controlling has respawned
     *
     * @param myPlayer The player which this input module is controlling
     */
    public abstract void onRespawn(Entity myPlayer);
}
