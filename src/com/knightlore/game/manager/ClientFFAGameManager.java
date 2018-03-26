package com.knightlore.game.manager;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.network.NetworkObject;

/**
 * Contains data and methods to handle the client-side of the Free For All game
 * mode
 *
 * @author James
 */
public class ClientFFAGameManager extends FFAGameManager {
    
    boolean gameOver = false;
    
    /**
     * Creates a Game Manager with a random UUID. Calls the other constructor
     * with this UUID.
     *
     * @see com.knightlore.game.manager.ClientFFAGameManager#ClientFFAGameManager(UUID)
     */
    public ClientFFAGameManager() {
        super(UUID.randomUUID());
    }
    
    /**
     * Creates a Game Manager with the given UUID. Also changes
     *
     * @param uuid the UUID of this network object
     */
    public ClientFFAGameManager(UUID uuid) {
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
        System.out.println("FFA CLIENT build, state size: " + state.remaining());
        NetworkObject obj = new ClientFFAGameManager(uuid);
        obj.init();
        obj.deserialize(state);
        return obj;
    }
    
    @Override
    public String getClientClassName() {
        return ClientFFAGameManager.class.getName();
    }
    
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
