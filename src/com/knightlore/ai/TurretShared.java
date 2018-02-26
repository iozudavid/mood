package com.knightlore.ai;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.engine.GameEngine;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public abstract class TurretShared extends Entity {
    
    protected int damage;
    protected int team;
    
    protected long nextCheckTime = 0;
    protected static final long TURRET_CHECK_DELAY = 20;
    
    protected Entity target = null;
    protected byte targetByte = 0;
    
    protected TurretShared(double size, Vector2D position, Vector2D direction) {
        super(size, position, direction);
    }
    
    protected TurretShared(UUID uuid, Vector2D pos, Vector2D dir) {
        super(uuid,pos,dir);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void onUpdate() {
        // 60 ticks per second
        long currentTime = GameEngine.ticker.getTime();
        aim();
        if (currentTime >= nextCheckTime) {
            shoot();
            nextCheckTime = currentTime + TURRET_CHECK_DELAY;
        }
    }
    
    @Override
    public synchronized ByteBuffer serialize() {
        ByteBuffer buf = super.serialize();
        // write 1 byte
        buf.put(hasTarget());
        return buf;
    }

    @Override
    public synchronized void deserialize(ByteBuffer buf) {
        super.deserialize(buf);
        // just grab 1 byte
        targetByte = buf.get();
    }
    
    /**
     * Screw java, it can't convert bool to byte. Do it here implicitly
     * @return 1 if true, 0 if false
     */
    protected abstract byte hasTarget();

    
    
    protected void aim() {
        long currentTime = GameEngine.ticker.getTime();
        if (target == null) {
            this.direction = new Vector2D(Math.sin(currentTime / 360d), Math.cos(currentTime / 360d));
            return;
        }
        this.direction = target.getPosition().subtract(this.getPosition());
        this.direction.normalise();
        this.plane = direction.perpendicular();
        
    }
    
    protected abstract void shoot();
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public double getDrawSize() {
        return 5;
    }
    
    @Override
    public int getMinimapColor() {
        return 0xFFFFFFFF;
    }
    
    @Override
    public DirectionalSprite getDirectionalSprite() {
        return DirectionalSprite.SHOTGUN_DIRECTIONAL_SPRITE;
    }

    @Override
    public String getClientClassName() {
        // One class for both client and server.
        return this.getClass().getName();
    }
}
