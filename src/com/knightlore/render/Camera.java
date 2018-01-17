package com.knightlore.render;

import com.knightlore.engine.Updateable;
import com.knightlore.game.Map;
import com.knightlore.input.Controller;
import com.knightlore.input.Keyboard;

public class Camera implements Updateable {

	private double xPos, yPos, xDir, yDir, xPlane, yPlane;
	public final double MOVE_SPEED = .08, STRAFE_SPEED = .04;
	public final double ROTATION_SPEED = .045;
	public static final double FIELD_OF_VIEW = -0.66;

	private Map map;

	public Camera(double xPos, double yPos, double xDir, double yDir, double xPlane, double yPlane, Map map) {
		super();
		this.xPos = xPos;
		this.yPos = yPos;
		this.xDir = xDir;
		this.yDir = yDir;
		this.xPlane = xPlane;
		this.yPlane = yPlane;
		this.map = map;
	}

	@Override
	public void tick(long ticker) {

		int[][] mapArr = map.getMapArray();

		Controller controller = new Controller(Keyboard.getInstance());
		if (controller.w()) {
			if (mapArr[(int) (xPos + xDir * MOVE_SPEED)][(int) (yPos)] == 0)
				xPos += xDir * MOVE_SPEED;
			if (mapArr[(int) xPos][(int) (yPos + yDir * MOVE_SPEED)] == 0)
				yPos += yDir * MOVE_SPEED;
		}

		if (controller.a()) {
			rotateLeft();
		}

		if (controller.s()) {
			if (mapArr[(int) (xPos - xDir * MOVE_SPEED)][(int) (yPos)] == 0)
				xPos -= xDir * MOVE_SPEED;
			if (mapArr[(int) xPos][(int) (yPos - yDir * MOVE_SPEED)] == 0)
				yPos -= yDir * MOVE_SPEED;
		}

		if (controller.d()) {
			rotateRight();
		}

		if (controller.q() || controller.e()) {
			double newxPos = xPos + (controller.q() ? -1 : 1) * yDir * STRAFE_SPEED;
			double newyPos = yPos + (controller.q() ? -1 : 1) * -xDir * STRAFE_SPEED;

			if (mapArr[(int) (newxPos)][(int) (yPos)] == 0) {
				xPos = newxPos;
			}

			if (mapArr[(int) (xPos)][(int) (newyPos)] == 0) {
				yPos = newyPos;
			}

		}
	}

	/**
	 * Rotation is simply multiplication by the rotation matrix. We take the
	 * position and plane vectors, then multiply them by the rotation matrix
	 * (whose parameter is ROTATION_SPEED). This lets us rotate.
	 */
	private void rotateLeft() {
		double oldxDir = xDir;
		xDir = xDir * Math.cos(ROTATION_SPEED) - yDir * Math.sin(ROTATION_SPEED);
		yDir = oldxDir * Math.sin(ROTATION_SPEED) + yDir * Math.cos(ROTATION_SPEED);
		double oldxPlane = xPlane;
		xPlane = xPlane * Math.cos(ROTATION_SPEED) - yPlane * Math.sin(ROTATION_SPEED);
		yPlane = oldxPlane * Math.sin(ROTATION_SPEED) + yPlane * Math.cos(ROTATION_SPEED);
	}

	/**
	 * Same as rotating left but clockwise this time.
	 */
	private void rotateRight() {
		double oldxDir = xDir;
		xDir = xDir * Math.cos(-ROTATION_SPEED) - yDir * Math.sin(-ROTATION_SPEED);
		yDir = oldxDir * Math.sin(-ROTATION_SPEED) + yDir * Math.cos(-ROTATION_SPEED);
		double oldxPlane = xPlane;
		xPlane = xPlane * Math.cos(-ROTATION_SPEED) - yPlane * Math.sin(-ROTATION_SPEED);
		yPlane = oldxPlane * Math.sin(-ROTATION_SPEED) + yPlane * Math.cos(-ROTATION_SPEED);
	}

	public double getxPos() {
		return xPos;
	}

	public double getyPos() {
		return yPos;
	}

	public double getxDir() {
		return xDir;
	}

	public void setxDir(double xDir) {
		this.xDir = xDir;
	}

	public double getyDir() {
		return yDir;
	}

	public void setyDir(double yDir) {
		this.yDir = yDir;
	}

	public double getxPlane() {
		return xPlane;
	}

	public void setxPlane(double xPlane) {
		this.xPlane = xPlane;
	}

	public double getyPlane() {
		return yPlane;
	}

	public void setyPlane(double yPlane) {
		this.yPlane = yPlane;
	}

}
