package com.knightlore.utils;

import com.knightlore.game.entity.Entity;

/**
 * A plain old data class, for storing information about what was hit in a raycast.
 * @author James
 */
public final class RaycastHit {
    
    private RaycastHitType hitType = RaycastHitType.nothing;
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
    
    public boolean didHit() {
        return hitType != RaycastHitType.nothing;
    }
    
    public Vector2D getHitPos() {
        return hitPos;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
}
