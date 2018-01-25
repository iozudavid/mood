package com.knightlore.render;

import java.awt.Color;

public enum Environment {
	DARK_OUTDOORS(50) {
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
	}, LIGHT_OUTDOORS(3) {
		@Override
		public void renderEnvironment(Screen screen) {

			final int skyClose = 0x7ec0ee, skyFar = 0xffffff;
			final int grassClose = 0x074A00, grassFar = 0xffffff;

			for (int yy = 0; yy < screen.getHeight() / 2; yy += 1) {
				int sky = screen.mixColor(skyClose, skyFar, yy / (double) (screen.getHeight()) / 2);
				int grass = screen.mixColor(grassClose, grassFar, yy / (double) (screen.getHeight()) / 2);
				for (int xx = 0; xx < screen.getWidth(); xx++) {
					screen.fillRect(sky, xx, yy, 1, 1);
					screen.fillRect(grass, xx, screen.getHeight() - yy, 1, 1);
				}
			}
		}
	}, DUNGEON(20) {
		@Override
		public void renderEnvironment(Screen screen) {
			screen.fillRect(0x000000, 0, 0, screen.getWidth(), screen.getHeight());
		}
	};

	private final int darkness;

	Environment(int darkness) {
		this.darkness = darkness;
	}

	public abstract void renderEnvironment(Screen screen);

	public int getDarkness() {
		return darkness;
	}

}
