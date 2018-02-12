package com.knightlore;

public final class GameSettings {
	
	static boolean client = false;
	static boolean server = false;
	
	public static boolean isClient(){
		return client;
	}
	
	public static boolean isServer(){
		return server;
	}
}
