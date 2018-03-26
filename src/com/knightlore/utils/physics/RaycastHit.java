package com.knightlore.utils.physics;

import com.knightlore.game.entity.Entity;

/**
 * A plain old data class, for storing information about what was hit in a raycast or linecast.
 * @author James
 */
public final class RaycastHit {
    
    private final Entity entity;
    
    public RaycastHit(Entity ent) {
        this.entity = ent;
    }
    
    /**
     * @returns True if the ray hit any entity
     */
    public boolean didHitEntity() {
        return entity != null;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
}
