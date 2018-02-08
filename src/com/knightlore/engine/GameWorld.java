package com.knightlore.engine;

import com.knightlore.game.area.Map;

public abstract class GameWorld {

	// all worlds need a map
	protected Map map;
	
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
	
	public abstract GameWorld loadFromFile(String fileName);
	
	public abstract boolean saveToFile(String fileName);

	
	
}
