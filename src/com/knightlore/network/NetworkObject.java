package com.knightlore.network;

import java.util.UUID;

import com.knightlore.engine.GameObject;
import com.knightlore.network.protocol.ServerCommand;

public abstract class NetworkObject extends GameObject{
	
	protected UUID objectUniqueID;
	
	public NetworkObject(){
		super();
		this.objectUniqueID = UUID.randomUUID();
	}
	
	public abstract byte[] serialize();
	
	public abstract ServerCommand deserialize(byte[] packet);
	
	public UUID getObjectId(){
		return this.objectUniqueID;
	}
	
	
	
}
