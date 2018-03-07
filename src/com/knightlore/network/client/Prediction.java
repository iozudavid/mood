package com.knightlore.network.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import com.knightlore.game.Player;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.protocol.ClientProtocol;
import com.knightlore.network.protocol.NetworkUtils;

public class Prediction {

	// first double is the input timestemp
	private ArrayList<ByteBuffer> clientInputHistory;
	private double nextPacketNoToSend;
	
	public Prediction(){
		this.clientInputHistory = new ArrayList<>();
		this.nextPacketNoToSend++;
	}

	// this will be called when a packet
	// is received from the server
	public Player onServerFrame(Player player, ByteBuffer received) {
		// remove the old history
		// inputs before this packet was sent
		double timeStemp = received.getDouble();
		Iterator<ByteBuffer> bufferIterator = this.clientInputHistory.iterator();
		while(bufferIterator.hasNext()){
			ByteBuffer b = bufferIterator.next();
			double time = b.getDouble();
			b.rewind();
			if(time<=timeStemp)
				bufferIterator.remove();
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
		player.setSize(size);
		player.setxPos(xPos);
		player.setyPos(yPos);
		player.setxDir(xDir);
		player.setyDir(yDir);
		player.setxPlane(xPlane);
		player.setyPlane(yPlane);
		player.setOnNextShot(shootOnNext==1 ? true : false);
		
		//predict again the player state
		//based on server packets
		for(ByteBuffer nextInput : this.clientInputHistory)
			player.setInputState(nextInput);
		
		// return new player state
		// predicted using last packet received from server
		// in order to keep server reconciliation
		return player;
		
	}
	
	// called every client frame
	public Player update(Player player, ByteBuffer input) {
		player.setInputState(input);
		ByteBuffer inputWithNo = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
		inputWithNo.putDouble(this.nextPacketNoToSend);
		this.nextPacketNoToSend++;
		for(int i=0; i<ClientProtocol.getIndexActionMap().size();i++){
			inputWithNo.putInt(input.getInt());
			inputWithNo.put(input.get());
		}
		inputWithNo.rewind();
		this.clientInputHistory.add(inputWithNo);
		return player;
	}

}
