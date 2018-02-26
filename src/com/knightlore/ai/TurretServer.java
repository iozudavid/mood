package com.knightlore.ai;

import com.knightlore.utils.Vector2D;

public final class TurretServer extends TurretShared {
    
    public TurretServer(double size, Vector2D position, Vector2D direction) {
        super(size, position, direction);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    protected void shoot() {
        if (target == null) {
            System.out.println("No target");
            return;
        }
        System.out.println("Shooting at position " + target.getPosition());
    }

    @Override
    protected byte hasTarget() {
        if(target == null) {
            return 0;
        }
        else {
            return 1;
        }
    }
}
