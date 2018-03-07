package com.knightlore.network.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.knightlore.game.Player;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.utils.Vector2D;

public class Prediction {

	private ArrayList<PredictedState> history;
	private Player player;
	
	public Prediction(){
		this.history = new ArrayList<>();
	}

	// this will be called when a packet
	// is received from the server
	public void onServerFrame(Player player, ByteBuffer received) {

		if(this.history.size()>30){
			this.history.remove(0);
		}
		NetworkUtils.getStringFromBuf(received);
		NetworkUtils.getStringFromBuf(received);
		double size = received.getDouble();
		double xPos = received.getDouble();
		double yPos = received.getDouble();
		double xDir = received.getDouble();
		double yDir = received.getDouble();
		double xPlane = received.getDouble();
		double yPlane = received.getDouble();
		int shootOnNext = received.getInt();
	//	if(player.getxPos()
		
	}
	
	// called every client frame
	public void update(Player player) {
		if(this.history.size()>30){
			this.history.remove(0);
		}
		PredictedState ps = new PredictedState(player.serialize());
		this.history.add(ps);
	}
	
	public double getTimeFormat(){
		double time = System.currentTimeMillis();
		while(time>=100){
			time *= 0.1;
		}
		return time;
	}
	
	public Vector2D getPosition(){
		return this.player.getPosition();
	}
	
	public Vector2D getDirection(){
		return this.player.getDirection();
	}
	
	public Vector2D getPlane(){
		return this.player.getPlane();
	}

}
