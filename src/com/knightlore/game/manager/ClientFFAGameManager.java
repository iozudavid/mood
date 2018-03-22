package com.knightlore.game.manager;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Player;
import com.knightlore.network.NetworkObject;

public class ClientFFAGameManager extends FFAGameManager {

    public ClientFFAGameManager() {
        super(UUID.randomUUID());
    }

    public ClientFFAGameManager(UUID uuid) {
        super(uuid);
        GameEngine.getSingleton().getWorld().changeGameManager(this);
    }

    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        System.out.println("Player build, state size: " + state.remaining());
        NetworkObject obj = new ClientFFAGameManager(uuid);
        obj.init();
        obj.deserialize(state);
        return obj;
    }

    @Override
    public String getClientClassName() {
        return ClientFFAGameManager.class.getName();
    }

    boolean gameOver = false;

    @Override
    public void onUpdate() {
        if (!gameOver && gameState == GameState.FINISHED) {
            onGameOver();
            gameOver = true;
        }
    }

    @Override
    public void onPlayerDeath(Player p) {
        // do nothing
    }

    private void onGameOver() {
        GameSettings.desiredBlockiness = 70;
        System.out.println("!!!!!");
    }

}
