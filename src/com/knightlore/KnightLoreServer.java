package com.knightlore;

import com.knightlore.engine.GameEngine;

public class KnightLoreServer {

	public static void main(String[] args) {
		System.out.println("Starting Server...");
		GameEngine engine = new GameEngine();
		engine.start();
		// TODO setup the server here
	}

}
