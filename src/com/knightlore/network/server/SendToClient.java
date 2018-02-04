package com.knightlore.network.server;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.knightlore.game.Player;
import com.knightlore.network.Connection;
import com.knightlore.network.protocol.ServerProtocol;

public class SendToClient implements Runnable{
	
	private Connection conn;
	private Player player;
	private byte[] currentState;
	private byte[] lastState;
	private BlockingQueue<byte[]> commandQueue;
	
	public SendToClient(Connection conn, Player player){
		this.conn = conn;
		this.player = player;
		this.currentState = this.player.serialize();
		this.lastState = this.player.serialize();
		this.commandQueue = new LinkedBlockingQueue<>();
		new Thread() {
			public void run() {
				while (!conn.terminated) {
					try {
						currentState = player.serialize();
						if(areStatesDifferent(lastState, currentState)){
							commandQueue.offer(currentState);
							lastState = currentState;
						}
						Thread.sleep(100);
					} catch (InterruptedException e) {
						System.err.println("Thread interrupted...");
					}
				}
			}
		}.start();
	}
	
	public void run() {
	    //send first state
	    //to let the client know his id
		conn.send(this.currentState);
		while (!conn.terminated) {
			byte[] nextState;
			try {
				nextState = commandQueue.take();
				conn.send(nextState);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }

	}
	
	public boolean areStatesDifferent(byte[] lastState, byte[] currentState){
		byte[] lastStateWithoutTimeToCompare = Arrays.copyOfRange(lastState, ServerProtocol.METADATA_LENGTH, ServerProtocol.TOTAL_LENGTH);
		byte[] currentStateWithoutTimeToCompare = Arrays.copyOfRange(currentState, ServerProtocol.METADATA_LENGTH, ServerProtocol.TOTAL_LENGTH);
		if(lastStateWithoutTimeToCompare.length != currentStateWithoutTimeToCompare.length)
			return true;
		for(int i = 0; i<lastStateWithoutTimeToCompare.length;i++){
			if(lastStateWithoutTimeToCompare[i]!=currentStateWithoutTimeToCompare[i])
				return true;
		}
		return false;
		
	}
	
}
