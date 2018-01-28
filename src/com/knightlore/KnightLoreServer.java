package com.knightlore;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.server.ServerManager;

public class KnightLoreServer {

	public static void main(String[] args) {
		System.out.println("Starting Server...");
		GameEngine engine = new GameEngine();
		engine.start();
		ServerManager networkManager = new ServerManager();
		// TODO setup the server here
	}

}
