package com.knightlore.render.environment;

import com.knightlore.render.Screen;

public class LightOutdoorsEnvironment implements IEnvironment {

	@Override
	public void renderEnvironment(Screen screen) {
		screen.fillRect(0x0000ff, 0, 0, screen.getWidth(), screen.getHeight() / 2);
		screen.fillRect(0x4dbd33, 0, screen.getHeight() / 2, screen.getWidth(), screen.getHeight() / 2);
	}

	@Override
	public int getDarkness() {
		return 0;
	}

}
