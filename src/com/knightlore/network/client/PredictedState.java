package com.knightlore.network.client;

import java.nio.ByteBuffer;

import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.utils.Vector2D;

public class PredictedState {
	
	private ByteBuffer currentState;
	private String uuid;
	private Vector2D position;
	private Vector2D direction;
	private Vector2D plane;
	private boolean shootOnNext;
	private double time;
	
	public PredictedState(ByteBuffer b){
		this.currentState = b;
		depack();
	}
	
	private void depack(){
		this.time = System.currentTimeMillis();
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
	
	public boolean getShootAbility(){
		return this.shootOnNext;
	}
	
	public double getTime(){
		return this.time;
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
	
}
