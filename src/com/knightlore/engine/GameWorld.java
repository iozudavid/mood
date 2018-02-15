package com.knightlore.engine;

import com.knightlore.game.area.Map;

public abstract class GameWorld {

	// all worlds need a map
	protected static Map map;
	
	public static Map getMap(){
		return map;
	}
	
	/**
	 * Called by the engine to initialise the world.
	 * This should be used to link the world into the managers
	 */
	public abstract void initWorld();
	
	/**
	 * Called by the engine after the initWorld()
	 * This should be used to populate the world with game objects
	 */
	public abstract void populateWorld();
	
	/**
	 * Called every time the game updates.
	 * This should be used to update non gameObject parts of the world.
	 * GameObjects will be updated separately.
	 */
	public abstract void updateWorld();
	
	public abstract GameWorld loadFromFile(String fileName);
	
	public abstract boolean saveToFile(String fileName);


	
	
}
