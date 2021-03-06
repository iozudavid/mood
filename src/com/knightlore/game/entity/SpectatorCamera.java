package com.knightlore.game.entity;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class SpectatorCamera extends Entity {
    
    public static final double CAMERA_SIZE = 0.25D;
    
    public SpectatorCamera(Vector2D position, Vector2D direction) {
        this(UUID.randomUUID(), position, direction);
    }
    
    public SpectatorCamera(UUID uuid, Vector2D position, Vector2D direction) {
        super(uuid, CAMERA_SIZE, position, direction);
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
        NetworkObject obj = new SpectatorCamera(uuid, Vector2D.ONE, Vector2D.ONE);
        obj.init();
        obj.deserialize(state);
        return obj;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        double angle = Math.sin(GameEngine.ticker.getTime() / 240D) / 150D;
        double xprime = direction.getX() * Math.cos(angle) - direction.getY() * Math.sin(angle);
        double yprime = direction.getX() * Math.sin(angle) + direction.getY() * Math.cos(angle);
        direction = new Vector2D(xprime, yprime);
        
        plane = direction.perpendicular();
    }
    
    @Override
    public int getMinimapColor() {
        return 0x00FF00;
    }
    
    @Override
    public void onCollide(Player player) {
    }
    
    @Override
    public DirectionalSprite getDirectionalSprite() {
        return DirectionalSprite.CAMERA_DIRECTIONAL_SPRITE;
    }
    
    @Override
    public String getClientClassName() {
        return this.getClass().getName();
    }
    
}
