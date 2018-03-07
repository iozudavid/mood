package com.knightlore.network.client;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.knightlore.game.Player;
import com.knightlore.network.protocol.NetworkUtils;

public class Prediction {

	// first double is the input timestemp
	private CopyOnWriteArrayList<byte[]> clientInputHistory;
	private double sentToServer;
	private double receiveFromServer;
	
	public Prediction(){
		this.clientInputHistory = new CopyOnWriteArrayList<>();
		this.sentToServer=0;
		this.receiveFromServer=0;
	}

	// this will be called when a packet
	// is received from the server
	public Player onServerFrame(Player player, ByteBuffer received) {
		// remove the old history
		// inputs before this packet was sent
		this.receiveFromServer++;
		NetworkUtils.getStringFromBuf(received);
		NetworkUtils.getStringFromBuf(received);
		double delta = Math.max(0, this.sentToServer - this.receiveFromServer);
		synchronized (this.clientInputHistory) {
			Iterator<byte[]> bufferIterator = this.clientInputHistory.iterator();
			if (this.clientInputHistory.size() > 0 && delta == 0)
				this.clientInputHistory.remove(0);
			while (delta >= 0 && bufferIterator.hasNext()) {
				@SuppressWarnings("unused")
				byte[] b = bufferIterator.next();
				if (delta > 0)
					bufferIterator.remove();
				delta--;
			}
		}
		double size = received.getDouble();
		double xPos = received.getDouble();
		double yPos = received.getDouble();
		double xDir = received.getDouble();
		double yDir = received.getDouble();
		int shootOnNext = received.getInt();
		player.setSize(size);
		player.setxPos(xPos);
		player.setyPos(yPos);
		player.setxDir(xDir);
		player.setyDir(yDir);
		player.setOnNextShot(shootOnNext==1 ? true : false);
		
		//predict again the player state
		//based on server packets
		synchronized (this.clientInputHistory) {
			for (byte[] nextInput : this.clientInputHistory) {
				player.setInputState(nextInput);
			}
		}
		
		// return new player state
		// predicted using last packet received from server
		// in order to keep server reconciliation
		return player;
		
	}
	
	// called every client frame
	public Player update(Player player, byte[] input) {
		player.setInputState(input);
		synchronized(this.clientInputHistory){
			this.clientInputHistory.add(input);
		}
		this.sentToServer++;
		return player;
	}

}
