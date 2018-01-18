package com.knightlore.render.environment;

import com.knightlore.render.Screen;

public class DungeonEnvironment implements IEnvironment {

	@Override
	public void renderEnvironment(Screen screen) {
		screen.fillRect(0x000000, 0, 0, screen.getWidth(), screen.getHeight());
	}

	@Override
	public int getDarkness() {
		return 20;
	}

}
