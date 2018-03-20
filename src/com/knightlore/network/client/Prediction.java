package com.knightlore.network.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.knightlore.game.Player;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.protocol.ClientController;
import com.knightlore.network.protocol.ClientProtocol;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.utils.Vector2D;

public class Prediction {

	// first double is the input timestemp
	private LinkedHashMap<Double, byte[]> clientInputHistory;
	private PredictedState nextPrediction;
	private ByteBuffer lastReceivedFromServer;
	private final double maxTolerance = 0.5D;
	private final double converge = 0.35D;
	
	public Prediction(){
		this.clientInputHistory = new LinkedHashMap<>();
		this.nextPrediction = null;
		this.lastReceivedFromServer = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
	}

	// this will be called when a packet
	// is received from the server
	public void onServerFrame(Player player, ByteBuffer received) {
		// remove the old history
		// inputs before this packet was sent
		received.position(0);
		NetworkUtils.getStringFromBuf(received);
		NetworkUtils.getStringFromBuf(received);

		// get player's stats on server
		double size = received.getDouble();
		player.setSize(size);
		double xPos = received.getDouble();
		double yPos = received.getDouble();
		double xDir = received.getDouble();
		double yDir = received.getDouble();
		// plane...
		received.getDouble();
		received.getDouble();
		int shootOnNext = received.getInt();
		player.setOnNextShot(shootOnNext == 1 ? true : false);
		double timeSent = received.getDouble();
		boolean respawn = (received.getInt()==1 ? true:false);
		// remove data inserted before this packet was sent
		if (!Arrays.equals(this.lastReceivedFromServer.array(), received.array())) {
			this.nextPrediction = new PredictedState(player, received);
			Iterator<Map.Entry<Double, byte[]>> it = this.clientInputHistory.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Double, byte[]> next = it.next();
				if (next.getKey() <= timeSent) {
					it.remove();
				}
			}
			// create a new player
			// which will predict the next steps

			this.nextPrediction.setPosition(xPos, yPos);
			this.nextPrediction.setDirection(xDir, yDir);
			this.nextPrediction.setOnShootNext(shootOnNext == 1 ? true : false);

			// predict again the player state
			// based on server packets
			// and all the inputs inserted after this packet was sent
	//		System.out.println("SIZE: " + this.clientInputHistory.size());
//			System.out.println("========DIFFERENCE========");
//			System.out.println(player.getxPos() + " -> " + this.nextPrediction.getPosition().getX());
//			System.out.println(player.getyPos() + " -> " + this.nextPrediction.getPosition().getY());
//			System.out.println(player.getxDir() + " -> " + this.nextPrediction.getDirection().getX());
//			System.out.println(player.getyDir() + " -> " + this.nextPrediction.getDirection().getY());
//			System.out.println("=========DIFFERENCE ENDED============");
			synchronized (this.clientInputHistory) {
				for (byte[] nextInput : this.clientInputHistory.values()) {
					this.nextPrediction.setInputState(nextInput);
				}
			}
			// use it as a start point
			// for the next prediction
			this.lastReceivedFromServer = received;
			if (respawn) {
				player.setxPos(nextPrediction.getPosition().getX());
				player.setyPos(nextPrediction.getPosition().getY());
				player.setxDir(nextPrediction.getDirection().getX());
				player.setyDir(nextPrediction.getDirection().getY());
			}
		}

	}
	
	// called every client frame
	public Player update(Player player, byte[] input, double packetNumber) {
		//use last prediction based on server stats
		//to construct the new position
		if (this.nextPrediction != null) {
			if ((Math.abs(player.getxPos() - this.nextPrediction.getPosition().getX()) > this.maxTolerance
					|| Math.abs(player.getyPos() - this.nextPrediction.getPosition().getY()) > this.maxTolerance
					|| Math.abs(player.getxDir() - this.nextPrediction.getDirection().getX()) > this.maxTolerance
					|| Math.abs(player.getyDir() - this.nextPrediction.getDirection().getY()) > this.maxTolerance)) {
				if(this.nextPrediction!=null){
				System.out.println("========DIFFERENCE========");
				System.out.println(player.getxPos() + " -> " + this.nextPrediction.getPosition().getX());
				System.out.println(player.getyPos() + " -> " + this.nextPrediction.getPosition().getY());
				System.out.println(player.getxDir() + " -> " + this.nextPrediction.getDirection().getX());
				System.out.println(player.getyDir() + " -> " + this.nextPrediction.getDirection().getY());
				}
				System.out.println("=========DIFFERENCE ENDED============");
				if (this.isMoveActivated(ClientController.FORWARD, input)
						|| this.isMoveActivated(ClientController.BACKWARD, input)
						|| this.isMoveActivated(ClientController.LEFT, input)
						|| this.isMoveActivated(ClientController.RIGHT, input))
					player.setxPos(nextPrediction.getPosition().getX());
				if (this.isMoveActivated(ClientController.FORWARD, input)
						|| this.isMoveActivated(ClientController.BACKWARD, input)
						|| this.isMoveActivated(ClientController.LEFT, input)
						|| this.isMoveActivated(ClientController.RIGHT, input))
					player.setyPos(nextPrediction.getPosition().getY());
				if (this.isMoveActivated(ClientController.ROTATE_ANTI_CLOCKWISE, input)
						|| this.isMoveActivated(ClientController.ROTATE_CLOCKWISE, input)
						|| this.isMoveActivated(ClientController.FORWARD, input)
						|| this.isMoveActivated(ClientController.BACKWARD, input)) {
					player.setxDir(nextPrediction.getDirection().getX());
					player.setyDir(nextPrediction.getDirection().getY());
				}
			} else {
				if (this.isMoveActivated(ClientController.FORWARD, input)
						|| this.isMoveActivated(ClientController.BACKWARD, input)
						|| this.isMoveActivated(ClientController.LEFT, input)
						|| this.isMoveActivated(ClientController.RIGHT, input))
					player.setxPos(smooth(player.getxPos(), this.nextPrediction.getPosition().getX()));
				if (this.isMoveActivated(ClientController.FORWARD, input)
						|| this.isMoveActivated(ClientController.BACKWARD, input)
						|| this.isMoveActivated(ClientController.LEFT, input)
						|| this.isMoveActivated(ClientController.RIGHT, input))
					player.setyPos(smooth(player.getyPos(), this.nextPrediction.getPosition().getY()));
				if (this.isMoveActivated(ClientController.ROTATE_ANTI_CLOCKWISE, input)
						|| this.isMoveActivated(ClientController.ROTATE_CLOCKWISE, input)
						|| this.isMoveActivated(ClientController.FORWARD, input)
						|| this.isMoveActivated(ClientController.BACKWARD, input)) {
					player.setxDir(smooth(player.getxDir(), this.nextPrediction.getDirection().getX()));
					player.setyDir(smooth(player.getyDir(), this.nextPrediction.getDirection().getY()));
				}	
			}
		}
		
		player.setInputState(input);
		synchronized(this.clientInputHistory){
			this.clientInputHistory.put(packetNumber,input);
		}
		return player;
	}
	
	public double smooth(double playerState, double predictedState){
		double delta = playerState-predictedState;
		return playerState-delta*this.converge;
	}
	
	public boolean isMoveActivated(ClientController c, byte[] input){
		for(int i=0; i<ClientProtocol.getIndexActionMap().size(); i=i+2){
			try {
				if(c==ClientProtocol.getByIndex(i)){
					if(input[i+1]==1)
						return true;
					else
						return false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
}
