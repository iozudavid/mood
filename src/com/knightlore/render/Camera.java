package com.knightlore.render;

import com.knightlore.game.area.Map;
import com.knightlore.game.entity.Entity;
import com.knightlore.utils.Vector2D;

public class Camera {

	/* -.66 is a good value. */
	public static final double FIELD_OF_VIEW = -.66;

	/* Variables concerning motion bob. */
	private static final double MOTION_BOB_AMOUNT = 7.0;
	private static final double MOTION_BOB_SPEED = 0.15;

	private static Camera mainCam;

	private int motionOffset;
	private long moveTicks;

	private Entity subject;
	private Object lockForSubject;
	private boolean checked;

	public Camera(Map map) {
		super();

		this.motionOffset = 0;
		this.moveTicks = 0;
		this.lockForSubject = new Object();
		this.checked = false;
		
		if (mainCam == null)
			mainCam = this;
	}

	/**
	 * 
	 * Returns the main camera. Note: This may be null if the main camera is
	 * destroyed.
	 */
	public static Camera mainCamera() {
		return mainCam;
	}

	/*
	 * we need to wait til the subject is set
	 * unless it will return null when trying
	 * to get position/direction/plane
	 */
	private synchronized void waitSubjectToBeNotNull() {
		synchronized (this) {
			while (checked==false) {
				try {
					wait();
				} catch (InterruptedException e) {
					System.err.println("Unexpected interruption while waiting for subject set " + e.getMessage());
				}
			}
		}
	}

	public synchronized Vector2D getPosition() {
		waitSubjectToBeNotNull();
		synchronized (this.lockForSubject) {
			return subject.getPosition();
		}
	}

	public synchronized Vector2D getDirection() {
		waitSubjectToBeNotNull();
		synchronized (this.lockForSubject) {
			return subject.getDirection();
		}
	}

	// TODO: implement motion bob using offset
	private void updateMotionOffset() {
		moveTicks++;
		this.motionOffset = (int) (Math.abs(Math.sin(moveTicks * MOTION_BOB_SPEED) * MOTION_BOB_AMOUNT));
	}

	public int getMotionOffset() {
		return motionOffset;
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
		waitSubjectToBeNotNull();
		synchronized (this.lockForSubject) {
			return subject.getPlane();
		}
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
		synchronized (this.lockForSubject) {
			return subject;
		}
	}

	public void setSubject(Entity subject) {
		synchronized (this.lockForSubject) {
			synchronized(this){
				this.subject = subject;
				notify();
				checked=true;
			}
		}
	}

}
