package com.knightlore.render;

import com.knightlore.GameSettings;
import com.knightlore.engine.audio.FootstepHandler;
import com.knightlore.game.area.Map;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Vector2D;

/**
 * Viewport into the world. A camera can be bound to any entity (its 'subject')
 * to view the world from the point of view of something/someone else.
 *
 * @author Joe Ellis
 */
public class Camera implements IRenderable {
    
    /**
     * This variable dictates the length of the plane (handedness) vector. The
     * larger this value, the large the field of view. .66 is a good value.
     */
    public static final double FIELD_OF_VIEW = .66D;
    /**
     * How much should the camera bob when the player moves?
     */
    private static final double MOTION_BOB_AMOUNT = GameSettings.motionBob ? 15.0D : 0;
    /**
     * How fast should the camera bob when the player moves?
     */
    private static final double MOTION_BOB_SPEED = GameSettings.motionBob ? 1.5D : 0;
    private final FootstepHandler fhandler;
    /**
     * The entity that this camera is tracking.
     */
    private Entity subject;
    
    /**
     * The last position of the entity. This is used to calculate
     * distanceTravelled, which is required to render entities.
     */
    private Vector2D lastPos;
    
    /**
     * Indicates how far the entity related to this camera has travelled since
     * the last update.
     */
    private double distanceTraveled;
    
    public Camera(Map map) {
        super();
        this.lastPos = new Vector2D(0, 0);
        this.fhandler = new FootstepHandler();
    }
    
    @Override
    public void render(PixelBuffer pix, int x, int y) {
        // Proxy method -- if the subject wants to do any rendering (e.g. a
        // player wants to render the current weapon) they can.
        subject.render(pix, x, y, getDistanceTraveled());
        fhandler.playFootstepIfNecessary(getDistanceTraveled());
    }
    
    /**
     * Calculates how far the entity has moved since the last update.
     *
     * @return how far the entity has moved since the last update.
     */
    private double getDisplacementDelta() {
        Vector2D current = subject.getPosition();
        double displacement = current.subtract(lastPos).magnitude();
        lastPos = current;
        return displacement;
    }
    
    /**
     * How far has the entity travelled?
     *
     * @return how far the entity has travelled since the last update.
     */
    private double getDistanceTraveled() {
        distanceTraveled += getDisplacementDelta();
        return distanceTraveled;
    }
    
    /**
     * Get how much to offset the renderer by when rendering from this camera's
     * perspective.
     *
     * @return the motion bob offset.
     */
    public int getMotionBobOffset() {
        double off = Math.sin(getDistanceTraveled() * MOTION_BOB_SPEED) * MOTION_BOB_AMOUNT;
        return (int)Math.abs(off);
    }
    
    /**
     * Get the position of the camera subject.
     *
     * @return the position of the camera subject.
     */
    public synchronized Vector2D getPosition() {
        return subject.getPosition();
    }
    
    /**
     * Gets the direction that the camera subject is facing.
     *
     * @return the direction that the camera subject is facing.
     */
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
    
    /**
     * Gets the plane/handedness vector, adjusted to match the FIELD_OF_VIEW
     * variable.
     *
     * @return the plane/handedness vector adjusted for FIELD_OF_VIEW.
     */
    public synchronized Vector2D getPlane() {
        Vector2D plane = subject.getPlane();
        return new Vector2D(plane.getX() * FIELD_OF_VIEW, plane.getY() * FIELD_OF_VIEW);
    }
    
    public synchronized double getxPlane() {
        Vector2D plane = this.getPlane();
        return plane.getX();
    }
    
    public synchronized double getyPlane() {
        Vector2D plane = this.getPlane();
        return plane.getY();
    }
    
    /**
     * Get the entity the camera is currently tracking.
     *
     * @return the entity the camera is currently tracking.
     */
    public Entity getSubject() {
        return subject;
    }
    
    /**
     * Set the camera subject.
     *
     * @param subject the new camera subject entity.
     */
    public void setSubject(Entity subject) {
        this.subject = subject;
        this.lastPos = subject.getPosition();
        this.distanceTraveled = 0;
    }
    
    /**
     * Are we tracking a subject or not?
     *
     * @return true if we have a subject set, false otherwise.
     */
    public boolean isSubjectSet() {
        return subject != null;
    }
    
}
