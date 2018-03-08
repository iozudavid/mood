package com.knightlore.network.client;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.knightlore.game.Player;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.utils.Vector2D;

public class Prediction {

	// first double is the input timestemp
	private LinkedHashMap<Double, byte[]> clientInputHistory;
	private Player nextPrediction;
	private ByteBuffer lastReceivedFromServer;
	
	public Prediction(){
		this.clientInputHistory = new LinkedHashMap<>();
		this.nextPrediction = null;
		this.lastReceivedFromServer = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
	}

	// this will be called when a packet
	// is received from the server
	public Player onServerFrame(Player player, ByteBuffer received) {
		// remove the old history
		// inputs before this packet was sent
		if (!Arrays.equals(this.lastReceivedFromServer.array(), received.array())) {
			NetworkUtils.getStringFromBuf(received);
			NetworkUtils.getStringFromBuf(received);

			// get player's stats on server
			double size = received.getDouble();
			double xPos = received.getDouble();
			double yPos = received.getDouble();
			double xDir = received.getDouble();
			double yDir = received.getDouble();
			int shootOnNext = received.getInt();
			double timeSent = received.getDouble();

			//remove data inserted before this packet was sent
			Iterator<Map.Entry<Double, byte[]>> it = this.clientInputHistory.entrySet().iterator();
			while(it.hasNext()){
				Entry<Double, byte[]> next = it.next();
				if(next.getKey()<=timeSent){
					it.remove();
				}
			}
			//create a new player
			//which will predict the next steps
			if(this.nextPrediction==null){
				this.nextPrediction=new Player(Vector2D.ONE, Vector2D.ONE);
			}
			this.nextPrediction.setSize(size);
			this.nextPrediction.setxPos(xPos);
			this.nextPrediction.setyPos(yPos);
			this.nextPrediction.setxDir(xDir);
			this.nextPrediction.setyDir(yDir);
			this.nextPrediction.setOnNextShot(shootOnNext == 1 ? true : false);

			// predict again the player state
			// based on server packets
			// and all the inputs inserted after this packet was sent
			synchronized (this.clientInputHistory) {
				for (byte[] nextInput : this.clientInputHistory.values()) {
					this.nextPrediction.setInputState(nextInput);
				}
			}
			//use it as a start point
			//for the next prediction
			this.lastReceivedFromServer = received;
		}
		
		return player;

	}
	
	// called every client frame
	public Player update(Player player, byte[] input, double time) {
		//use last prediction based on server stats
		//to construct the new position
		if (this.nextPrediction!=null) {
			player.setxPos(this.nextPrediction.getxPos());
			player.setyPos(this.nextPrediction.getyPos());
			player.setxDir(this.nextPrediction.getxDir());
			player.setyDir(this.nextPrediction.getyDir());
		}
		player.setInputState(input);
		synchronized(this.clientInputHistory){
			this.clientInputHistory.put(time,input);
		}
		return player;
	}
	
}
