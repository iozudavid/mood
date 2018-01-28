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
            //DEBUG
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
        synchronized (inputState) {
            this.inputState = inputState;
        }
    }

    private void moveForward() {
        Tile xTile = map.getTile((int) (xPos + xDir * MOVE_SPEED), (int) yPos);
        Tile yTile = map.getTile((int) xPos, (int) (yPos + yDir * MOVE_SPEED));
        xPos += xDir * MOVE_SPEED * (1 - xTile.getSolidity());
        yPos += yDir * MOVE_SPEED * (1 - yTile.getSolidity());
    }

    private void moveBackward() {
        Tile xTile = map.getTile((int) (xPos - xDir * MOVE_SPEED), (int) yPos);
        Tile yTile = map.getTile((int) xPos, (int) (yPos - yDir * MOVE_SPEED));
        xPos -= xDir * MOVE_SPEED * (1 - xTile.getSolidity());
        yPos -= yDir * MOVE_SPEED * (1 - yTile.getSolidity());
    }

    private void strafeLeft() {
        Tile xTile = map.getTile((int) (xPos - yDir * STRAFE_SPEED),
                (int) (yPos));
        Tile yTile = map.getTile((int) (xPos),
                (int) (yPos + xDir * STRAFE_SPEED));
        xPos -= yDir * STRAFE_SPEED * (1 - xTile.getSolidity());
        yPos -= -xDir * STRAFE_SPEED * (1 - yTile.getSolidity());
    }

    private void strafeRight() {
        Tile xTile = map.getTile((int) (xPos + yDir * STRAFE_SPEED),
                (int) (yPos));
        Tile yTile = map.getTile((int) (xPos),
                (int) (yPos + -xDir * STRAFE_SPEED));
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
        xDir = xDir * Math.cos(ROTATION_SPEED)
                - yDir * Math.sin(ROTATION_SPEED);
        yDir = oldxDir * Math.sin(ROTATION_SPEED)
                + yDir * Math.cos(ROTATION_SPEED);
        double oldxPlane = xPlane;
        xPlane = xPlane * Math.cos(ROTATION_SPEED)
                - yPlane * Math.sin(ROTATION_SPEED);
        yPlane = oldxPlane * Math.sin(ROTATION_SPEED)
                + yPlane * Math.cos(ROTATION_SPEED);
    }

    /**
     * Same as rotating left but clockwise this time.
     */
    private void rotateClockwise() {
        double oldxDir = xDir;
        xDir = xDir * Math.cos(-ROTATION_SPEED)
                - yDir * Math.sin(-ROTATION_SPEED);
        yDir = oldxDir * Math.sin(-ROTATION_SPEED)
                + yDir * Math.cos(-ROTATION_SPEED);
        double oldxPlane = xPlane;
        xPlane = xPlane * Math.cos(-ROTATION_SPEED)
                - yPlane * Math.sin(-ROTATION_SPEED);
        yPlane = oldxPlane * Math.sin(-ROTATION_SPEED)
                + yPlane * Math.cos(-ROTATION_SPEED);
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
