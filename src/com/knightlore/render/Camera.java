package com.knightlore.render;

import com.knightlore.engine.GameObject;
import com.knightlore.game.area.Map;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Vector2D;

public class Camera extends GameObject {

    public static final double FIELD_OF_VIEW = -.66;
    private static final double MOTION_BOB_AMOUNT = 7.0;
    private static final double MOTION_BOB_SPEED = 0.15;

    private int motionOffset;
    private long moveTicks;

    private Entity subject;

    // TODO constructor takes a lot of parameters, maybe use Builder Pattern
    // instead?
    public Camera(Map map) {
        super();

        this.motionOffset = 0;
        this.moveTicks = 0;
    }

    @Override
    public Vector2D getPosition() {
        return subject.getPosition();
    }

    public Vector2D getDirection() {
        return subject.getDirection();
    }

    @Override
    public void onUpdate() {
    }

    private void updateMotionOffset() {
        moveTicks++;
        this.motionOffset = (int) (Math.abs(Math.sin(moveTicks * MOTION_BOB_SPEED) * MOTION_BOB_AMOUNT));
    }

    public int getMotionOffset() {
        return motionOffset;
    }

    public synchronized double getxPos() {
        return getPosition().getX();
    }

    public synchronized double getyPos() {
        return getPosition().getY();
    }

    public synchronized double getxDir() {
        return getDirection().getX();
    }

    public synchronized double getyDir() {
        return getDirection().getY();
    }
    
    public Vector2D getPlane() {
        return subject.getPlane();
    }

    public synchronized double getxPlane() {
        return getPlane().getX();
    }

    public synchronized double getyPlane() {
        return getPlane().getY();
    }
    
    public Entity getSubject() {
        return subject;
    }
    
    public void setSubject(Entity subject) {
        this.subject = subject;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
    }

}