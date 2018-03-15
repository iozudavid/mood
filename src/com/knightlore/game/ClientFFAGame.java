package com.knightlore.game;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.NetworkObject;
import com.knightlore.utils.Vector2D;

public class ClientFFAGame extends FFAGame {
    
    public ClientFFAGame() {
        super(UUID.randomUUID());
    }
    
 public ClientFFAGame(UUID uuid) {
        super(uuid);
    }

    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        System.out.println("Player build, state size: " + state.remaining());
        NetworkObject obj = new ClientFFAGame(uuid);
        obj.init();
        obj.deserialize(state);
        return obj;
    }
    
    @Override
    public String getClientClassName() {
        return ClientFFAGame.class.getName();
    }
    
    @Override
    public void onUpdate() {
        // client does nothing
    }
    
    @Override
    public void onPlayerDeath(Player p) {
        // do nothing
    }
    
}
