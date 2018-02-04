package com.knightlore.render;

import java.util.HashMap;
import java.util.Map.Entry;

import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.network.protocol.ClientControl;

public class Camera {

    public static final double FIELD_OF_VIEW = -0.66;
    private static final double MOVE_SPEED = .084;
    private static final double STRAFE_SPEED = .04;
    private static final double ROTATION_SPEED = .045;

    private final Map map;
    private double xPos, yPos, xDir, yDir, xPlane, yPlane;
    // Maps all inputs that the player could be making to their values.
    private java.util.Map<ClientControl, Byte> inputState = new HashMap<>();
    private java.util.Map<ClientControl, Runnable> ACTION_MAPPINGS = new HashMap<>();
    private Object lock;

    // TODO constructor takes a lot of parameters, maybe use Builder Pattern
    // instead?
    public Camera(double xPos, double yPos, double xDir, double yDir,
            double xPlane, double yPlane, Map map) {
        super();
        this.xPos = xPos;
        this.yPos = yPos;
        this.xDir = xDir;
        this.yDir = yDir;
        this.xPlane = xPlane;
        this.yPlane = yPlane;
        this.map = map;
        this.lock = new Object();
        // Map possible inputs to the methods that handle them. Avoids long
        // if-statement chain.
        ACTION_MAPPINGS.put(ClientControl.FORWARD, Camera.this::moveForward);
        ACTION_MAPPINGS.put(ClientControl.ROTATE_ANTI_CLOCKWISE,
                Camera.this::rotateAntiClockwise);
        ACTION_MAPPINGS.put(ClientControl.BACKWARD, Camera.this::moveBackward);
        ACTION_MAPPINGS.put(ClientControl.ROTATE_CLOCKWISE,
                Camera.this::rotateClockwise);
        ACTION_MAPPINGS.put(ClientControl.LEFT, Camera.this::strafeLeft);
        ACTION_MAPPINGS.put(ClientControl.RIGHT, Camera.this::strafeRight);
    }

    public void update() {
        synchronized (inputState) {
            // Check whether each input is triggered - if it is, execute the
            // respective method.
            // DEBUG
            String s = "";
            for (Entry<ClientControl, Byte> entry : inputState.entrySet())
                // For boolean inputs (i.e. all current inputs), 0 represents
                // false.
                if (entry.getValue() != 0) {
                    ACTION_MAPPINGS.get(entry.getKey()).run();
                    s += entry.getKey().name();
                }
            if (!s.equals(""))
                System.out.println(s);
        }
    }

    public void setInputState(java.util.Map<ClientControl, Byte> inputState) {
        synchronized (this.inputState) {
            this.inputState = inputState;
        }
    }

    private void moveForward() {
		synchronized (this.lock) {
			Tile xTile = map.getTile((int) (xPos + xDir * MOVE_SPEED), (int) yPos);
			Tile yTile = map.getTile((int) xPos, (int) (yPos + yDir * MOVE_SPEED));
			xPos += xDir * MOVE_SPEED * (1 - xTile.getSolidity());
			yPos += yDir * MOVE_SPEED * (1 - yTile.getSolidity());
		}
    }

    private void moveBackward() {
		synchronized (this.lock) {
			Tile xTile = map.getTile((int) (xPos - xDir * MOVE_SPEED), (int) yPos);
			Tile yTile = map.getTile((int) xPos, (int) (yPos - yDir * MOVE_SPEED));
			xPos -= xDir * MOVE_SPEED * (1 - xTile.getSolidity());
			yPos -= yDir * MOVE_SPEED * (1 - yTile.getSolidity());
		}
    }

    private void strafeLeft() {
		synchronized (this.lock) {
			Tile xTile = map.getTile((int) (xPos - yDir * STRAFE_SPEED), (int) (yPos));
			Tile yTile = map.getTile((int) (xPos), (int) (yPos + xDir * STRAFE_SPEED));
			xPos -= yDir * STRAFE_SPEED * (1 - xTile.getSolidity());
			yPos -= -xDir * STRAFE_SPEED * (1 - yTile.getSolidity());
		}
    }

    private void strafeRight() {
		synchronized (this.lock) {
			Tile xTile = map.getTile((int) (xPos + yDir * STRAFE_SPEED), (int) (yPos));
			Tile yTile = map.getTile((int) (xPos), (int) (yPos + -xDir * STRAFE_SPEED));
			xPos += yDir * STRAFE_SPEED * (1 - xTile.getSolidity());
			yPos += -xDir * STRAFE_SPEED * (1 - yTile.getSolidity());
		}
    }

    /**
     * Rotation is simply multiplication by the rotation matrix. We take the
     * position and plane vectors, then multiply them by the rotation matrix
     * (whose parameter is ROTATION_SPEED). This lets us rotate.
     */
    private void rotateAntiClockwise() {
		synchronized (this.lock) {
			double oldxDir = xDir;
			xDir = xDir * Math.cos(ROTATION_SPEED) - yDir * Math.sin(ROTATION_SPEED);
			yDir = oldxDir * Math.sin(ROTATION_SPEED) + yDir * Math.cos(ROTATION_SPEED);
			double oldxPlane = xPlane;
			xPlane = xPlane * Math.cos(ROTATION_SPEED) - yPlane * Math.sin(ROTATION_SPEED);
			yPlane = oldxPlane * Math.sin(ROTATION_SPEED) + yPlane * Math.cos(ROTATION_SPEED);
		}
    }

    /**
     * Same as rotating left but clockwise this time.
     */
    private void rotateClockwise() {
		synchronized (this.lock) {
			double oldxDir = xDir;
			xDir = xDir * Math.cos(-ROTATION_SPEED) - yDir * Math.sin(-ROTATION_SPEED);
			yDir = oldxDir * Math.sin(-ROTATION_SPEED) + yDir * Math.cos(-ROTATION_SPEED);
			double oldxPlane = xPlane;
			xPlane = xPlane * Math.cos(-ROTATION_SPEED) - yPlane * Math.sin(-ROTATION_SPEED);
			yPlane = oldxPlane * Math.sin(-ROTATION_SPEED) + yPlane * Math.cos(-ROTATION_SPEED);
		}
    }

    public double getxPos() {
		synchronized (this.lock) {
			return xPos;
		}
    }

    public double getyPos() {
    	synchronized(this.lock){
    		return yPos;
    	}
    }
    
    public void setxPos(double xPos) {
        synchronized (this.lock) {
            this.xPos = xPos;
        }
    }

    public void setyPos(double yPos) {
        synchronized(this.lock){
            this.yPos = yPos;
        }
    }

    public double getxDir() {
    	synchronized(this.lock){
    		return xDir;
    	}
    }

    public void setxDir(double xDir) {
    	synchronized(this.lock){
    		this.xDir = xDir;
    	}
    }

    public double getyDir() {
    	synchronized(this.lock){
    		return yDir;
    	}
    }

    public void setyDir(double yDir) {
    	synchronized(this.lock){
    		this.yDir = yDir;
    	}
    }

    public double getxPlane() {
    	synchronized(this.lock){
    		return xPlane;
    	}
    }

    public void setxPlane(double xPlane) {
    	synchronized(this.lock){
    		this.xPlane = xPlane;
    	}
    }

    public double getyPlane() {
    	synchronized(this.lock){
    		return yPlane;
    	}
    }

    public void setyPlane(double yPlane) {
    	synchronized(this.lock){
    		this.yPlane = yPlane;
    	}
    }

}
