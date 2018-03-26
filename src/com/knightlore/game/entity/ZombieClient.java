package com.knightlore.game.entity;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.NetworkObject;
import com.knightlore.render.animation.Animation;
import com.knightlore.render.animation.PlayerMoveAnimation;
import com.knightlore.render.animation.PlayerStandAnimation;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.ZombieGraphicMatrix;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class ZombieClient extends ZombieShared {
    
    private final PlayerMoveAnimation moveAnim = new PlayerMoveAnimation(
            ZombieGraphicMatrix.getGraphic(ZombieGraphicMatrix.Stance.MOVE));
    
    private final PlayerStandAnimation standAnim = new PlayerStandAnimation(
            ZombieGraphicMatrix.getGraphic(ZombieGraphicMatrix.Stance.STAND),
            (long)(GameEngine.UPDATES_PER_SECOND / 10));
    
    private Animation<DirectionalSprite> currentAnim = standAnim;
    
    private Vector2D prevPos;
    
    public ZombieClient(Vector2D position, Vector2D direction) {
        super(position, direction);
    }
    
    private ZombieClient(UUID uuid, double size, Vector2D position, Vector2D direction) {
        super(uuid, size, position, direction);
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
        NetworkObject obj = new ZombieClient(uuid, 0, Vector2D.ONE, Vector2D.ONE);
        obj.init();
        obj.deserialize(state);
        return obj;
    }
    
    @Override
    public void onUpdate() {
        if (prevPos != null) {
            // The difference between our previous and new positions.
            Vector2D displacement = position.subtract(prevPos);
            double dis = displacement.magnitude();
            moveAnim.update(dis);
        }
        
        currentAnim = moveAnim.expired() ? standAnim : moveAnim;
        prevPos = position;
    }
    
    @Override
    public Graphic getGraphic(Vector2D playerPos) {
        return currentAnim.getFrame().getCurrentGraphic(position, direction, playerPos);
    }
    
    @Override
    public void onCollide(Player player) {
    }
    
}
