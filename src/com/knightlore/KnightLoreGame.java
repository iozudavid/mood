package com.knightlore;

import com.knightlore.engine.GameEngine;
import com.knightlore.network.client.ClientManager;

public class KnightLoreGame {

	public static void main(String[] args) {
		System.out.println("Starting Client...");
		GameEngine engine = new GameEngine();
		engine.start(true);
		ClientManager networkManager = new ClientManager();
		new Thread(networkManager).start();
		// TODO setup the client here
	}

}
