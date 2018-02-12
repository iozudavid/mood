package com.knightlore;

import com.knightlore.utils.Vector2D;

public final class GameSettings {
	
	static boolean client = false;
	static boolean server = false;
	// FIXME please....
	public static Vector2D spawnPos;
	
	public static boolean isClient(){
		return client;
	}
	
	public static boolean isServer(){
		return server;
	}
}
