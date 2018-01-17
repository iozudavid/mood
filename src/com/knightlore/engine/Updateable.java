package com.knightlore.engine;

public interface Updateable {
	
	/**
	 * Updates the updateable.
	 * @param ticker the current ticker of the game engine.
	 */
	public void tick(long ticker);

}
