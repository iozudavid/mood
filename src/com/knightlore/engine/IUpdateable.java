package com.knightlore.engine;

public interface IUpdateable {
	
	/**
	 * Updates the updateable.
	 * @param ticker the current ticker of the game engine.
	 */
	void tick(long ticker);

}
