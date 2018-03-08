package com.knightlore.network.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

import com.knightlore.ai.InputModule;
import com.knightlore.ai.RemoteInput;
import com.knightlore.engine.GameEngine;
import com.knightlore.game.Player;
import com.knightlore.game.area.Map;
import com.knightlore.game.tile.Tile;
import com.knightlore.network.protocol.ClientController;
import com.knightlore.network.protocol.ClientProtocol;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.utils.Vector2D;

public class PredictedState {
	
	private ByteBuffer currentState;
	private String uuid;
	private Vector2D position;
	private Vector2D deltaPosition;
	private Vector2D direction;
	private Vector2D deltaDirection;
	private Vector2D plane;
	private Vector2D deltaPlane;
	private boolean shootOnNext;
	private double time;
    protected double moveSpeed = .040;
    protected double strafeSpeed = .01;
    protected double rotationSpeed = .025;
	protected Map map;
    private java.util.Map<ClientController, Byte> inputState = new HashMap<>();
    private InputModule inputModule = null;
    private java.util.Map<ClientController, Runnable> ACTION_MAPPINGS = new HashMap<>();
	private Player currentPlayer;
    
    
	public PredictedState(Player currentPlayer, ByteBuffer b){
		this.currentPlayer = currentPlayer;
		this.inputModule = new RemoteInput();

        // Map possible inputs to the methods that handle them. Avoids long
        // if-statement chain.
        ACTION_MAPPINGS.put(ClientController.FORWARD, this::moveForward);
        ACTION_MAPPINGS.put(ClientController.ROTATE_ANTI_CLOCKWISE, this::rotateAntiClockwise);
        ACTION_MAPPINGS.put(ClientController.BACKWARD, this::moveBackward);
        ACTION_MAPPINGS.put(ClientController.ROTATE_CLOCKWISE, this::rotateClockwise);
        ACTION_MAPPINGS.put(ClientController.LEFT, this::strafeLeft);
        ACTION_MAPPINGS.put(ClientController.RIGHT, this::strafeRight);
        ACTION_MAPPINGS.put(ClientController.SHOOT, this::shoot);
		this.currentState = b;
		this.currentState.rewind();
		depack();
		this.map = GameEngine.getSingleton().getWorld().getMap();
	}
	
	private void depack(){
		this.uuid=(NetworkUtils.getStringFromBuf(currentState));
		//method remote
		NetworkUtils.getStringFromBuf(currentState);
		//size
		currentState.getDouble();
		double xPos = currentState.getDouble();
		double yPos = currentState.getDouble();
		this.position = new Vector2D(xPos, yPos);
		double xDir = currentState.getDouble();
		double yDir = currentState.getDouble();
		this.direction = new Vector2D(xDir, yDir);
		double xPlane = currentState.getDouble();
		double yPlane = currentState.getDouble();
		this.plane = new Vector2D(xPlane, yPlane);
		this.shootOnNext = currentState.getInt()==1 ? true : false;
		this.time = this.currentState.getDouble();
		this.deltaPosition = new Vector2D(0,0);
		this.deltaDirection = new Vector2D(0,0);
		this.deltaDirection = new Vector2D(0,0);
	}

	public String getUuid() {
		return uuid;
	}

	public Vector2D getPosition(){
		return this.position;
	}
	
	public Vector2D getDirection(){
		return this.direction;
	}
	
	public Vector2D getPlane(){
		return this.plane;
	}
	
	public Vector2D getDeltaPosition(){
		return this.deltaPosition;
	}
	
	public Vector2D getDeltaDirection(){
		return this.deltaDirection;
	}
	
	public Vector2D getDeltaPlane(){
		return this.deltaPlane;
	}
	
	public boolean getShootAbility(){
		return this.shootOnNext;
	}
	
	public double getTime(){
		return this.time;
	}
	
	public void setOnShootNext(boolean b){
		this.shootOnNext = b;
	}
	
	public void setTime(double newTime){
		this.time = newTime;
	}
	
	public void setPosition(double x, double y){
		this.position = new Vector2D(x, y);
	}
	
	public void setDirection(double x, double y){
		this.direction = new Vector2D(x, y);
	}
	
	public void setPlane(double x, double y){
		this.plane = new Vector2D(x, y);
	}
	
	public void setDeltaPosition(double x, double y){
		this.deltaPosition = new Vector2D(x, y);
	}
	
	public void setDeltaDirection(double x, double y){
		this.deltaDirection = new Vector2D(x, y);
	}
	
	public void setDeltaPlane(double x, double y){
		this.deltaPlane = new Vector2D(x, y);
	}
	
	// ============DEBUG==============
    protected synchronized void moveForward() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos + xDir * moveSpeed), (int) yPos);
        Tile yTile = map.getTile((int) xPos, (int) (yPos + yDir * moveSpeed));
        xPos += xDir * moveSpeed * (1 - xTile.getSolidity());
        yPos += yDir * moveSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }

    protected synchronized void moveBackward() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos - xDir * moveSpeed), (int) yPos);
        Tile yTile = map.getTile((int) xPos, (int) (yPos - yDir * moveSpeed));
        xPos -= xDir * moveSpeed * (1 - xTile.getSolidity());
        yPos -= yDir * moveSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }

    protected synchronized void strafeLeft() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos - yDir * strafeSpeed), (int) (yPos));
        Tile yTile = map.getTile((int) (xPos), (int) (yPos + xDir * strafeSpeed));
        xPos -= yDir * strafeSpeed * (1 - xTile.getSolidity());
        yPos -= -xDir * strafeSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }

    protected synchronized void strafeRight() {
        double xPos = position.getX(), yPos = position.getY();
        double xDir = direction.getX(), yDir = direction.getY();
        Tile xTile = map.getTile((int) (xPos + yDir * strafeSpeed), (int) (yPos));
        Tile yTile = map.getTile((int) (xPos), (int) (yPos + -xDir * strafeSpeed));
        xPos += yDir * strafeSpeed * (1 - xTile.getSolidity());
        yPos += -xDir * strafeSpeed * (1 - yTile.getSolidity());
        position = new Vector2D(xPos, yPos);
    }

    /**
     * Rotation is simply multiplication by the rotation matrix. We take the
     * position and plane vectors, then multiply them by the rotation matrix
     * (whose parameter is ROTATION_SPEED). This lets us rotate.
     */
    protected synchronized void rotateAntiClockwise() {
        double xDir = direction.getX(), yDir = direction.getY();
        double oldxDir = xDir;
        xDir = xDir * Math.cos(rotationSpeed) - yDir * Math.sin(rotationSpeed);
        yDir = oldxDir * Math.sin(rotationSpeed) + yDir * Math.cos(rotationSpeed);
        direction = new Vector2D(xDir, yDir);
        plane = direction.perpendicular();
    }

    /**
     * Same as rotating left but clockwise this time.
     */
    protected synchronized void rotateClockwise() {
        double xDir = direction.getX(), yDir = direction.getY();
        double oldxDir = xDir;
        xDir = xDir * Math.cos(-rotationSpeed) - yDir * Math.sin(-rotationSpeed);
        yDir = oldxDir * Math.sin(-rotationSpeed) + yDir * Math.cos(-rotationSpeed);
        direction = new Vector2D(xDir, yDir);
        plane = direction.perpendicular();
    }
    
    public void setInputState(byte[] inputs) {
        synchronized (inputState) {
            for(int i=0;i<inputs.length;i=i+2) {
                // take the control
                // using client protocol
                // to fetch the order
                ClientController control = null;
                try {
                    control = ClientProtocol.getByIndex(inputs[i]);
                } catch (IOException e) {
                    System.err.println("Index not good... " + e.getMessage());
                }
                Byte value = inputs[i+1];
                inputState.put(control, value);
            }
        }
        synchronized (inputState) {
            // Check whether each input is triggered - if it is, execute the
            // respective method.
            // DEBUG
            for (Entry<ClientController, Byte> entry : inputState.entrySet()) {
            	// For boolean inputs (i.e. all current inputs), 0 represents
                // false.
                if (entry.getValue() != 0) {
                    ACTION_MAPPINGS.get(entry.getKey()).run();
                }
            }
        }

    }
    
    private void shoot() {
        if (currentPlayer.getCurrentWeapon() == null)
            return;

        if (currentPlayer.getCurrentWeapon().canFire()) {
            currentPlayer.setOnNextShot(true);
        }
    }
	
}
