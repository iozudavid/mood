package com.knightlore.game;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.NetworkObject;

public class ClientFFAGame extends FFAGame {
    
    public ClientFFAGame() {
        super(UUID.randomUUID());
    }
    
 public ClientFFAGame(UUID uuid) {
        super(uuid);
        GameEngine.getSingleton().getWorld().changeGameManager(this);
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
