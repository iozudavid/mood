package com.knightlore.render.environment;

import com.knightlore.render.Screen;

public class LightOutdoorsEnvironment implements IEnvironment {

	@Override
	public void renderEnvironment(Screen screen) {

		final int skyClose = 0x7ec0ee, skyFar = 0xffffff;
		final int grassClose = 0x00aa0b, grassFar = 0x59FE64;

		for (int yy = 0; yy < screen.getHeight() / 2; yy += 1) {
			int sky = screen.mixColor(skyClose, skyFar, yy / (double) (screen.getHeight()) / 2);
			int grass = screen.mixColor(grassFar, grassClose, yy / (double) (screen.getHeight()) / 2);
			for (int xx = 0; xx < screen.getWidth(); xx++) {
				screen.fillRect(sky, xx, yy, 1, 1);
				screen.fillRect(grass, xx, yy + screen.getHeight() / 2, 1, 1);
			}
		}
	}

	@Override
	public int getDarkness() {
		return 0;
	}

}
