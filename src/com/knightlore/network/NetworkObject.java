package com.knightlore.network;

import java.util.UUID;

import com.knightlore.engine.GameObject;
import com.knightlore.network.protocol.ServerCommand;

public abstract class NetworkObject extends GameObject{
	
	protected UUID objectUniqueID;
	
	public NetworkObject(UUID uuid){
		super();
		this.objectUniqueID = uuid;
        NetworkObjectManager.getSingleton().registerNetworkObject(this);
        System.out.println("netobject uuid " + objectUniqueID + " registered with manager");
	}
	
	public abstract byte[] serialize();
	
	public abstract void deserialize(ServerCommand packet);
	
	public synchronized UUID getObjectId(){
		return this.objectUniqueID;
	}
	
	@Override
	public void onCreate() {
	}
	
	
}
