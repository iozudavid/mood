package com.knightlore.game.entity;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.network.NetworkObject;
import com.knightlore.utils.Vector2D;

public final class TurretClient extends TurretShared {
    
    private TurretClient(UUID uuid, Vector2D one, Vector2D one2) {
        super(uuid, one, one2);
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
