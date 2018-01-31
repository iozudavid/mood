package com.knightlore.network.protocol;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class ServerCommand {

	private long timeSent;
	private UUID playerId;
	private Map<ServerControl, Double> objectStats;
	
	public ServerCommand(long timeSent, UUID playerId, Map<ServerControl, Double> objectStats){
		this.timeSent = timeSent;
		this.playerId = playerId;
		this.objectStats = objectStats;
	}
	
	public long getTimeSent(){
		return this.timeSent;
	}
	
	public UUID getPlayerId(){
		return this.playerId;
	}
	
	public double getValueByControl(ServerControl aControl) throws IOException{
		if(!this.objectStats.containsKey(aControl))
			throw new IOException();
		return this.objectStats.get(aControl);
	}
	
}
