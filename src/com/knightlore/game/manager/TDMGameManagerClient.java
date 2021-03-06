package com.knightlore.game.manager;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.network.NetworkObject;

/**
 * Contains data and methods to handle the Free For All game mode client-side.
 *
 * @author Tom
 * @see TDMGameManager
 */
public class TDMGameManagerClient extends TDMGameManager {
    
    private boolean gameOver;

    /**
     * Creates a Game Manager with a random UUID. Calls the other constructor
     * with this UUID.
     *
     * @see com.knightlore.game.manager.TDMGameManagerClient#TDMGameManagerClient(UUID)
     */
    public TDMGameManagerClient() {
        super(UUID.randomUUID());
    }
    
    /**
     * Creates a Game Manager with the given UUID, also sets this game manager
     * in the world.
     */
    public TDMGameManagerClient(UUID uuid) {
        super(uuid);
        GameEngine.getSingleton().getWorld().changeGameManager(this);
    }

    /**
     * Called by the network when creating the client-side representation of
     * this object. Instantiates a copy of the client class, and deserializes
     * the state into it.
     *
     * @param uuid  The uuid provided to this object
     * @param state The initial state of this object
     * @returns The client-side network object
     * @see NetworkObject
     */
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        System.out.println("TDM CLIENT build, state size: " + state.remaining());
        NetworkObject obj = new TDMGameManagerClient(uuid);
        obj.init();
        obj.deserialize(state);
        return obj;
    }
    
    /**
     * Sets the game state to LOBBY.
     */
    @Override
    public void startLobby() {
        gameState = GameState.LOBBY;
    }

    /**
     * Checks if the game is over.
     */
    @Override
    public void onUpdate() {
        if (!gameOver && gameState == GameState.FINISHED) {
            onGameOver();
            gameOver = true;
        }
    }
    
    private void onGameOver() {
        GameSettings.desiredBlockiness = 70;
        System.out.println("!!!!!");
    }
    
}
