package com.knightlore.render;

import java.util.HashMap;
import java.util.Map.Entry;

import com.knightlore.engine.GameObject;
import com.knightlore.game.Player;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.input.BasicController;
import com.knightlore.network.protocol.ClientControl;
import com.knightlore.utils.Vector2D;

public class Camera extends GameObject {
	

    public static final double FIELD_OF_VIEW = -.66;
    private static final double MOTION_BOB_AMOUNT = 7.0;
    private static final double MOTION_BOB_SPEED = 0.15;
    private static final double MOVE_SPEED = .040;

    // will be implemented
    private static final double SPRINT_MULTIPLIER = 1.5D;
    private static final double STRAFE_SPEED = .01;
    private static final double ROTATION_SPEED = .025;

    private int motionOffset;
    private long moveTicks;

    private final Map map;
    private Player player;
    private double xPos, yPos, xDir, yDir, xPlane, yPlane;

    // Maps all inputs that the player could be making to their values.
    private java.util.Map<ClientControl, Byte> inputState = new HashMap<>();
    private java.util.Map<ClientControl, Runnable> ACTION_MAPPINGS = new HashMap<>();

    private static Camera mainCam;
    
    // TODO constructor takes a lot of parameters, maybe use Builder Pattern
    // instead?
    public Camera(double xPos, double yPos, double xDir, double yDir, double xPlane, double yPlane, Map map) {
        super();

        // Map possible inputs to the methods that handle them. Avoids long
        // if-statement chain.
        ACTION_MAPPINGS.put(ClientControl.FORWARD, Camera.this::moveForward);
        ACTION_MAPPINGS.put(ClientControl.ROTATE_ANTI_CLOCKWISE, Camera.this::rotateAntiClockwise);
        ACTION_MAPPINGS.put(ClientControl.BACKWARD, Camera.this::moveBackward);
        ACTION_MAPPINGS.put(ClientControl.ROTATE_CLOCKWISE, Camera.this::rotateClockwise);
        ACTION_MAPPINGS.put(ClientControl.LEFT, Camera.this::strafeLeft);
        ACTION_MAPPINGS.put(ClientControl.RIGHT, Camera.this::strafeRight);

        this.xPos = xPos;
        this.yPos = yPos;
        this.xDir = xDir;
        this.yDir = yDir;
        this.xPlane = xPlane;
        this.yPlane = yPlane;
        this.map = map;

        this.motionOffset = 0;
        this.moveTicks = 0;
        if(mainCam == null){
			mainCam = this;
		}
    }


	/**
	 * 
	 * Returns the main camera. Note: This may be null if the main camera is destroyed.
	 */
	public static Camera mainCamera(){
		return mainCam;
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
        synchronized (inputState) {
            // Check whether each input is triggered - if it is, execute the
            // respective method.
            // DEBUG
            boolean updated = false;
            for (Entry<ClientControl, Byte> entry : inputState.entrySet())
                // For boolean inputs (i.e. all current inputs), 0 represents
                // false.
                if (entry.getValue() != 0) {
                    ACTION_MAPPINGS.get(entry.getKey()).run();
                    updated = true;
                }
            if (updated)
                updateMotionOffset();
        }
    }

    public void setInputState(java.util.Map<ClientControl, Byte> inputState) {
        synchronized (this.inputState) {
            this.inputState = inputState;
        }
    }

    private void updateMotionOffset() {
        moveTicks++;
        this.motionOffset = (int) (Math.abs(Math.sin(moveTicks * MOTION_BOB_SPEED) * MOTION_BOB_AMOUNT));
    }

    public int getMotionOffset() {
        return motionOffset;
    }

    private synchronized void moveForward() {
        Tile xTile = map.getTile((int) (xPos + xDir * MOVE_SPEED), (int) yPos);
        Tile yTile = map.getTile((int) xPos, (int) (yPos + yDir * MOVE_SPEED));
        xPos += xDir * MOVE_SPEED * (1 - xTile.getSolidity());
        yPos += yDir * MOVE_SPEED * (1 - yTile.getSolidity());
    }

    private synchronized void moveBackward() {
        Tile xTile = map.getTile((int) (xPos - xDir * MOVE_SPEED), (int) yPos);
        Tile yTile = map.getTile((int) xPos, (int) (yPos - yDir * MOVE_SPEED));
        xPos -= xDir * MOVE_SPEED * (1 - xTile.getSolidity());
        yPos -= yDir * MOVE_SPEED * (1 - yTile.getSolidity());
    }

    private synchronized void strafeLeft() {
        Tile xTile = map.getTile((int) (xPos - yDir * STRAFE_SPEED), (int) (yPos));
        Tile yTile = map.getTile((int) (xPos), (int) (yPos + xDir * STRAFE_SPEED));
        xPos -= yDir * STRAFE_SPEED * (1 - xTile.getSolidity());
        yPos -= -xDir * STRAFE_SPEED * (1 - yTile.getSolidity());
    }

    private synchronized void strafeRight() {
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
    private synchronized void rotateAntiClockwise() {
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
    private synchronized void rotateClockwise() {
        double oldxDir = xDir;
        xDir = xDir * Math.cos(-ROTATION_SPEED) - yDir * Math.sin(-ROTATION_SPEED);
        yDir = oldxDir * Math.sin(-ROTATION_SPEED) + yDir * Math.cos(-ROTATION_SPEED);
        double oldxPlane = xPlane;
        xPlane = xPlane * Math.cos(-ROTATION_SPEED) - yPlane * Math.sin(-ROTATION_SPEED);
        yPlane = oldxPlane * Math.sin(-ROTATION_SPEED) + yPlane * Math.cos(-ROTATION_SPEED);
    }

    public synchronized double getxPos() {
        return xPos;
    }

    public synchronized double getyPos() {
        return yPos;
    }

    public synchronized void setxPos(double xPos) {
        this.xPos = xPos;
    }

    public synchronized void setyPos(double yPos) {
        this.yPos = yPos;
    }

    public synchronized double getxDir() {
        return xDir;
    }

    public synchronized void setxDir(double xDir) {
        this.xDir = xDir;
    }

    public synchronized double getyDir() {
        return yDir;
    }

    public synchronized void setyDir(double yDir) {
        this.yDir = yDir;
    }

    public synchronized double getxPlane() {
        return xPlane;
    }

    public synchronized void setxPlane(double xPlane) {
        this.xPlane = xPlane;
    }

    public synchronized double getyPlane() {
        return yPlane;
    }

    public synchronized void setyPlane(double yPlane) {
        this.yPlane = yPlane;
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