package com.knightlore.game.manager;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.NetworkObject;

public class TDMGameManagerClient extends TDMGameManager {
    
 // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        System.out.println("TDM CLIENT build, state size: " + state.remaining());
        NetworkObject obj = new TDMGameManagerClient(uuid);
        obj.init();
        obj.deserialize(state);
        return obj;
    }
    
    public TDMGameManagerClient() {
        super(UUID.randomUUID());
    }

    public TDMGameManagerClient(UUID uuid) {
        super(uuid);
        GameEngine.getSingleton().getWorld().changeGameManager(this);
    }
    
}
