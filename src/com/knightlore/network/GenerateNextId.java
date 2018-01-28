package com.knightlore.network;

import java.util.UUID;

//this will ensure that the both connections
//on the server and client side
//will have the same id for client
public class GenerateNextId {
	private static int nextIdForServer = 0;
	private static int nextIdForClient = 0;
	
	//should be accessed only by server
	public static UUID forServer(){
		int currentID = nextIdForServer++;
		return(UUID.fromString(Integer.toString(currentID)));
	}
	
	//should be accessed only by client
	public static UUID forClient(){
		int currentID = nextIdForClient++;
		return(UUID.fromString(Integer.toString(currentID)));
	}
	
}
