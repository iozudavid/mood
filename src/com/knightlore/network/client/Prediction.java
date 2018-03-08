package com.knightlore.network.client;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.knightlore.game.Player;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.utils.Vector2D;

public class Prediction {

	// first double is the input timestemp
	private CopyOnWriteArrayList<byte[]> clientInputHistory;
	private double sentToServer;
	private double receiveFromServer;
	private Vector2D actualClientPos;
	private Vector2D actualClientDir;
	private final double maxTolerance = 0.2;
	private Player auxPlayer;
	
	public Prediction(){
		this.clientInputHistory = new CopyOnWriteArrayList<>();
		this.sentToServer=0;
		this.receiveFromServer=0;
		this.auxPlayer = new Player(Vector2D.ONE, Vector2D.ONE);
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
		this.auxPlayer.setSize(size);
		this.auxPlayer.setxPos(xPos);
		this.auxPlayer.setyPos(yPos);
		this.auxPlayer.setxDir(xDir);
		this.auxPlayer.setyDir(yDir);
		this.auxPlayer.setOnNextShot(shootOnNext==1 ? true : false);
		
		//predict again the player state
		//based on server packets
		synchronized (this.clientInputHistory) {
			for (byte[] nextInput : this.clientInputHistory) {
				this.auxPlayer.setInputState(nextInput);
			}
		}
		
		// return new player state
		// predicted using last packet received from server
		// in order to keep server reconciliation
		if(this.actualClientPos!= null && this.actualClientDir!=null &&
				(Math.abs(this.auxPlayer.getxPos()-this.actualClientPos.getX())>this.maxTolerance ||
					Math.abs(this.auxPlayer.getyPos()-this.actualClientPos.getY())>this.maxTolerance ||
						Math.abs(this.auxPlayer.getxDir()-this.actualClientDir.getX())>this.maxTolerance ||
							Math.abs(this.auxPlayer.getyDir()-this.actualClientDir.getY())>this.maxTolerance)){
			player.setxPos(this.auxPlayer.getxPos());
			player.setyPos(this.auxPlayer.getyPos());
			player.setxDir(this.auxPlayer.getxDir());
			player.setyDir(this.auxPlayer.getyDir());
		}
		return player;
		
	}
	
	// called every client frame
	public Player update(Player player, byte[] input) {
		player.setInputState(input);
		synchronized(this.clientInputHistory){
			this.clientInputHistory.add(input);
		}
		this.sentToServer++;
		this.actualClientPos = player.getPosition();
		this.actualClientDir = player.getDirection();
		return player;
	}
	
}
