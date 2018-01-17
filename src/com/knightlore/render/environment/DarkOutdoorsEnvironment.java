package com.knightlore.render.environment;

import java.awt.Color;

import com.knightlore.render.Screen;

public class DarkOutdoorsEnvironment implements IEnvironment {

	@Override
	public void renderEnvironment(Screen screen) {
		for (int yy = screen.getHeight() / 2; yy < screen.getHeight(); yy += 1) {
			for (int xx = 0; xx < screen.getWidth(); xx++) {
				int n = Math.max(0, Math.min(yy * yy / 5000, 255));
				Color c = new Color(n, n, n);
				screen.fillRect(c.getRGB(), xx, yy, 1, 1);
				screen.fillRect(c.getRGB(), xx, -screen.getHeight() / 2 + yy, 1, 1);
			}
		}
	}

	@Override
	public int getDarkness() {
		return 50;
	}

}
