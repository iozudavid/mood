package com.knightlore.utils.physics;

import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Vector2D;

/**
 * A plain old data class, for storing information about what was hit in a raycast or linecast.
 * @author James
 * @see GameWorld#raycast
 */
public final class RaycastHit {
    
    private RaycastHitType hitType = RaycastHitType.NOTHING;
    private Vector2D hitPos = Vector2D.ZERO;
    private Entity entity = null;
    
    public RaycastHit(RaycastHitType type,Vector2D pos, Entity ent) {
        this.hitType = type;
        this.hitPos = pos;
        this.entity = ent;
    }
    
    public RaycastHitType getHitType() {
        return hitType;
    }
    
    /**
     * 
     * @returns True if the ray hit something, i.e. getHitType is not NOTHING
     * @see RaycastHitType
     */
    public boolean didHit() {
        return hitType != RaycastHitType.NOTHING;
    }
    
    /**
     * @returns True if the ray hit any entity, i.e. getEntity() will not return null
     * @see getEntity
     */
    public boolean didHitEntity() {
        return entity != null;
    }
    
    public Vector2D getHitPos() {
        return hitPos;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
}
