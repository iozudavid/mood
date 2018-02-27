package com.knightlore.ai;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.knightlore.game.Team;
import com.knightlore.game.entity.Entity;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Utils;
import com.knightlore.utils.Vector2D;

public abstract class TurretShared extends Entity {
    
    protected int damage;
    protected Team team = Team.none;
    
    protected long nextCheckTime = 0;
    protected static final long TURRET_CHECK_DELAY = 20;
    
    protected Entity target = null;
    protected byte targetByte = 0;
    protected double sqrRange = 25;
    
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
    public synchronized ByteBuffer serialize() {
        ByteBuffer buf = super.serialize();
        // write 1 byte
        buf.put(getTargetByte());
        return buf;
    }

    @Override
    public synchronized void deserialize(ByteBuffer buf) {
        super.deserialize(buf);
        // just grab 1 byte
        targetByte = buf.get();
    }
    
    protected abstract boolean hasTarget();
    
    /**
     * Screw java, it can't convert bool to byte. Do it here implicitly
     * @return 1 if true, 0 if false
     */
    private byte getTargetByte() {
        if(hasTarget()) {
            return 1;
        }
        else {
            return 0;
        }
    }
    
    protected abstract void shoot();
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public double getDrawSize() {
        return 0.5f;
    }
    
    @Override
    public int getMinimapColor() {
        return Utils.colorForTeam(team);
    }
    
    @Override
    public DirectionalSprite getDirectionalSprite() {
        return DirectionalSprite.TURRET_DIRECTIONAL_SPRITE;
    }

    @Override
    public String getClientClassName() {
        // turret client please :)
        return TurretClient.class.getName();
    }
}
