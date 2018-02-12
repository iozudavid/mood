package com.knightlore.network;

import java.util.UUID;

import com.knightlore.engine.GameObject;
import com.knightlore.network.protocol.ServerCommand;
import com.knightlore.utils.Vector2D;

public abstract class NetworkObject extends GameObject{
	
	protected UUID objectUniqueID;
	
	public NetworkObject(UUID uuid) {
	    this(uuid, Vector2D.ZERO);
	}
	
	public NetworkObject(UUID uuid, Vector2D position){
		super(position);
		this.objectUniqueID = uuid;
        NetworkObjectManager.getSingleton().registerNetworkObject(this);
        System.out.println("netobject uuid " + objectUniqueID + " registered with manager");
	}
	
	//the boolean variable will be used to know which type of state
	//we want to obtain
	//state = the resulted byte array
	//if disconnect == true  : the state will be a disconnect state for this network object
	//                         the protocol for disconnect state will be found in server protocols
	//if disconnect == false : the state will be the actual state of the client
	public abstract byte[] serialize(boolean disconnect);
	
	public abstract void deserialize(ServerCommand packet);
	
	public synchronized UUID getObjectId(){
		return this.objectUniqueID;
	}
	
}
