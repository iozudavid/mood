package com.knightlore.network.protocol;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//this class contains the protocol
//of how data is encapsulating in a packet
//by the client
public final class ClientProtocol {
	
	//position in the byte array - key map
	//convention of the client
	//to create the packet
	private static final Map<Integer, ClientControl> indexAction;
	static
    {
        indexAction = new HashMap<Integer, ClientControl>();
        indexAction.put(0, ClientControl.FORWARD);
        indexAction.put(1, ClientControl.LEFT);
        indexAction.put(2, ClientControl.BACKWARD);
        indexAction.put(3, ClientControl.RIGHT);
        //just this for now
    }
	
	//get the key by passing index in the array
	public static ClientControl getByIndex(int i) throws IOException{
		if(!indexAction.containsKey(i))
			throw new IOException();
		return indexAction.get(i);
	}
	
	//get the index by passing the key
	public static int getByKey(ClientControl key) throws IOException{
		if(!indexAction.containsValue(key))
			throw new IOException();
		for(int i: indexAction.keySet()){
			if(indexAction.get(i)==key)
				return i;
		}
		return -1;
	}
	
	public static Map<Integer, ClientControl> getIndexActionMap(){
		return indexAction;
	}
	
}
