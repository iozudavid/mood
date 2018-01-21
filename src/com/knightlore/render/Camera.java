package com.knightlore.render;

import com.knightlore.engine.IUpdateable;
import com.knightlore.engine.Input;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.input.Controller;

public class Camera implements IUpdateable {

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
		Controller controller = new Controller(Input.getKeyboard());
		if (controller.w()) {
			Tile xTile = map.getTile((int) (xPos + xDir * MOVE_SPEED), (int) yPos);
			Tile yTile = map.getTile((int) xPos, (int) (yPos + yDir * MOVE_SPEED));
			xPos += xDir * MOVE_SPEED * (1 - xTile.getSolidity());
			yPos += yDir * MOVE_SPEED * (1 - yTile.getSolidity());
		}

		if (controller.a()) {
			rotateLeft();
		}

		if (controller.s()) {
			Tile xTile = map.getTile((int) (xPos - xDir * MOVE_SPEED), (int) yPos);
			Tile yTile = map.getTile((int) xPos, (int) (yPos - yDir * MOVE_SPEED));
			xPos -= xDir * MOVE_SPEED * (1 - xTile.getSolidity());
			yPos -= yDir * MOVE_SPEED * (1 - yTile.getSolidity());
		}

		if (controller.d()) {
			rotateRight();
		}

		Tile xTile = map.getTile((int) (xPos + yDir * STRAFE_SPEED), (int) (yPos));
		Tile yTile = map.getTile((int) (xPos), (int) (yPos + -xDir * STRAFE_SPEED));

		if (controller.q()) {
			xPos -= yDir * STRAFE_SPEED * (1 - xTile.getSolidity());
			yPos -= -xDir * STRAFE_SPEED * (1 - yTile.getSolidity());
		}

		if (controller.e()) {
			xPos += yDir * STRAFE_SPEED * (1 - xTile.getSolidity());
			yPos += -xDir * STRAFE_SPEED * (1 - yTile.getSolidity());
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
