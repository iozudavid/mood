package com.knightlore.game.entity;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.network.NetworkObject;
import com.knightlore.utils.Vector2D;

public final class TurretClient extends TurretShared {
    
    protected TurretClient(double size, Vector2D position, Vector2D direction) {
        super(size, position, direction);
    }
    
    public TurretClient(UUID uuid, Vector2D one, Vector2D one2) {
        super(uuid, one, one2);
    }
    
    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        System.out.println("Player build, state size: " + state.remaining());
        NetworkObject obj = new TurretClient(uuid, Vector2D.ONE, Vector2D.ONE);
        obj.init();
        obj.deserialize(state);
        return obj;
    }
    
    @Override
    public void onCollide(Player player) {
    }
    
    @Override
    protected void shoot() {
        
    }

    @Override
    protected boolean hasTarget() {
        return targetByte == 1;
    }

    @Override
    public void onUpdate() {
    }
    
}
