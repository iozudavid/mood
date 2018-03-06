package com.knightlore.network.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

public class Prediction {
	
	private ArrayList<PredictedState> history;
	private final int latency = 30;
	
	//this will be called when a packet
	//is received from the server
	public void onServerFrame(ByteBuffer received){
		
		//now remove the frames from history
		//until its duration is equal to the latency
		final double time = System.currentTimeMillis();
		double historyDuration = 0;
		if(!this.history.isEmpty())
			historyDuration = history.get(0).getTime();
		double dt = Math.max(0, historyDuration - latency);
		Iterator<PredictedState> it = this.history.iterator();		
		while(it.hasNext() && dt>0){
			PredictedState nextState = it.next();
			if(dt >= nextState.getTime()){
				dt -= nextState.getTime();
				it.remove();
			}else{
				double t = (double)((1-dt)/(double)nextState.getTime());
				nextState.setTime(nextState.getTime()-dt);
				nextState.setPosition(nextState.getPosition().getX()*t, nextState.getPosition().getY()*t);
				nextState.setDirection(nextState.getDirection().getX()*t, nextState.getDirection().getY()*t);
				nextState.setPlane(nextState.getPlane().getX()*t, nextState.getPlane().getY()*t);
				break;
			}
		}
	
	}
	
	
	
}
