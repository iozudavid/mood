package com.knightlore.render;

import com.knightlore.engine.GameObject;
import com.knightlore.engine.input.Controller;
import com.knightlore.engine.input.InputManager;
import com.knightlore.engine.input.Keyboard;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.input.BasicController;
import com.knightlore.utils.Vector2D;

public class Camera extends GameObject {

	private static final double MOTION_BOB_AMOUNT = 7.0;
	private static final double MOTION_BOB_SPEED = 0.15;
	private int motionOffset;
	private long moveTicks;

	public static final double FIELD_OF_VIEW = -.66;
	private static final double MOVE_SPEED = .100;
	private static final double SPRINT_MULTIPLIER = 1.5D;
	private static final double STRAFE_SPEED = .04;
	private static final double ROTATION_SPEED = .045;

	private final Map map;
	private double xPos, yPos, xDir, yDir, xPlane, yPlane;

	private Keyboard keyboard;
	private Controller controller;

	// TODO constructor takes a lot of parameters, maybe use Builder Pattern
	// instead?
	public Camera(double xPos, double yPos, double xDir, double yDir, double xPlane, double yPlane, Map map) {
		super();
		this.xPos = xPos;
		this.yPos = yPos;
		this.xDir = xDir;
		this.yDir = yDir;
		this.xPlane = xPlane;
		this.yPlane = yPlane;
		this.map = map;

		this.motionOffset = 0;
		this.moveTicks = 0;

		this.keyboard = InputManager.getKeyboard();
		this.controller = new BasicController();
	}

	@Override
	public Vector2D getPosition() {
		return new Vector2D(xPos, yPos);
	}

	public Vector2D getDirection() {
		return new Vector2D(xDir, yDir);
	}

	@Override
	public void onUpdate() {
		double oX = xPos, oY = yPos;

		if (keyboard.isPressed(controller.moveForward())) {
			moveForward();
		}

		if (keyboard.isPressed(controller.rotateAntiClockwise())) {
			rotateAntiClockwise();
		}

		if (keyboard.isPressed(controller.moveBackward())) {
			moveBackward();
		}

		if (keyboard.isPressed(controller.rotateClockwise())) {
			rotateClockwise();
		}

		if (keyboard.isPressed(controller.moveLeft())) {
			strafeLeft();
		}

		if (keyboard.isPressed(controller.moveRight())) {
			strafeRight();
		}

		if (oX != xPos || oY != yPos) {
			updateMotionOffset();
		}

	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDestroy() {
	}

	public boolean isMoving() {
		return keyboard.isPressed(controller.moveForward()) || keyboard.isPressed(controller.moveBackward())
				|| keyboard.isPressed(controller.moveLeft()) || keyboard.isPressed(controller.moveRight());
	}

	private void updateMotionOffset() {
		moveTicks++;

		double speed = MOTION_BOB_SPEED, amount = MOTION_BOB_AMOUNT;
		if (keyboard.isPressed(controller.sprint())) {
			amount *= SPRINT_MULTIPLIER * 1.1;
		}

		this.motionOffset = (int) (Math.abs(Math.sin(moveTicks * speed) * amount));
	}

	public int getMotionOffset() {
		return motionOffset;
	}

	private void moveForward() {
		// if we're sprinting, apply the speed multiplier.
		double speed = MOVE_SPEED * (keyboard.isPressed(controller.sprint()) ? SPRINT_MULTIPLIER : 1);

		Tile xTile = map.getTile((int) (xPos + xDir * speed), (int) yPos);
		Tile yTile = map.getTile((int) xPos, (int) (yPos + yDir * speed));
		xPos += xDir * speed * (1 - xTile.getSolidity());
		yPos += yDir * speed * (1 - yTile.getSolidity());
	}

	private void moveBackward() {
		Tile xTile = map.getTile((int) (xPos - xDir * MOVE_SPEED), (int) yPos);
		Tile yTile = map.getTile((int) xPos, (int) (yPos - yDir * MOVE_SPEED));
		xPos -= xDir * MOVE_SPEED * (1 - xTile.getSolidity());
		yPos -= yDir * MOVE_SPEED * (1 - yTile.getSolidity());
	}

	private void strafeLeft() {
		Tile xTile = map.getTile((int) (xPos - yDir * STRAFE_SPEED), (int) (yPos));
		Tile yTile = map.getTile((int) (xPos), (int) (yPos + xDir * STRAFE_SPEED));
		xPos -= yDir * STRAFE_SPEED * (1 - xTile.getSolidity());
		yPos -= -xDir * STRAFE_SPEED * (1 - yTile.getSolidity());
	}

	private void strafeRight() {
		Tile xTile = map.getTile((int) (xPos + yDir * STRAFE_SPEED), (int) (yPos));
		Tile yTile = map.getTile((int) (xPos), (int) (yPos + -xDir * STRAFE_SPEED));
		xPos += yDir * STRAFE_SPEED * (1 - xTile.getSolidity());
		yPos += -xDir * STRAFE_SPEED * (1 - yTile.getSolidity());
	}

	/**
	 * Rotation is simply multiplication by the rotation matrix. We take the
	 * position and plane vectors, then multiply them by the rotation matrix
	 * (whose parameter is ROTATION_SPEED). This lets us rotate.
	 */
	private void rotateAntiClockwise() {
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
	private void rotateClockwise() {
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
