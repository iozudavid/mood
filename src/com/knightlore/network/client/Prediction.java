package com.knightlore.network.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import com.knightlore.game.Player;
import com.knightlore.utils.Vector2D;

public class Prediction {

	private ArrayList<PredictedState> history;
	private final int latency = 100;
	private PredictedState serverState;
	private PredictedState nextPrediction;
	
	public Prediction(){
		this.history = new ArrayList<>();
	}

	// this will be called when a packet
	// is received from the server
	public void onServerFrame(ByteBuffer received) {

		// now remove the frames from history
		// until its duration is equal to the latency
		System.out.println(this.history.size());
		double historyDuration = 0;
		if (!this.history.isEmpty())
			historyDuration = history.get(0).getTime();
		while(historyDuration>=100)
			historyDuration *= 0.1;
		double dt = Math.max(0, historyDuration - latency);
		Iterator<PredictedState> it = this.history.iterator();
		while (it.hasNext() && dt > 0) {
			PredictedState nextState = it.next();
			if (dt >= nextState.getTime()) {
				dt -= nextState.getTime();
				it.remove();
			} else {
				double t = (double) ((double)(dt - 1) / (double) nextState.getTime());
				nextState.setTime(nextState.getTime() - dt);
				nextState.setPosition(nextState.getPosition().getX() * t, nextState.getPosition().getY() * t);
				nextState.setDirection(nextState.getDirection().getX() * t, nextState.getDirection().getY() * t);
				nextState.setPlane(nextState.getPlane().getX() * t, nextState.getPlane().getY() * t);
				break;
			}
		}
		this.serverState = new PredictedState(received);
		// add deltas from history to server state
		// to get predicted state
		PredictedState nextPrediction = serverState;
		for (PredictedState historyPrediction : this.history) {
//			double deltaXPos = this.serverState.getPosition().getX() - historyPrediction.getPosition().getX();
//			double deltaYPos = this.serverState.getPosition().getY() - historyPrediction.getPosition().getY();
//			nextPrediction.setPosition(this.serverState.getPosition().getX() + deltaXPos,
//					this.serverState.getPosition().getY() + deltaYPos);
//			double deltaXDir = this.serverState.getDirection().getX() - historyPrediction.getDirection().getX();
//			double deltaYDir = this.serverState.getDirection().getY() - historyPrediction.getDirection().getY();
//			nextPrediction.setDirection(this.serverState.getDirection().getX() + deltaXDir,
//					this.serverState.getDirection().getY() + deltaYDir);
//			double deltaXPlane = this.serverState.getPlane().getX() - historyPrediction.getPlane().getX();
//			double deltaYPlane = this.serverState.getPlane().getY() - historyPrediction.getPlane().getY();
//			nextPrediction.setPlane(this.serverState.getPlane().getX() + deltaXPlane,
//					this.serverState.getPlane().getY() + deltaYPlane);
			double xPos = nextPrediction.getPosition().getX()+historyPrediction.getDeltaPosition().getX();
			double yPos = nextPrediction.getPosition().getY()+historyPrediction.getDeltaPosition().getY();
			nextPrediction.setPosition(xPos, yPos);
			double xDir = nextPrediction.getDirection().getX()+historyPrediction.getDeltaDirection().getX();
			double yDir = nextPrediction.getDirection().getY()+historyPrediction.getDeltaDirection().getY();
			nextPrediction.setDirection(xDir, yDir);
			double xPlane = nextPrediction.getPlane().getX()+historyPrediction.getDeltaPlane().getX();
			double yPlane = nextPrediction.getPlane().getY()+historyPrediction.getDeltaPlane().getY();
			nextPrediction.setPosition(xPlane, yPlane);
		}
		this.nextPrediction = nextPrediction;
	}
	
	// called every client frame
	public void update(Player player, ByteBuffer holdingState) {
	//	player.setInputState(holdingState);
		// delta frame
		PredictedState frame = new PredictedState(holdingState);
		if(this.nextPrediction == null)
			this.nextPrediction = new PredictedState(holdingState);
		frame.setDeltaPosition(frame.getPosition().getX() - this.nextPrediction.getPosition().getX(),
				frame.getPosition().getY() - this.nextPrediction.getPosition().getY());
		frame.setDeltaDirection(frame.getDirection().getX() - this.nextPrediction.getDirection().getX(),
				frame.getDirection().getY() - this.nextPrediction.getDirection().getY());
		frame.setDeltaPlane(frame.getPlane().getX() - this.nextPrediction.getPlane().getX(),
				frame.getPlane().getY() - this.nextPrediction.getPlane().getY());
		this.history.add(frame);
		//extrapolate predicted position
		//lower value of converge would be more agressive
		double converge = .05D;
		double extrapolatedXPos = this.nextPrediction.getPosition().getX()+.04*latency*converge;
		double extrapolatedYPos = this.nextPrediction.getPosition().getY()+.04*latency*converge;
		Vector2D extrapolatedPos = new Vector2D(extrapolatedXPos, extrapolatedYPos);
		double extrapolatedXDir = this.nextPrediction.getDirection().getX()+.025*latency*converge;
		double extrapolatedYDir = this.nextPrediction.getDirection().getY()+.025*latency*converge;
		Vector2D extrapolatedDir = new Vector2D(extrapolatedXDir, extrapolatedYDir);
		double extrapolatedXPlane = this.nextPrediction.getPlane().getX()+.01*latency*converge;
		double extrapolatedYPlane = this.nextPrediction.getPlane().getY()+.01*latency*converge;
		Vector2D extrapolatedPlane = new Vector2D(extrapolatedXPlane, extrapolatedYPlane);
		
		//interpolate
		double t = (double)((double)getTimeFormat()/(double)(latency * (1+converge)));
		System.out.println("=======================start===============================");
		System.out.println((double)((extrapolatedXPos-player.getxPos())*(double)t));
		System.out.println(((double)((extrapolatedYPos-player.getyPos())*(double)t)));
		System.out.println(((double)((extrapolatedXDir-player.getxDir())*(double)t)));
		System.out.println(((double)((extrapolatedYDir-player.getyDir())*(double)t)));
		System.out.println("=======================stop===============================");
//		player.setxPos((double)((extrapolatedXPos-player.getxPos())*(double)t)+20);
//		player.setyPos((double)((extrapolatedYPos-player.getyPos())*(double)t)+20);
//		player.setxDir((double)((extrapolatedXDir-player.getxDir())*(double)t)+20);
//		player.setyDir((double)((extrapolatedYDir-player.getyDir())*(double)t)+20);
//		player.setxPlane((double)((extrapolatedXPlane-player.getxPlane())*(double)t)+20);
//		player.setyPlane((double)((extrapolatedYPlane-player.getyPlane())*(double)t)+20);
		
		
	}
	
	public double getTimeFormat(){
		double time = System.currentTimeMillis();
		while(time>=100){
			time *= 0.1;
		}
		return time;
	}
	
	public static void main(String[] args){
		System.out.println(System.currentTimeMillis());
	}

}
