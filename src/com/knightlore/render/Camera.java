package com.knightlore.render;

import com.knightlore.game.area.Map;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Vector2D;

/**
 * Viewport into the world. A camera can be bound to any entity (its 'subject')
 * to view the world from the point of view of something/someone else.
 * 
 * @author Joe Ellis
 *
 */
public class Camera {

    /* -.66 is a good value. */
    public static final double FIELD_OF_VIEW = .66;

    /* Variables concerning motion bob. */
    private static final double MOTION_BOB_AMOUNT = 10.0;
    private static final double MOTION_BOB_SPEED = 0.15 * 10;

    private Entity subject;
    private Vector2D lastPos;
    private double distanceTraveled;

    public Camera(Map map) {
        super();
        this.lastPos = new Vector2D(0, 0);
    }

    private double getDisplacementDelta() {
        Vector2D current = subject.getPosition();
        double displacement = current.subtract(lastPos).magnitude();
        lastPos = current;
        return displacement;
    }

    private double getDistanceTraveled() {
        distanceTraveled += getDisplacementDelta();
        return distanceTraveled;
    }

    public int getMotionBobOffset() {
        double off = Math.sin(getDistanceTraveled() * MOTION_BOB_SPEED) * MOTION_BOB_AMOUNT;
        return (int) Math.abs(off);
    }

    public synchronized Vector2D getPosition() {
        return subject.getPosition();
    }

    public synchronized Vector2D getDirection() {
        return subject.getDirection();
    }

    public synchronized double getxPos() {
        Vector2D pos = this.getPosition();
        return pos.getX();
    }

    public synchronized double getyPos() {
        Vector2D pos = this.getPosition();
        return pos.getY();
    }

    public synchronized double getxDir() {
        Vector2D dir = this.getDirection();
        return dir.getX();
    }

    public synchronized double getyDir() {
        Vector2D dir = this.getDirection();
        return dir.getY();
    }

    public synchronized Vector2D getPlane() {
        Vector2D plane = subject.getPlane();
        Vector2D fovAdjustedPlane = new Vector2D(plane.getX(), plane.getY() * FIELD_OF_VIEW);
        return fovAdjustedPlane;
    }

    public synchronized double getxPlane() {
        Vector2D plane = this.getPlane();
        return plane.getX();
    }

    public synchronized double getyPlane() {
        Vector2D plane = this.getPlane();
        return plane.getY();
    }

    public Entity getSubject() {
        return subject;
    }

    public void setSubject(Entity subject) {
        this.subject = subject;
        this.lastPos = subject.getPosition();
    }

    public boolean isSubjectSet() {
        return subject != null;
    }

}
